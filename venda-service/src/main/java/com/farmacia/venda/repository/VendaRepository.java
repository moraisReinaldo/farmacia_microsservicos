package com.farmacia.venda.repository;

import com.farmacia.venda.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findByClienteId(Long clienteId);

    List<Venda> findByVendedorId(Long vendedorId);

    List<Venda> findByTipoVenda(Venda.TipoVenda tipoVenda);

    List<Venda> findByStatus(Venda.StatusVenda status);

    @Query("SELECT v FROM Venda v WHERE v.createdAt BETWEEN :inicio AND :fim")
    List<Venda> findByPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT v FROM Venda v WHERE v.vendedorId = :vendedorId AND v.createdAt BETWEEN :inicio AND :fim")
    List<Venda> findByVendedorIdAndPeriodo(@Param("vendedorId") Long vendedorId,
                                           @Param("inicio") LocalDateTime inicio,
                                           @Param("fim") LocalDateTime fim);
}
