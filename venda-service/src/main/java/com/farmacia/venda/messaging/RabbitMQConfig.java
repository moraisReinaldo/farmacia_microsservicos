package com.farmacia.venda.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;

/**
 * Configuração RabbitMQ para V2.
 * Publica eventos de venda que são consumidos por:
 * - estoque-service (debitar estoque)
 * - nota-fiscal-service (emitir NF-e)
 * - receita-service (registrar receita controlada)
 *
 * Ativar com o profile "v2": --spring.profiles.active=v2
 */
@Slf4j
@Configuration
@Profile("v2")
public class RabbitMQConfig {

    public static final String FILA_ESTOQUE = "farmacia.estoque.saida";
    public static final String FILA_NOTA_FISCAL = "farmacia.notafiscal.emitir";
    public static final String FILA_RECEITA = "farmacia.receita.registrar";

    @Bean
    public Queue filaEstoque() {
        return new Queue(FILA_ESTOQUE, true);
    }

    @Bean
    public Queue filaNotaFiscal() {
        return new Queue(FILA_NOTA_FISCAL, true);
    }

    @Bean
    public Queue filaReceita() {
        return new Queue(FILA_RECEITA, true);
    }
}
