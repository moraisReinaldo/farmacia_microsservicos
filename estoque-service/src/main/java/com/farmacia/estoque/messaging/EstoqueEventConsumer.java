package com.farmacia.estoque.messaging;

import com.farmacia.estoque.service.EstoqueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumer RabbitMQ para V2.
 * Consome eventos de saída de estoque publicados pelo venda-service.
 */
@Slf4j
@Component
@Profile("v2")
@RequiredArgsConstructor
public class EstoqueEventConsumer {

    private final EstoqueService estoqueService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "farmacia.estoque.saida")
    public void processarSaidaEstoque(String mensagem) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> evento = objectMapper.readValue(mensagem, Map.class);

            Long produtoId = Long.valueOf(evento.get("produtoId").toString());
            Integer quantidade = Integer.valueOf(evento.get("quantidade").toString());
            Long vendaId = Long.valueOf(evento.get("vendaId").toString());
            String motivo = evento.get("motivo") != null ? evento.get("motivo").toString() : "Saída via mensageria";

            estoqueService.registrarSaida(produtoId, quantidade, vendaId, motivo);
            log.info("[RabbitMQ] Saída de estoque processada: produto={}, qtd={}", produtoId, quantidade);
        } catch (Exception e) {
            log.error("[RabbitMQ] Erro ao processar evento de estoque: {}", e.getMessage());
        }
    }
}
