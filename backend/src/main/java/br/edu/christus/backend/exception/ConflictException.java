package br.edu.christus.backend.exception;

// Conflito com o estado atual dos dados (ex.: ISBN duplicado). Vira HTTP 409.
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
