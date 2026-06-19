package com.farmacia.cliente.controller;

import com.farmacia.common.dto.ApiResponse;
import com.farmacia.cliente.model.Cliente;
import com.farmacia.cliente.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller do Microsserviço de Clientes.
 * Cadastro obrigatório apenas para bonificações e medicamentos controlados.
 */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cliente>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.sucesso(clienteService.listarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.sucesso(clienteService.buscarPorId(id)));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ApiResponse<Cliente>> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(ApiResponse.sucesso(clienteService.buscarPorCpf(cpf)));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<Cliente>>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(ApiResponse.sucesso(clienteService.buscarPorNome(nome)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Cliente>> criar(@RequestBody Cliente cliente) {
        Cliente salvo = clienteService.salvar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.sucesso("Cliente cadastrado", salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        return ResponseEntity.ok(ApiResponse.sucesso("Cliente atualizado", clienteService.atualizar(id, cliente)));
    }

    @PatchMapping("/{id}/incrementar-compras")
    public ResponseEntity<ApiResponse<Void>> incrementarCompras(@PathVariable Long id) {
        clienteService.incrementarCompras(id);
        return ResponseEntity.ok(ApiResponse.sucesso("Compras incrementadas", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desativar(@PathVariable Long id) {
        clienteService.desativar(id);
        return ResponseEntity.ok(ApiResponse.sucesso("Cliente desativado", null));
    }
}
