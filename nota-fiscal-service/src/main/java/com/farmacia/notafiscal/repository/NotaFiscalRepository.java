package com.farmacia.notafiscal.repository;

import com.farmacia.notafiscal.model.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {
    Optional<NotaFiscal> findByVendaId(Long vendaId);
    List<NotaFiscal> findByStatus(NotaFiscal.StatusNF status);
}
