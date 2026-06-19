package com.farmacia.relatorio.controller;

import com.farmacia.common.dto.ApiResponse;
import com.farmacia.relatorio.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller de Relatórios Gerenciais.
 * Vendas por período, medicamentos mais vendidos, estoque e comissões.
 */
@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/vendas")
    public ResponseEntity<ApiResponse<Map<String, Object>>> relatorioVendas(
            @RequestParam String inicio, @RequestParam String fim) {
        return ResponseEntity.ok(ApiResponse.sucesso(relatorioService.relatorioVendas(inicio, fim)));
    }

    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> produtosMaisVendidos(
            @RequestParam String inicio, @RequestParam String fim) {
        return ResponseEntity.ok(ApiResponse.sucesso(relatorioService.relatorioProdutosMaisVendidos(inicio, fim)));
    }

    @GetMapping("/estoque")
    public ResponseEntity<ApiResponse<Map<String, Object>>> relatorioEstoque() {
        return ResponseEntity.ok(ApiResponse.sucesso(relatorioService.relatorioEstoque()));
    }

    @GetMapping("/comissoes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> relatorioComissoes(
            @RequestParam String inicio, @RequestParam String fim) {
        return ResponseEntity.ok(ApiResponse.sucesso(relatorioService.relatorioComissoes(inicio, fim)));
    }
}
