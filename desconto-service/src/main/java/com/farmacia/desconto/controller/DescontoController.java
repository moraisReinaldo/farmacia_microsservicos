package com.farmacia.desconto.controller;

import com.farmacia.common.dto.ApiResponse;
import com.farmacia.desconto.model.RegraDesconto;
import com.farmacia.desconto.service.DescontoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/descontos")
@RequiredArgsConstructor
public class DescontoController {

    private final DescontoService descontoService;

    @PostMapping("/calcular")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calcularDesconto(@RequestBody Map<String, Object> body) {
        Long clienteId = Long.valueOf(body.get("clienteId").toString());
        BigDecimal subtotal = new BigDecimal(body.get("subtotal").toString());
        return ResponseEntity.ok(ApiResponse.sucesso(descontoService.calcularDesconto(clienteId, subtotal)));
    }

    @GetMapping("/regras")
    public ResponseEntity<ApiResponse<List<RegraDesconto>>> listarRegras() {
        return ResponseEntity.ok(ApiResponse.sucesso(descontoService.listarRegras()));
    }

    @PostMapping("/regras")
    public ResponseEntity<ApiResponse<RegraDesconto>> criarRegra(@RequestBody RegraDesconto regra) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.sucesso("Regra de desconto criada", descontoService.salvarRegra(regra)));
    }

    @PutMapping("/regras/{id}")
    public ResponseEntity<ApiResponse<RegraDesconto>> atualizarRegra(@PathVariable Long id, @RequestBody RegraDesconto regra) {
        return ResponseEntity.ok(ApiResponse.sucesso("Regra atualizada", descontoService.atualizarRegra(id, regra)));
    }

    @DeleteMapping("/regras/{id}")
    public ResponseEntity<ApiResponse<Void>> desativarRegra(@PathVariable Long id) {
        descontoService.desativarRegra(id);
        return ResponseEntity.ok(ApiResponse.sucesso("Regra desativada", null));
    }
}
