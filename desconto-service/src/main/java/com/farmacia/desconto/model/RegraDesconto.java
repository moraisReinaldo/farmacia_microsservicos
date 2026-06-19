package com.farmacia.desconto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Regra de desconto configurável por farmácia.
 * Tipos: PROGRESSIVO (baseado em compras), CONVENIO (idoso+convênio),
 * FABRICANTE (desconto do fabricante), IDOSO (desconto geral para idosos).
 */
@Entity
@Table(name = "regras_desconto")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegraDesconto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDesconto tipo;

    @Column(name = "valor_percentual", nullable = false, precision = 5, scale = 2)
    private BigDecimal valorPercentual;

    /**
     * Número mínimo de compras para aplicar (desconto progressivo).
     */
    @Column(name = "min_compras")
    private Integer minCompras = 0;

    @Column(name = "max_compras")
    private Integer maxCompras;

    /**
     * ID da farmácia que criou a regra (cada farmácia define suas regras).
     */
    @Column(name = "farmacia_id")
    private Long farmaciaId;

    @Column(name = "apenas_idosos")
    private Boolean apenasIdosos = false;

    @Column(name = "requer_convenio")
    private Boolean requerConvenio = false;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum TipoDesconto {
        PROGRESSIVO, CONVENIO, FABRICANTE, IDOSO
    }
}
