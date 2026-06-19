package com.farmacia.notafiscal.controller;

import com.farmacia.common.dto.ApiResponse;
import com.farmacia.notafiscal.model.NotaFiscal;
import com.farmacia.notafiscal.service.NotaFiscalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Controller de Nota Fiscal.
 * Emissão de NF-e com CPF opcional e integração SEFAZ.
 */
@RestController
@RequestMapping("/api/notas-fiscais")
@RequiredArgsConstructor
public class NotaFiscalController {

    private final NotaFiscalService notaFiscalService;

    @PostMapping
    public ResponseEntity<ApiResponse<NotaFiscal>> emitirNFe(@RequestBody Map<String, Object> body) {
        Long vendaId = Long.valueOf(body.get("vendaId").toString());
        String cpfDestinatario = body.get("cpfDestinatario") != null ? body.get("cpfDestinatario").toString() : null;
        BigDecimal valorTotal = new BigDecimal(body.get("valorTotal").toString());

        NotaFiscal nf = notaFiscalService.emitirNFe(vendaId, cpfDestinatario, valorTotal);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.sucesso("NF-e emitida e autorizada pela SEFAZ", nf));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotaFiscal>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.sucesso(notaFiscalService.buscarPorId(id)));
    }

    @GetMapping("/venda/{vendaId}")
    public ResponseEntity<ApiResponse<NotaFiscal>> buscarPorVendaId(@PathVariable Long vendaId) {
        return ResponseEntity.ok(ApiResponse.sucesso(notaFiscalService.buscarPorVendaId(vendaId)));
    }
}
