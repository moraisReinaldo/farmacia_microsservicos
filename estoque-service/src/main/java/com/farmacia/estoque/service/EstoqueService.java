package com.farmacia.estoque.service;

import com.farmacia.estoque.model.MovimentacaoEstoque;
import com.farmacia.estoque.repository.MovimentacaoEstoqueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final MovimentacaoEstoqueRepository repository;
    private final RestTemplate restTemplate;

    @Value("${microservicos.produto-service.url:http://localhost:8081}")
    private String produtoServiceUrl;

    /**
     * Registra uma entrada de estoque e atualiza o produto-service.
     */
    public MovimentacaoEstoque registrarEntrada(Long produtoId, Integer quantidade, String motivo) {
        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setProdutoId(produtoId);
        mov.setTipo(MovimentacaoEstoque.TipoMovimentacao.ENTRADA);
        mov.setQuantidade(quantidade);
        mov.setMotivo(motivo != null ? motivo : "Entrada de estoque");

        // Atualizar estoque no produto-service
        try {
            restTemplate.patchForObject(
                    produtoServiceUrl + "/api/produtos/" + produtoId + "/estoque?quantidade=" + quantidade,
                    null, Map.class);
            log.info("Estoque atualizado no produto-service: produto={}, +{}", produtoId, quantidade);
        } catch (Exception e) {
            log.error("Erro ao atualizar estoque no produto-service: {}", e.getMessage());
        }

        return repository.save(mov);
    }

    /**
     * Registra uma saída de estoque (venda) e atualiza o produto-service.
     */
    public MovimentacaoEstoque registrarSaida(Long produtoId, Integer quantidade, Long vendaId, String motivo) {
        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setProdutoId(produtoId);
        mov.setTipo(MovimentacaoEstoque.TipoMovimentacao.SAIDA);
        mov.setQuantidade(quantidade);
        mov.setVendaId(vendaId);
        mov.setMotivo(motivo != null ? motivo : "Saída por venda");

        // Debitar estoque no produto-service (quantidade negativa)
        try {
            restTemplate.patchForObject(
                    produtoServiceUrl + "/api/produtos/" + produtoId + "/estoque?quantidade=" + (-quantidade),
                    null, Map.class);
            log.info("Estoque debitado no produto-service: produto={}, -{}", produtoId, quantidade);
        } catch (Exception e) {
            log.error("Erro ao debitar estoque no produto-service: {}", e.getMessage());
        }

        return repository.save(mov);
    }

    public List<MovimentacaoEstoque> listarMovimentacoes(Long produtoId) {
        return repository.findByProdutoIdOrderByCreatedAtDesc(produtoId);
    }

    public List<MovimentacaoEstoque> listarTodas() {
        return repository.findAll();
    }
}
