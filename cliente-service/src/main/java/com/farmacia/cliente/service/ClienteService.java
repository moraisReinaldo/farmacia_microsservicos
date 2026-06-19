package com.farmacia.cliente.service;

import com.farmacia.common.exception.ResourceNotFoundException;
import com.farmacia.common.util.CpfCnpjValidator;
import com.farmacia.cliente.model.Cliente;
import com.farmacia.cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findByAtivoTrue();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    public Cliente buscarPorCpf(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        String cpfFormatado = CpfCnpjValidator.formatarCPF(cpfLimpo);
        return clienteRepository.findByCpf(cpfFormatado)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com CPF: " + cpf));
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
    }

    public Cliente salvar(Cliente cliente) {
        // Validar CPF
        if (cliente.getCpf() != null && !CpfCnpjValidator.validarCPF(cliente.getCpf())) {
            throw new RuntimeException("CPF inválido: " + cliente.getCpf());
        }
        // Formatar CPF
        if (cliente.getCpf() != null) {
            cliente.setCpf(CpfCnpjValidator.formatarCPF(cliente.getCpf()));
        }
        // Verificar duplicidade
        if (cliente.getCpf() != null && clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new RuntimeException("Já existe um cliente cadastrado com este CPF");
        }
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long id, Cliente dadosAtualizados) {
        Cliente cliente = buscarPorId(id);
        cliente.setNome(dadosAtualizados.getNome());
        cliente.setEmail(dadosAtualizados.getEmail());
        cliente.setTelefone(dadosAtualizados.getTelefone());
        cliente.setEndereco(dadosAtualizados.getEndereco());
        cliente.setCidade(dadosAtualizados.getCidade());
        cliente.setEstado(dadosAtualizados.getEstado());
        cliente.setCep(dadosAtualizados.getCep());
        cliente.setDataNascimento(dadosAtualizados.getDataNascimento());
        cliente.setConvenioMedico(dadosAtualizados.getConvenioMedico());
        cliente.setNomeConvenio(dadosAtualizados.getNomeConvenio());
        return clienteRepository.save(cliente);
    }

    public void incrementarCompras(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setTotalCompras(cliente.getTotalCompras() + 1);
        clienteRepository.save(cliente);
    }

    public void desativar(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }
}
