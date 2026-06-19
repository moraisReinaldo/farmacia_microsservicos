package com.farmacia.produto.service;

import com.farmacia.common.exception.ResourceNotFoundException;
import com.farmacia.produto.model.Produto;
import com.farmacia.produto.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public List<Produto> listarTodos() {
        return produtoRepository.findByAtivoTrue();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", id));
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
    }

    public List<Produto> listarControlados() {
        return produtoRepository.findByControladoTrueAndAtivoTrue();
    }

    public List<Produto> listarPorCategoria(Produto.CategoriaProduto categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto atualizar(Long id, Produto dadosAtualizados) {
        Produto produto = buscarPorId(id);
        produto.setNome(dadosAtualizados.getNome());
        produto.setDescricao(dadosAtualizados.getDescricao());
        produto.setCategoria(dadosAtualizados.getCategoria());
        produto.setPreco(dadosAtualizados.getPreco());
        produto.setEstoque(dadosAtualizados.getEstoque());
        produto.setEstoqueMinimo(dadosAtualizados.getEstoqueMinimo());
        produto.setControlado(dadosAtualizados.getControlado());
        produto.setRegistroAnvisa(dadosAtualizados.getRegistroAnvisa());
        produto.setFabricante(dadosAtualizados.getFabricante());
        return produtoRepository.save(produto);
    }

    public void desativar(Long id) {
        Produto produto = buscarPorId(id);
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    public void atualizarEstoque(Long id, Integer quantidade) {
        Produto produto = buscarPorId(id);
        produto.setEstoque(produto.getEstoque() + quantidade);
        produtoRepository.save(produto);
    }
}
