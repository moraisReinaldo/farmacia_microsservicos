package com.farmacia.cliente.repository;

import com.farmacia.cliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

    List<Cliente> findByAtivoTrue();

    List<Cliente> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    List<Cliente> findByConvenioMedicoTrueAndAtivoTrue();

    boolean existsByCpf(String cpf);
}
