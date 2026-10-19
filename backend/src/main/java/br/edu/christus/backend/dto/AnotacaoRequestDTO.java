package br.edu.christus.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA do POST /api/v1/anotacoes.
 *
 * Padrão do projeto: DTOs são sempre `record` (imutáveis, sem Lombok).
 * O dono da anotação NÃO vem no corpo: é sempre o usuário do token (RN17/RN19).
 * As anotações de validação (@NotNull, @Size...) são aplicadas pelo @Valid do controller;
 * quando falham, o ApiExceptionHandler devolve 400 (RN18: máximo de 400 caracteres).
 */
public record AnotacaoRequestDTO(

        @NotNull(message = "O livro é obrigatório")
        Long livroId,

        @NotNull(message = "A página é obrigatória")
        @Min(value = 1, message = "A página deve ser maior ou igual a 1")
        Integer pagina,

        @NotBlank(message = "O texto é obrigatório")
        @Size(max = 400, message = "O texto deve ter no máximo 400 caracteres")
        String texto
) {
}
