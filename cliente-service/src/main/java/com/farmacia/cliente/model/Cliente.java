package com.farmacia.cliente.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Entidade Cliente.
 * O cadastro é necessário apenas para bonificações e medicamentos controlados.
 * NF com CPF NÃO implica cadastro no sistema.
 */
@Entity
@Table(name = "clientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 14)
    private String cpf;

    @Column(nullable = false)
    private String nome;

    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(length = 500)
    private String endereco;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(length = 10)
    private String cep;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    /**
     * Indica se o cliente possui convênio médico.
     * Idosos com convênio podem ter desconto especial.
     */
    @Column(name = "convenio_medico")
    private Boolean convenioMedico = false;

    @Column(name = "nome_convenio")
    private String nomeConvenio;

    /**
     * Contador de compras para desconto progressivo.
     */
    @Column(name = "total_compras")
    private Integer totalCompras = 0;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica se o cliente é idoso (60+ anos).
     */
    @Transient
    public boolean isIdoso() {
        if (dataNascimento == null) return false;
        return Period.between(dataNascimento, LocalDate.now()).getYears() >= 60;
    }
}
