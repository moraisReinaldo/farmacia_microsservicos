package com.farmacia.notafiscal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Nota Fiscal Eletrônica (NF-e).
 * Integra com SEFAZ para envio e autorização.
 * O CPF do destinatário é opcional - o cliente pode solicitar NF com CPF sem estar cadastrado.
 */
@Entity
@Table(name = "notas_fiscais")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venda_id", nullable = false)
    private Long vendaId;

    @Column(name = "numero_nf", nullable = false, length = 50)
    private String numeroNf;

    /**
     * Chave de acesso da NF-e (44 dígitos) gerada pela SEFAZ.
     */
    @Column(name = "chave_acesso", length = 44)
    private String chaveAcesso;

    /**
     * CPF do destinatário - OPCIONAL.
     * Não implica cadastro do cliente no sistema.
     */
    @Column(name = "cpf_destinatario", length = 14)
    private String cpfDestinatario;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusNF status = StatusNF.EMITIDA;

    @Column(name = "xml_nfe", columnDefinition = "TEXT")
    private String xmlNfe;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum StatusNF {
        EMITIDA, AUTORIZADA, CANCELADA
    }
}
