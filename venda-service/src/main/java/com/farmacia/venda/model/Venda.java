package com.farmacia.venda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade Venda.
 * Suporta vendas no balcão, online e via iFood.
 * Cada venda pode ter comissão para o vendedor.
 */
@Entity
@Table(name = "vendas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID do cliente cadastrado (opcional).
     * Obrigatório apenas para medicamentos controlados ou para obter descontos.
     */
    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "vendedor_id")
    private Long vendedorId;

    /**
     * CPF para emissão de NF - NÃO implica cadastro no sistema.
     * O cliente pode solicitar NF com CPF sem estar cadastrado.
     */
    @Column(name = "cpf_nota", length = 14)
    private String cpfNota;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_venda", nullable = false)
    private TipoVenda tipoVenda;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(precision = 10, scale = 2)
    private BigDecimal comissao = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVenda status = StatusVenda.PENDENTE;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemVenda> itens;

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

    public enum TipoVenda {
        BALCAO, ONLINE, IFOOD
    }

    public enum StatusVenda {
        PENDENTE, CONCLUIDA, CANCELADA
    }
}
