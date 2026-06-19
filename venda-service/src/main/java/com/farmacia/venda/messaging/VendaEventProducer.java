package com.farmacia.venda.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Producer de eventos de venda para RabbitMQ (V2).
 * Publica eventos nas filas que serão consumidos pelos demais microsserviços.
 */
@Slf4j
@Component
@Profile("v2")
@RequiredArgsConstructor
public class VendaEventProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Publica evento de saída de estoque.
     */
    public void publicarSaidaEstoque(Long produtoId, Integer quantidade, Long vendaId) {
        try {
            Map<String, Object> evento = Map.of(
                    "produtoId", produtoId,
                    "quantidade", quantidade,
                    "vendaId", vendaId,
                    "motivo", "Venda #" + vendaId
            );
            String json = objectMapper.writeValueAsString(evento);
            rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_ESTOQUE, json);
            log.info("[RabbitMQ] Evento de saída de estoque publicado: produto={}, qtd={}", produtoId, quantidade);
        } catch (Exception e) {
            log.error("[RabbitMQ] Erro ao publicar evento de estoque: {}", e.getMessage());
        }
    }

    /**
     * Publica evento de emissão de NF-e.
     */
    public void publicarEmissaoNFe(Long vendaId, String cpfDestinatario, java.math.BigDecimal valorTotal) {
        try {
            Map<String, Object> evento = Map.of(
                    "vendaId", vendaId,
                    "cpfDestinatario", cpfDestinatario != null ? cpfDestinatario : "",
                    "valorTotal", valorTotal.toString()
            );
            String json = objectMapper.writeValueAsString(evento);
            rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_NOTA_FISCAL, json);
            log.info("[RabbitMQ] Evento de emissão de NF-e publicado: venda={}", vendaId);
        } catch (Exception e) {
            log.error("[RabbitMQ] Erro ao publicar evento de NF-e: {}", e.getMessage());
        }
    }

    /**
     * Publica evento de registro de receita controlada.
     */
    public void publicarRegistroReceita(Long vendaId, String clienteCpf, Long produtoId,
                                         String numeroReceita, String crmMedico, String nomeMedico) {
        try {
            Map<String, Object> evento = Map.of(
                    "vendaId", vendaId,
                    "clienteCpf", clienteCpf,
                    "produtoId", produtoId,
                    "numeroReceita", numeroReceita != null ? numeroReceita : "",
                    "crmMedico", crmMedico != null ? crmMedico : "",
                    "nomeMedico", nomeMedico != null ? nomeMedico : ""
            );
            String json = objectMapper.writeValueAsString(evento);
            rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_RECEITA, json);
            log.info("[RabbitMQ] Evento de receita publicado: venda={}", vendaId);
        } catch (Exception e) {
            log.error("[RabbitMQ] Erro ao publicar evento de receita: {}", e.getMessage());
        }
    }
}
