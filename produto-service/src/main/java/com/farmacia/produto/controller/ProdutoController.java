package com.farmacia.produto.controller;

import com.farmacia.common.dto.ApiResponse;
import com.farmacia.produto.model.Produto;
import com.farmacia.produto.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller do Microsserviço de Produtos.
 * Gerencia medicamentos (controlados e não controlados) e produtos de higiene/cosméticos.
 */
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Produto>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.sucesso(produtoService.listarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Produto>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.sucesso(produtoService.buscarPorId(id)));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<Produto>>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(ApiResponse.sucesso(produtoService.buscarPorNome(nome)));
    }

    @GetMapping("/controlados")
    public ResponseEntity<ApiResponse<List<Produto>>> listarControlados() {
        return ResponseEntity.ok(ApiResponse.sucesso(produtoService.listarControlados()));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<ApiResponse<List<Produto>>> listarPorCategoria(
            @PathVariable Produto.CategoriaProduto categoria) {
        return ResponseEntity.ok(ApiResponse.sucesso(produtoService.listarPorCategoria(categoria)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Produto>> criar(@RequestBody Produto produto) {
        Produto salvo = produtoService.salvar(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.sucesso("Produto cadastrado", salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Produto>> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        return ResponseEntity.ok(ApiResponse.sucesso("Produto atualizado", produtoService.atualizar(id, produto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desativar(@PathVariable Long id) {
        produtoService.desativar(id);
        return ResponseEntity.ok(ApiResponse.sucesso("Produto desativado", null));
    }

    @PatchMapping("/{id}/estoque")
    public ResponseEntity<ApiResponse<Void>> atualizarEstoque(
            @PathVariable Long id, @RequestParam Integer quantidade) {
        produtoService.atualizarEstoque(id, quantidade);
        return ResponseEntity.ok(ApiResponse.sucesso("Estoque atualizado", null));
    }
}
