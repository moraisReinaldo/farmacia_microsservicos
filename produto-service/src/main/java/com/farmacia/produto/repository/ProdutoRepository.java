package com.farmacia.produto.repository;

import com.farmacia.produto.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrue();

    List<Produto> findByCategoria(Produto.CategoriaProduto categoria);

    List<Produto> findByControladoTrueAndAtivoTrue();

    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    List<Produto> findByEstoqueLessThanEqualAndAtivoTrue(Integer estoqueMinimo);
}
