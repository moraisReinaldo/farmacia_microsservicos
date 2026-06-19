package com.farmacia.receita.messaging;

import com.farmacia.receita.model.Receita;
import com.farmacia.receita.service.ReceitaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumer RabbitMQ para V2.
 * Consome eventos de registro de receita publicados pelo venda-service.
 */
@Slf4j
@Component
@Profile("v2")
@RequiredArgsConstructor
public class ReceitaEventConsumer {

    private final ReceitaService receitaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "farmacia.receita.registrar")
    public void processarRegistroReceita(String mensagem) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> evento = objectMapper.readValue(mensagem, Map.class);

            Receita receita = new Receita();
            receita.setVendaId(Long.valueOf(evento.get("vendaId").toString()));
            receita.setClienteCpf(evento.get("clienteCpf").toString());
            receita.setProdutoId(Long.valueOf(evento.get("produtoId").toString()));
            receita.setNumeroReceita(evento.get("numeroReceita").toString());
            receita.setCrmMedico(evento.get("crmMedico").toString());
            receita.setNomeMedico(evento.get("nomeMedico").toString());

            receitaService.registrarReceita(receita);
            log.info("[RabbitMQ] Receita registrada via mensageria: venda={}", receita.getVendaId());
        } catch (Exception e) {
            log.error("[RabbitMQ] Erro ao processar evento de receita: {}", e.getMessage());
        }
    }
}
