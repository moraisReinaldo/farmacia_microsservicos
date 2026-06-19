package com.farmacia.venda.service;

import com.farmacia.common.exception.ResourceNotFoundException;
import com.farmacia.venda.dto.VendaRequest;
import com.farmacia.venda.model.ItemVenda;
import com.farmacia.venda.model.Venda;
import com.farmacia.venda.model.Vendedor;
import com.farmacia.venda.repository.VendaRepository;
import com.farmacia.venda.repository.VendedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Serviço de Vendas - V1 (REST síncrono).
 * Chama os demais microsserviços via RestTemplate.
 * Fluxo: valida produto → valida cliente → calcula desconto → debita estoque → emite NF-e → registra receita
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final VendedorRepository vendedorRepository;
    private final RestTemplate restTemplate;

    @Value("${microservicos.produto-service.url:http://localhost:8081}")
    private String produtoServiceUrl;

    @Value("${microservicos.cliente-service.url:http://localhost:8082}")
    private String clienteServiceUrl;

    @Value("${microservicos.estoque-service.url:http://localhost:8084}")
    private String estoqueServiceUrl;

    @Value("${microservicos.nota-fiscal-service.url:http://localhost:8085}")
    private String notaFiscalServiceUrl;

    @Value("${microservicos.desconto-service.url:http://localhost:8087}")
    private String descontoServiceUrl;

    @Value("${microservicos.receita-service.url:http://localhost:8086}")
    private String receitaServiceUrl;

    /**
     * Processa uma venda completa (V1 - chamadas REST síncronas).
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> processarVenda(VendaRequest request) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        log.info("Processando venda tipo: {}", request.getTipoVenda());

        // 1. Buscar dados dos produtos e validar
        BigDecimal subtotal = BigDecimal.ZERO;
        List<ItemVenda> itensVenda = new ArrayList<>();
        boolean temControlado = false;

        for (VendaRequest.ItemRequest item : request.getItens()) {
            try {
                Map<String, Object> resposta = restTemplate.getForObject(
                        produtoServiceUrl + "/api/produtos/" + item.getProdutoId(), Map.class);
                Map<String, Object> produto = (Map<String, Object>) resposta.get("dados");

                BigDecimal preco = new BigDecimal(produto.get("preco").toString());
                BigDecimal subtotalItem = preco.multiply(BigDecimal.valueOf(item.getQuantidade()));

                ItemVenda itemVenda = new ItemVenda();
                itemVenda.setProdutoId(item.getProdutoId());
                itemVenda.setQuantidade(item.getQuantidade());
                itemVenda.setPrecoUnitario(preco);
                itemVenda.setSubtotal(subtotalItem);
                itensVenda.add(itemVenda);

                subtotal = subtotal.add(subtotalItem);

                if (Boolean.TRUE.equals(produto.get("controlado"))) {
                    temControlado = true;
                }
            } catch (Exception e) {
                log.error("Erro ao buscar produto {}: {}", item.getProdutoId(), e.getMessage());
                throw new RuntimeException("Produto não encontrado: " + item.getProdutoId());
            }
        }

        // 2. Se tem controlado, exige dados do cliente
        if (temControlado && request.getClienteId() == null) {
            throw new RuntimeException("Medicamento controlado exige dados do cliente cadastrado");
        }

        // 3. Calcular desconto (chamar desconto-service)
        BigDecimal desconto = BigDecimal.ZERO;
        if (request.getClienteId() != null) {
            try {
                Map<String, Object> descontoReq = new HashMap<>();
                descontoReq.put("clienteId", request.getClienteId());
                descontoReq.put("subtotal", subtotal);
                Map<String, Object> descontoResp = restTemplate.postForObject(
                        descontoServiceUrl + "/api/descontos/calcular", descontoReq, Map.class);
                if (descontoResp != null && descontoResp.get("dados") != null) {
                    Map<String, Object> descontoDados = (Map<String, Object>) descontoResp.get("dados");
                    desconto = new BigDecimal(descontoDados.get("valorDesconto").toString());
                }
            } catch (Exception e) {
                log.warn("Erro ao calcular desconto: {}", e.getMessage());
            }
        }

        BigDecimal total = subtotal.subtract(desconto).max(BigDecimal.ZERO);

        // 4. Calcular comissão do vendedor
        BigDecimal comissao = BigDecimal.ZERO;
        if (request.getVendedorId() != null) {
            Vendedor vendedor = vendedorRepository.findById(request.getVendedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendedor", request.getVendedorId()));
            comissao = total.multiply(vendedor.getPercentualComissao())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        // 5. Criar e salvar a venda
        Venda venda = new Venda();
        venda.setClienteId(request.getClienteId());
        venda.setVendedorId(request.getVendedorId());
        venda.setCpfNota(request.getCpfNota());
        venda.setTipoVenda(Venda.TipoVenda.valueOf(request.getTipoVenda()));
        venda.setSubtotal(subtotal);
        venda.setDesconto(desconto);
        venda.setTotal(total);
        venda.setComissao(comissao);
        venda.setStatus(Venda.StatusVenda.CONCLUIDA);

        // Associar itens à venda
        for (ItemVenda item : itensVenda) {
            item.setVenda(venda);
        }
        venda.setItens(itensVenda);

        venda = vendaRepository.save(venda);
        resultado.put("venda", venda);

        // 6. Debitar estoque (chamar estoque-service)
        for (VendaRequest.ItemRequest item : request.getItens()) {
            try {
                Map<String, Object> saidaReq = new HashMap<>();
                saidaReq.put("produtoId", item.getProdutoId());
                saidaReq.put("quantidade", item.getQuantidade());
                saidaReq.put("vendaId", venda.getId());
                saidaReq.put("motivo", "Venda #" + venda.getId());
                restTemplate.postForObject(estoqueServiceUrl + "/api/estoque/saida", saidaReq, Map.class);
            } catch (Exception e) {
                log.warn("Erro ao debitar estoque do produto {}: {}", item.getProdutoId(), e.getMessage());
            }
        }

        // 7. Emitir NF-e (chamar nota-fiscal-service)
        try {
            Map<String, Object> nfReq = new HashMap<>();
            nfReq.put("vendaId", venda.getId());
            nfReq.put("cpfDestinatario", request.getCpfNota());
            nfReq.put("valorTotal", total);
            Map<String, Object> nfResp = restTemplate.postForObject(
                    notaFiscalServiceUrl + "/api/notas-fiscais", nfReq, Map.class);
            resultado.put("notaFiscal", nfResp != null ? nfResp.get("dados") : null);
        } catch (Exception e) {
            log.warn("Erro ao emitir NF-e: {}", e.getMessage());
        }

        // 8. Se medicamento controlado, registrar receita (chamar receita-service)
        if (temControlado) {
            try {
                Map<String, Object> receitaReq = new HashMap<>();
                receitaReq.put("vendaId", venda.getId());
                receitaReq.put("clienteCpf", request.getCpfNota());
                receitaReq.put("produtoId", request.getItens().get(0).getProdutoId());
                receitaReq.put("numeroReceita", request.getNumeroReceita());
                receitaReq.put("crmMedico", request.getCrmMedico());
                receitaReq.put("nomeMedico", request.getNomeMedico());
                Map<String, Object> recResp = restTemplate.postForObject(
                        receitaServiceUrl + "/api/receitas", receitaReq, Map.class);
                resultado.put("receita", recResp != null ? recResp.get("dados") : null);
            } catch (Exception e) {
                log.warn("Erro ao registrar receita: {}", e.getMessage());
            }
        }

        // 9. Incrementar compras do cliente
        if (request.getClienteId() != null) {
            try {
                restTemplate.patchForObject(
                        clienteServiceUrl + "/api/clientes/" + request.getClienteId() + "/incrementar-compras",
                        null, Map.class);
            } catch (Exception e) {
                log.warn("Erro ao incrementar compras: {}", e.getMessage());
            }
        }

        resultado.put("status", "SUCESSO");
        resultado.put("mensagem", "Venda processada com sucesso");
        return resultado;
    }

    public List<Venda> listarTodas() {
        return vendaRepository.findAll();
    }

    public Venda buscarPorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda", id));
    }

    public List<Venda> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return vendaRepository.findByPeriodo(inicio, fim);
    }

    public List<Venda> buscarPorCliente(Long clienteId) {
        return vendaRepository.findByClienteId(clienteId);
    }

    // ============ VENDEDORES ============

    public List<Vendedor> listarVendedores() {
        return vendedorRepository.findByAtivoTrue();
    }

    public Vendedor salvarVendedor(Vendedor vendedor) {
        return vendedorRepository.save(vendedor);
    }

    public Vendedor buscarVendedorPorId(Long id) {
        return vendedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor", id));
    }

    /**
     * Calcula comissões de um vendedor em um período.
     */
    public Map<String, Object> calcularComissoes(Long vendedorId, LocalDateTime inicio, LocalDateTime fim) {
        Vendedor vendedor = buscarVendedorPorId(vendedorId);
        List<Venda> vendas = vendaRepository.findByVendedorIdAndPeriodo(vendedorId, inicio, fim);

        BigDecimal totalVendas = vendas.stream()
                .map(Venda::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComissoes = vendas.stream()
                .map(Venda::getComissao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("vendedor", vendedor.getNome());
        resultado.put("periodo", Map.of("inicio", inicio, "fim", fim));
        resultado.put("quantidadeVendas", vendas.size());
        resultado.put("totalVendas", totalVendas);
        resultado.put("totalComissoes", totalComissoes);
        resultado.put("percentualComissao", vendedor.getPercentualComissao());
        return resultado;
    }
}
