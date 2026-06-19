package com.farmacia.receita.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Integração SIMULADA com a ANS (Agência Nacional de Saúde Suplementar).
 * Envia cópias digitais das receitas retidas de medicamentos controlados
 * e de uso contínuo, sujeitos a notificação.
 */
@Slf4j
@Component
public class AnsIntegration {

    /**
     * Simula o envio periódico de cópia digital da receita para a ANS.
     * Retorna o protocolo gerado pela ANS.
     */
    public String enviarReceitaDigital(String clienteCpf, String numeroReceita, String crmMedico, String nomeMedico) {
        String protocoloAns = "ANS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        log.info("[ANS] ========================================");
        log.info("[ANS] Enviando cópia digital da receita para ANS");
        log.info("[ANS] CPF do Paciente: {}", clienteCpf);
        log.info("[ANS] Número da Receita: {}", numeroReceita);
        log.info("[ANS] CRM do Médico: {}", crmMedico);
        log.info("[ANS] Nome do Médico: {}", nomeMedico);
        log.info("[ANS] Protocolo: {}", protocoloAns);
        log.info("[ANS] Status: RECEBIDA");
        log.info("[ANS] ========================================");

        return protocoloAns;
    }

    /**
     * Simula o envio em lote de receitas pendentes para a ANS.
     */
    public int enviarLotePendentes(int quantidadeReceitas) {
        log.info("[ANS] Enviando lote de {} receitas pendentes para a ANS", quantidadeReceitas);
        log.info("[ANS] Lote processado com sucesso");
        return quantidadeReceitas;
    }
}
