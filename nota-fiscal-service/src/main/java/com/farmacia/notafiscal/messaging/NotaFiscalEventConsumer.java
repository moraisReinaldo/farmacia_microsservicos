package com.farmacia.notafiscal.messaging;

import com.farmacia.notafiscal.service.NotaFiscalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Consumer RabbitMQ para V2.
 * Consome eventos de emissão de NF-e publicados pelo venda-service.
 */
@Slf4j
@Component
@Profile("v2")
@RequiredArgsConstructor
public class NotaFiscalEventConsumer {

    private final NotaFiscalService notaFiscalService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "farmacia.notafiscal.emitir")
    public void processarEmissaoNFe(String mensagem) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> evento = objectMapper.readValue(mensagem, Map.class);

            Long vendaId = Long.valueOf(evento.get("vendaId").toString());
            String cpfDestinatario = evento.get("cpfDestinatario").toString();
            if (cpfDestinatario.isEmpty()) cpfDestinatario = null;
            BigDecimal valorTotal = new BigDecimal(evento.get("valorTotal").toString());

            notaFiscalService.emitirNFe(vendaId, cpfDestinatario, valorTotal);
            log.info("[RabbitMQ] NF-e emitida via mensageria: venda={}", vendaId);
        } catch (Exception e) {
            log.error("[RabbitMQ] Erro ao processar evento de NF-e: {}", e.getMessage());
        }
    }
}
