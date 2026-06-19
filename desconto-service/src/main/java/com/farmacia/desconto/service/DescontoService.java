package com.farmacia.desconto.service;

import com.farmacia.common.exception.ResourceNotFoundException;
import com.farmacia.desconto.model.RegraDesconto;
import com.farmacia.desconto.repository.DescontoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Motor de regras de desconto.
 * Implementa:
 * - Desconto progressivo baseado no total de compras do cliente
 * - Desconto para idosos com convênio médico
 * - Desconto do fabricante para idosos
 * - Cada farmácia pode definir suas próprias regras
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DescontoService {

    private final DescontoRepository repository;
    private final RestTemplate restTemplate;

    @Value("${microservicos.cliente-service.url:http://localhost:8082}")
    private String clienteServiceUrl;

    /**
     * Calcula o melhor desconto aplicável para um cliente.
     * Regra: idoso com convênio recebe o MAIOR entre desconto de convênio e de fabricante.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> calcularDesconto(Long clienteId, BigDecimal subtotal) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("clienteId", clienteId);
        resultado.put("subtotal", subtotal);

        // Buscar dados do cliente
        Map<String, Object> clienteDados;
        try {
            Map<String, Object> resposta = restTemplate.getForObject(
                    clienteServiceUrl + "/api/clientes/" + clienteId, Map.class);
            clienteDados = (Map<String, Object>) resposta.get("dados");
        } catch (Exception e) {
            log.warn("Erro ao buscar cliente {}: {}", clienteId, e.getMessage());
            resultado.put("valorDesconto", BigDecimal.ZERO);
            resultado.put("regraAplicada", "Nenhuma");
            return resultado;
        }

        Integer totalCompras = clienteDados.get("totalCompras") != null ?
                Integer.valueOf(clienteDados.get("totalCompras").toString()) : 0;
        Boolean convenioMedico = Boolean.TRUE.equals(clienteDados.get("convenioMedico"));

        // Calcular idade para verificar se é idoso
        boolean isIdoso = false;
        if (clienteDados.get("dataNascimento") != null) {
            try {
                String dataNasc = clienteDados.get("dataNascimento").toString();
                java.time.LocalDate nascimento = java.time.LocalDate.parse(dataNasc.length() > 10 ? dataNasc.substring(0, 10) : dataNasc);
                isIdoso = java.time.Period.between(nascimento, java.time.LocalDate.now()).getYears() >= 60;
            } catch (Exception e) {
                log.warn("Erro ao calcular idade: {}", e.getMessage());
            }
        }

        List<RegraDesconto> regrasAtivas = repository.findByAtivoTrue();
        BigDecimal melhorDesconto = BigDecimal.ZERO;
        String melhorRegra = "Nenhuma";

        for (RegraDesconto regra : regrasAtivas) {
            BigDecimal valorDesconto = BigDecimal.ZERO;

            switch (regra.getTipo()) {
                case PROGRESSIVO:
                    // Desconto progressivo baseado no total de compras
                    if (totalCompras >= regra.getMinCompras() &&
                            (regra.getMaxCompras() == null || totalCompras <= regra.getMaxCompras())) {
                        valorDesconto = subtotal.multiply(regra.getValorPercentual())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    }
                    break;

                case CONVENIO:
                    // Desconto de convênio: apenas para idosos com convênio
                    if (isIdoso && convenioMedico) {
                        valorDesconto = subtotal.multiply(regra.getValorPercentual())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    }
                    break;

                case FABRICANTE:
                    // Desconto do fabricante: para idosos
                    if (isIdoso) {
                        valorDesconto = subtotal.multiply(regra.getValorPercentual())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    }
                    break;

                case IDOSO:
                    if (isIdoso) {
                        valorDesconto = subtotal.multiply(regra.getValorPercentual())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    }
                    break;
            }

            // Aplicar o MAIOR desconto encontrado
            if (valorDesconto.compareTo(melhorDesconto) > 0) {
                melhorDesconto = valorDesconto;
                melhorRegra = regra.getNome() + " (" + regra.getTipo() + " - " + regra.getValorPercentual() + "%)";
            }
        }

        resultado.put("valorDesconto", melhorDesconto);
        resultado.put("regraAplicada", melhorRegra);
        resultado.put("isIdoso", isIdoso);
        resultado.put("temConvenio", convenioMedico);
        resultado.put("totalCompras", totalCompras);

        log.info("Desconto calculado para cliente {}: {} ({})", clienteId, melhorDesconto, melhorRegra);
        return resultado;
    }

    public List<RegraDesconto> listarRegras() {
        return repository.findByAtivoTrue();
    }

    public RegraDesconto salvarRegra(RegraDesconto regra) {
        return repository.save(regra);
    }

    public RegraDesconto atualizarRegra(Long id, RegraDesconto dadosAtualizados) {
        RegraDesconto regra = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regra de Desconto", id));
        regra.setNome(dadosAtualizados.getNome());
        regra.setDescricao(dadosAtualizados.getDescricao());
        regra.setTipo(dadosAtualizados.getTipo());
        regra.setValorPercentual(dadosAtualizados.getValorPercentual());
        regra.setMinCompras(dadosAtualizados.getMinCompras());
        regra.setMaxCompras(dadosAtualizados.getMaxCompras());
        regra.setApenasIdosos(dadosAtualizados.getApenasIdosos());
        regra.setRequerConvenio(dadosAtualizados.getRequerConvenio());
        return repository.save(regra);
    }

    public void desativarRegra(Long id) {
        RegraDesconto regra = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regra de Desconto", id));
        regra.setAtivo(false);
        repository.save(regra);
    }
}
