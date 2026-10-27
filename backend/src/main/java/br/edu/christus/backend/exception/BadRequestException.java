package br.edu.christus.backend.exception;

// Requisição inválida que o @Valid sozinho não cobre (ex.: rejeitar upload sem corpo). Vira HTTP 400.
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
