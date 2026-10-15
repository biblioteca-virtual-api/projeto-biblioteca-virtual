package br.edu.christus.backend.exception;

// Violação de regra de negócio (ex.: RN12 - gênero inativo). Vira HTTP 422.
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
