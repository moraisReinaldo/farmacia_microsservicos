package com.farmacia.common.exception;

/**
 * Exceção lançada quando um recurso não é encontrado.
 * Componente reutilizável em todos os microsserviços (V1 e V2).
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensagem) {
        super(mensagem);
    }

    public ResourceNotFoundException(String recurso, Long id) {
        super(recurso + " não encontrado(a) com ID: " + id);
    }
}
