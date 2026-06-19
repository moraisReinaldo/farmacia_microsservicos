package com.farmacia.receita.repository;

import com.farmacia.receita.model.Receita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceitaRepository extends JpaRepository<Receita, Long> {
    List<Receita> findByVendaId(Long vendaId);
    List<Receita> findByEnviadaAnsFalse();
    List<Receita> findByClienteCpf(String clienteCpf);
}
