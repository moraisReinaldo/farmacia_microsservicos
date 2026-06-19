package com.farmacia.receita.controller;

import com.farmacia.common.dto.ApiResponse;
import com.farmacia.receita.model.Receita;
import com.farmacia.receita.service.ReceitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller de Receitas.
 * Registra receitas retidas de medicamentos controlados e envia para ANS.
 */
@RestController
@RequestMapping("/api/receitas")
@RequiredArgsConstructor
public class ReceitaController {

    private final ReceitaService receitaService;

    @PostMapping
    public ResponseEntity<ApiResponse<Receita>> registrar(@RequestBody Receita receita) {
        Receita salva = receitaService.registrarReceita(receita);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.sucesso("Receita registrada e enviada à ANS", salva));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Receita>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.sucesso(receitaService.buscarPorId(id)));
    }

    @GetMapping("/pendentes")
    public ResponseEntity<ApiResponse<List<Receita>>> listarPendentes() {
        return ResponseEntity.ok(ApiResponse.sucesso(receitaService.listarPendentes()));
    }

    @PostMapping("/enviar-ans")
    public ResponseEntity<ApiResponse<Map<String, Object>>> enviarLoteAns() {
        int enviadas = receitaService.enviarLoteParaAns();
        return ResponseEntity.ok(ApiResponse.sucesso("Lote enviado à ANS",
                Map.of("receitasEnviadas", enviadas)));
    }
}
