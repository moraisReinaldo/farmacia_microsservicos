package com.farmacia.desconto.repository;

import com.farmacia.desconto.model.RegraDesconto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescontoRepository extends JpaRepository<RegraDesconto, Long> {
    List<RegraDesconto> findByAtivoTrue();
    List<RegraDesconto> findByTipoAndAtivoTrue(RegraDesconto.TipoDesconto tipo);
    List<RegraDesconto> findByApenasIdososTrueAndAtivoTrue();
}
