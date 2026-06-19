package com.farmacia.estoque.controller;

import com.farmacia.common.dto.ApiResponse;
import com.farmacia.estoque.model.MovimentacaoEstoque;
import com.farmacia.estoque.service.EstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovimentacaoEstoque>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.sucesso(estoqueService.listarTodas()));
    }

    @GetMapping("/movimentacoes/{produtoId}")
    public ResponseEntity<ApiResponse<List<MovimentacaoEstoque>>> listarMovimentacoes(@PathVariable Long produtoId) {
        return ResponseEntity.ok(ApiResponse.sucesso(estoqueService.listarMovimentacoes(produtoId)));
    }

    @PostMapping("/entrada")
    public ResponseEntity<ApiResponse<MovimentacaoEstoque>> registrarEntrada(@RequestBody Map<String, Object> body) {
        Long produtoId = Long.valueOf(body.get("produtoId").toString());
        Integer quantidade = Integer.valueOf(body.get("quantidade").toString());
        String motivo = body.get("motivo") != null ? body.get("motivo").toString() : null;
        MovimentacaoEstoque mov = estoqueService.registrarEntrada(produtoId, quantidade, motivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.sucesso("Entrada registrada", mov));
    }

    @PostMapping("/saida")
    public ResponseEntity<ApiResponse<MovimentacaoEstoque>> registrarSaida(@RequestBody Map<String, Object> body) {
        Long produtoId = Long.valueOf(body.get("produtoId").toString());
        Integer quantidade = Integer.valueOf(body.get("quantidade").toString());
        Long vendaId = body.get("vendaId") != null ? Long.valueOf(body.get("vendaId").toString()) : null;
        String motivo = body.get("motivo") != null ? body.get("motivo").toString() : null;
        MovimentacaoEstoque mov = estoqueService.registrarSaida(produtoId, quantidade, vendaId, motivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.sucesso("Saída registrada", mov));
    }
}
