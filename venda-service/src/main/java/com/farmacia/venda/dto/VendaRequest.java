package com.farmacia.venda.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de requisição para registrar uma venda.
 */
@Data
public class VendaRequest {
    private Long clienteId;
    private Long vendedorId;
    private String cpfNota;
    private String tipoVenda; // BALCAO, ONLINE, IFOOD
    private List<ItemRequest> itens;
    private String numeroReceita;
    private String crmMedico;
    private String nomeMedico;

    @Data
    public static class ItemRequest {
        private Long produtoId;
        private Integer quantidade;
    }
}
