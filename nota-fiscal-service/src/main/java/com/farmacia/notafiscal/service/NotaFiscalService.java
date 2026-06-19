package com.farmacia.notafiscal.service;

import com.farmacia.common.exception.ResourceNotFoundException;
import com.farmacia.notafiscal.integration.SefazIntegration;
import com.farmacia.notafiscal.model.NotaFiscal;
import com.farmacia.notafiscal.repository.NotaFiscalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotaFiscalService {

    private final NotaFiscalRepository repository;
    private final SefazIntegration sefazIntegration;
    private static final AtomicLong nfCounter = new AtomicLong(1000);

    /**
     * Emite uma NF-e e envia para a SEFAZ.
     * O CPF do destinatário é OPCIONAL.
     */
    public NotaFiscal emitirNFe(Long vendaId, String cpfDestinatario, BigDecimal valorTotal) {
        String numeroNf = String.format("NF-%06d", nfCounter.incrementAndGet());

        // Enviar para SEFAZ (simulado)
        String chaveAcesso = sefazIntegration.enviarNFe(vendaId, cpfDestinatario, valorTotal);
        String xmlNfe = sefazIntegration.gerarXmlNFe(numeroNf, chaveAcesso, cpfDestinatario, valorTotal);

        NotaFiscal nf = new NotaFiscal();
        nf.setVendaId(vendaId);
        nf.setNumeroNf(numeroNf);
        nf.setChaveAcesso(chaveAcesso);
        nf.setCpfDestinatario(cpfDestinatario);
        nf.setValorTotal(valorTotal);
        nf.setStatus(NotaFiscal.StatusNF.AUTORIZADA);
        nf.setXmlNfe(xmlNfe);

        log.info("NF-e emitida: {} - Chave: {}", numeroNf, chaveAcesso);
        return repository.save(nf);
    }

    public NotaFiscal buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota Fiscal", id));
    }

    public NotaFiscal buscarPorVendaId(Long vendaId) {
        return repository.findByVendaId(vendaId)
                .orElseThrow(() -> new ResourceNotFoundException("Nota Fiscal não encontrada para venda: " + vendaId));
    }
}
