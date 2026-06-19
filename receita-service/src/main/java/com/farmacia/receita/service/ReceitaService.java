package com.farmacia.receita.service;

import com.farmacia.common.exception.ResourceNotFoundException;
import com.farmacia.receita.integration.AnsIntegration;
import com.farmacia.receita.model.Receita;
import com.farmacia.receita.repository.ReceitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceitaService {

    private final ReceitaRepository repository;
    private final AnsIntegration ansIntegration;

    public Receita registrarReceita(Receita receita) {
        // Enviar para ANS automaticamente
        String protocolo = ansIntegration.enviarReceitaDigital(
                receita.getClienteCpf(),
                receita.getNumeroReceita(),
                receita.getCrmMedico(),
                receita.getNomeMedico()
        );

        receita.setProtocoloAns(protocolo);
        receita.setEnviadaAns(true);
        receita.setDataEnvioAns(LocalDateTime.now());

        log.info("Receita registrada e enviada para ANS. Protocolo: {}", protocolo);
        return repository.save(receita);
    }

    public Receita buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receita", id));
    }

    public List<Receita> listarPendentes() {
        return repository.findByEnviadaAnsFalse();
    }

    /**
     * Envia lote de receitas pendentes para a ANS.
     */
    public int enviarLoteParaAns() {
        List<Receita> pendentes = repository.findByEnviadaAnsFalse();
        if (pendentes.isEmpty()) {
            log.info("Nenhuma receita pendente para enviar à ANS");
            return 0;
        }

        for (Receita receita : pendentes) {
            String protocolo = ansIntegration.enviarReceitaDigital(
                    receita.getClienteCpf(),
                    receita.getNumeroReceita(),
                    receita.getCrmMedico(),
                    receita.getNomeMedico()
            );
            receita.setProtocoloAns(protocolo);
            receita.setEnviadaAns(true);
            receita.setDataEnvioAns(LocalDateTime.now());
            repository.save(receita);
        }

        log.info("{} receitas enviadas para ANS com sucesso", pendentes.size());
        return pendentes.size();
    }
}
