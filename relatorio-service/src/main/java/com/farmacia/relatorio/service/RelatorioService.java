package com.farmacia.relatorio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Serviço de Relatórios Gerenciais.
 * Agrega dados de todos os microsserviços via REST.
 * Relatórios: vendas por período, produtos mais vendidos, estoque, comissões, faturamento.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final RestTemplate restTemplate;

    @Value("${microservicos.venda-service.url:http://localhost:8083}")
    private String vendaServiceUrl;

    @Value("${microservicos.produto-service.url:http://localhost:8081}")
    private String produtoServiceUrl;

    @Value("${microservicos.estoque-service.url:http://localhost:8084}")
    private String estoqueServiceUrl;

    /**
     * Relatório de vendas por período.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> relatorioVendas(String inicio, String fim) {
        Map<String, Object> relatorio = new LinkedHashMap<>();
        relatorio.put("tipo", "VENDAS_POR_PERIODO");
        relatorio.put("periodo", Map.of("inicio", inicio, "fim", fim));

        try {
            Map<String, Object> resposta = restTemplate.getForObject(
                    vendaServiceUrl + "/api/vendas/periodo?inicio=" + inicio + "&fim=" + fim, Map.class);
            List<Map<String, Object>> vendas = (List<Map<String, Object>>) resposta.get("dados");

            double totalFaturado = vendas.stream()
                    .mapToDouble(v -> Double.parseDouble(v.get("total").toString()))
                    .sum();
            double totalDescontos = vendas.stream()
                    .mapToDouble(v -> Double.parseDouble(v.get("desconto").toString()))
                    .sum();
            double totalComissoes = vendas.stream()
                    .mapToDouble(v -> Double.parseDouble(v.get("comissao").toString()))
                    .sum();

            // Contagem por tipo de venda
            Map<String, Long> vendasPorTipo = new LinkedHashMap<>();
            for (Map<String, Object> v : vendas) {
                String tipo = v.get("tipoVenda").toString();
                vendasPorTipo.merge(tipo, 1L, Long::sum);
            }

            relatorio.put("totalVendas", vendas.size());
            relatorio.put("totalFaturado", String.format("%.2f", totalFaturado));
            relatorio.put("totalDescontos", String.format("%.2f", totalDescontos));
            relatorio.put("totalComissoes", String.format("%.2f", totalComissoes));
            relatorio.put("vendasPorTipo", vendasPorTipo);
            relatorio.put("vendas", vendas);
        } catch (Exception e) {
            log.error("Erro ao gerar relatório de vendas: {}", e.getMessage());
            relatorio.put("erro", "Não foi possível obter dados de vendas");
        }

        return relatorio;
    }

    /**
     * Relatório de produtos mais vendidos.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> relatorioProdutosMaisVendidos(String inicio, String fim) {
        Map<String, Object> relatorio = new LinkedHashMap<>();
        relatorio.put("tipo", "PRODUTOS_MAIS_VENDIDOS");

        try {
            Map<String, Object> resposta = restTemplate.getForObject(
                    vendaServiceUrl + "/api/vendas/periodo?inicio=" + inicio + "&fim=" + fim, Map.class);
            List<Map<String, Object>> vendas = (List<Map<String, Object>>) resposta.get("dados");

            // Contar itens vendidos por produto
            Map<Long, Integer> contagemProdutos = new HashMap<>();
            for (Map<String, Object> venda : vendas) {
                List<Map<String, Object>> itens = (List<Map<String, Object>>) venda.get("itens");
                if (itens != null) {
                    for (Map<String, Object> item : itens) {
                        Long produtoId = Long.valueOf(item.get("produtoId").toString());
                        Integer qtd = Integer.valueOf(item.get("quantidade").toString());
                        contagemProdutos.merge(produtoId, qtd, Integer::sum);
                    }
                }
            }

            // Ordenar por quantidade vendida (desc)
            List<Map<String, Object>> ranking = new ArrayList<>();
            contagemProdutos.entrySet().stream()
                    .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                    .limit(20)
                    .forEach(entry -> {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("produtoId", entry.getKey());
                        item.put("quantidadeVendida", entry.getValue());

                        // Buscar nome do produto
                        try {
                            Map<String, Object> prodResp = restTemplate.getForObject(
                                    produtoServiceUrl + "/api/produtos/" + entry.getKey(), Map.class);
                            Map<String, Object> prod = (Map<String, Object>) prodResp.get("dados");
                            item.put("nomeProduto", prod.get("nome"));
                            item.put("categoria", prod.get("categoria"));
                        } catch (Exception e) {
                            item.put("nomeProduto", "Produto #" + entry.getKey());
                        }

                        ranking.add(item);
                    });

            relatorio.put("ranking", ranking);
        } catch (Exception e) {
            log.error("Erro ao gerar relatório de produtos: {}", e.getMessage());
            relatorio.put("erro", "Não foi possível obter dados");
        }

        return relatorio;
    }

    /**
     * Relatório de controle de estoque.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> relatorioEstoque() {
        Map<String, Object> relatorio = new LinkedHashMap<>();
        relatorio.put("tipo", "CONTROLE_ESTOQUE");

        try {
            Map<String, Object> resposta = restTemplate.getForObject(
                    produtoServiceUrl + "/api/produtos", Map.class);
            List<Map<String, Object>> produtos = (List<Map<String, Object>>) resposta.get("dados");

            List<Map<String, Object>> alertasBaixoEstoque = new ArrayList<>();
            int totalItens = 0;

            for (Map<String, Object> produto : produtos) {
                int estoque = Integer.parseInt(produto.get("estoque").toString());
                int estoqueMinimo = Integer.parseInt(produto.get("estoqueMinimo").toString());
                totalItens += estoque;

                if (estoque <= estoqueMinimo) {
                    Map<String, Object> alerta = new LinkedHashMap<>();
                    alerta.put("produtoId", produto.get("id"));
                    alerta.put("nome", produto.get("nome"));
                    alerta.put("estoqueAtual", estoque);
                    alerta.put("estoqueMinimo", estoqueMinimo);
                    alerta.put("status", estoque == 0 ? "ESGOTADO" : "BAIXO");
                    alertasBaixoEstoque.add(alerta);
                }
            }

            relatorio.put("totalProdutos", produtos.size());
            relatorio.put("totalItensEmEstoque", totalItens);
            relatorio.put("produtosComBaixoEstoque", alertasBaixoEstoque.size());
            relatorio.put("alertas", alertasBaixoEstoque);
            relatorio.put("produtos", produtos);
        } catch (Exception e) {
            log.error("Erro ao gerar relatório de estoque: {}", e.getMessage());
            relatorio.put("erro", "Não foi possível obter dados de estoque");
        }

        return relatorio;
    }

    /**
     * Relatório de comissões dos vendedores.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> relatorioComissoes(String inicio, String fim) {
        Map<String, Object> relatorio = new LinkedHashMap<>();
        relatorio.put("tipo", "COMISSOES_VENDEDORES");

        try {
            // Buscar vendedores
            Map<String, Object> vendedoresResp = restTemplate.getForObject(
                    vendaServiceUrl + "/api/vendedores", Map.class);
            List<Map<String, Object>> vendedores = (List<Map<String, Object>>) vendedoresResp.get("dados");

            List<Map<String, Object>> comissoesPorVendedor = new ArrayList<>();
            for (Map<String, Object> vendedor : vendedores) {
                Long vendedorId = Long.valueOf(vendedor.get("id").toString());
                try {
                    Map<String, Object> comResp = restTemplate.getForObject(
                            vendaServiceUrl + "/api/vendedores/" + vendedorId + "/comissoes?inicio=" + inicio + "&fim=" + fim,
                            Map.class);
                    comissoesPorVendedor.add((Map<String, Object>) comResp.get("dados"));
                } catch (Exception e) {
                    log.warn("Erro ao buscar comissões do vendedor {}: {}", vendedorId, e.getMessage());
                }
            }

            relatorio.put("periodo", Map.of("inicio", inicio, "fim", fim));
            relatorio.put("comissoes", comissoesPorVendedor);
        } catch (Exception e) {
            log.error("Erro ao gerar relatório de comissões: {}", e.getMessage());
            relatorio.put("erro", "Não foi possível obter dados de comissões");
        }

        return relatorio;
    }
}
