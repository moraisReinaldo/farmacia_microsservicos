package com.farmacia.notafiscal.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Integração SIMULADA com a SEFAZ (Secretaria da Fazenda).
 * Envia NF-e de medicamentos e insumos.
 * Em produção, seria substituída pela comunicação real via Web Service SEFAZ.
 */
@Slf4j
@Component
public class SefazIntegration {

    /**
     * Simula o envio da NF-e para a SEFAZ.
     * Retorna a chave de acesso (44 dígitos) gerada.
     */
    public String enviarNFe(Long vendaId, String cpfDestinatario, java.math.BigDecimal valorTotal) {
        // Gera chave de acesso simulada (44 caracteres)
        String chaveAcesso = UUID.randomUUID().toString().replace("-", "").toUpperCase()
                + UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 12);
        chaveAcesso = chaveAcesso.substring(0, 44);

        log.info("[SEFAZ] ========================================");
        log.info("[SEFAZ] Enviando NF-e para Secretaria da Fazenda");
        log.info("[SEFAZ] Venda ID: {}", vendaId);
        log.info("[SEFAZ] CPF Destinatário: {}", cpfDestinatario != null ? cpfDestinatario : "NÃO INFORMADO");
        log.info("[SEFAZ] Valor Total: R$ {}", valorTotal);
        log.info("[SEFAZ] Chave de Acesso: {}", chaveAcesso);
        log.info("[SEFAZ] Status: AUTORIZADA");
        log.info("[SEFAZ] ========================================");

        return chaveAcesso;
    }

    /**
     * Gera XML simulado da NF-e.
     */
    public String gerarXmlNFe(String numeroNf, String chaveAcesso, String cpf, java.math.BigDecimal valor) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe" versao="4.00">
                    <NFe>
                        <infNFe Id="NFe%s">
                            <ide>
                                <nNF>%s</nNF>
                                <dhEmi>%s</dhEmi>
                            </ide>
                            <dest>
                                <CPF>%s</CPF>
                            </dest>
                            <total>
                                <ICMSTot>
                                    <vNF>%s</vNF>
                                </ICMSTot>
                            </total>
                        </infNFe>
                    </NFe>
                    <protNFe>
                        <infProt>
                            <chNFe>%s</chNFe>
                            <cStat>100</cStat>
                            <xMotivo>Autorizado o uso da NF-e</xMotivo>
                        </infProt>
                    </protNFe>
                </nfeProc>
                """.formatted(chaveAcesso, numeroNf, java.time.LocalDateTime.now(),
                cpf != null ? cpf : "", valor, chaveAcesso);
    }
}
