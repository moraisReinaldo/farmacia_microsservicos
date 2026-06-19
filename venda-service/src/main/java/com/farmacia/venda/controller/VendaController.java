package com.farmacia.venda.controller;

import com.farmacia.common.dto.ApiResponse;
import com.farmacia.venda.dto.VendaRequest;
import com.farmacia.venda.model.Venda;
import com.farmacia.venda.model.Vendedor;
import com.farmacia.venda.service.VendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller do Microsserviço de Vendas.
 * Suporta vendas no balcão, online e via iFood.
 * Cada venda pode gerar comissão para o vendedor.
 */
@RestController
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    // ============ VENDAS ============

    @PostMapping("/api/vendas")
    public ResponseEntity<ApiResponse<Map<String, Object>>> realizarVenda(@RequestBody VendaRequest request) {
        Map<String, Object> resultado = vendaService.processarVenda(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.sucesso("Venda realizada", resultado));
    }

    /**
     * Endpoint para receber pedidos do iFood.
     * O iFood envia pedidos que são processados como vendas online.
     */
    @PostMapping("/api/vendas/ifood")
    public ResponseEntity<ApiResponse<Map<String, Object>>> receberPedidoIFood(@RequestBody VendaRequest request) {
        request.setTipoVenda("IFOOD");
        Map<String, Object> resultado = vendaService.processarVenda(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.sucesso("Pedido iFood recebido", resultado));
    }

    @GetMapping("/api/vendas")
    public ResponseEntity<ApiResponse<List<Venda>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.sucesso(vendaService.listarTodas()));
    }

    @GetMapping("/api/vendas/{id}")
    public ResponseEntity<ApiResponse<Venda>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.sucesso(vendaService.buscarPorId(id)));
    }

    @GetMapping("/api/vendas/periodo")
    public ResponseEntity<ApiResponse<List<Venda>>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(ApiResponse.sucesso(vendaService.buscarPorPeriodo(inicio, fim)));
    }

    @GetMapping("/api/vendas/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<Venda>>> buscarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ApiResponse.sucesso(vendaService.buscarPorCliente(clienteId)));
    }

    // ============ VENDEDORES ============

    @GetMapping("/api/vendedores")
    public ResponseEntity<ApiResponse<List<Vendedor>>> listarVendedores() {
        return ResponseEntity.ok(ApiResponse.sucesso(vendaService.listarVendedores()));
    }

    @PostMapping("/api/vendedores")
    public ResponseEntity<ApiResponse<Vendedor>> criarVendedor(@RequestBody Vendedor vendedor) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.sucesso("Vendedor cadastrado", vendaService.salvarVendedor(vendedor)));
    }

    @GetMapping("/api/vendedores/{id}")
    public ResponseEntity<ApiResponse<Vendedor>> buscarVendedor(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.sucesso(vendaService.buscarVendedorPorId(id)));
    }

    @GetMapping("/api/vendedores/{id}/comissoes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calcularComissoes(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(ApiResponse.sucesso(vendaService.calcularComissoes(id, inicio, fim)));
    }
}
