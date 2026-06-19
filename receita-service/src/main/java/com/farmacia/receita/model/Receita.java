package com.farmacia.receita.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Receita retida - medicamentos controlados e de uso contínuo.
 * Cópias digitais das receitas são enviadas periodicamente para a ANS.
 */
@Entity
@Table(name = "receitas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Receita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venda_id", nullable = false)
    private Long vendaId;

    @Column(name = "cliente_cpf", nullable = false, length = 14)
    private String clienteCpf;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "numero_receita", length = 50)
    private String numeroReceita;

    @Column(name = "crm_medico", length = 20)
    private String crmMedico;

    @Column(name = "nome_medico")
    private String nomeMedico;

    /**
     * Protocolo gerado pela ANS ao receber a receita.
     */
    @Column(name = "protocolo_ans", length = 50)
    private String protocoloAns;

    @Column(name = "enviada_ans")
    private Boolean enviadaAns = false;

    @Column(name = "data_envio_ans")
    private LocalDateTime dataEnvioAns;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
