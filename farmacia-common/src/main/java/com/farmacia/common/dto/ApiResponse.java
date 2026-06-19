package com.farmacia.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO padrão de resposta da API.
 * Componente reutilizável em todos os microsserviços (V1 e V2).
 *
 * @param <T> Tipo do dado retornado
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private boolean sucesso;
    private String mensagem;
    private T dados;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> sucesso(T dados) {
        return new ApiResponse<>(true, "Operação realizada com sucesso", dados, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> sucesso(String mensagem, T dados) {
        return new ApiResponse<>(true, mensagem, dados, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> erro(String mensagem) {
        return new ApiResponse<>(false, mensagem, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> erro(String mensagem, T dados) {
        return new ApiResponse<>(false, mensagem, dados, LocalDateTime.now());
    }
}
