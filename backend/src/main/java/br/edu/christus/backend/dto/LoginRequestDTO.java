package br.edu.christus.backend.dto;

public record LoginRequestDTO(
        String email,
        String senha
) {
}