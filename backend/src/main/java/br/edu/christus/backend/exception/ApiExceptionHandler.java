package br.edu.christus.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

// Handler complementar ao ResourceExceptionHandler (404 e 401 continuam lá).
@RestControllerAdvice
public class ApiExceptionHandler {

    // RN12: regra de negócio violada -> 422
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> business(BusinessException e, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negócio violada", e.getMessage(), request);
    }

    // ISBN duplicado -> 409
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<StandardError> conflict(ConflictException e, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Conflito", e.getMessage(), request);
    }

    // RN22: o banco recusou a exclusão por causa da FK (ON DELETE RESTRICT) -> 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> integrity(DataIntegrityViolationException e, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Violação de integridade",
                "Operação não permitida: existem registros vinculados (ex.: livros) ou dados duplicados.", request);
    }

    // Falha do @Valid nos DTOs -> 400, listando os campos inválidos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "Dados inválidos", msg, request);
    }

    // RN17/RN19: tentativa de acessar recurso de outro usuário -> 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardError> forbidden(AccessDeniedException e, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "Acesso negado", e.getMessage(), request);
    }

    private ResponseEntity<StandardError> build(HttpStatus status, String error, String message,
                                                HttpServletRequest request) {
        StandardError err = new StandardError(Instant.now(), status.value(), error, message,
                request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
