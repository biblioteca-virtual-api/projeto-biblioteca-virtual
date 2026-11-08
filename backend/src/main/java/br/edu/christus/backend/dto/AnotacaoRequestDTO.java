package br.edu.christus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Dados de entrada para criar uma anotação privada de leitura")
public record AnotacaoRequestDTO(

        @Schema(description = "ID do livro ao qual a anotação pertence", example = "10")
        @NotNull(message = "O livro é obrigatório")
        Long livroId,

        @Schema(description = "Número da página do livro (maior ou igual a 1)", example = "42", minimum = "1")
        @NotNull(message = "A página é obrigatória")
        @Min(value = 1, message = "A página deve ser maior ou igual a 1")
        Integer pagina,

        @Schema(description = "Texto da anotação (até 400 caracteres, RN18)",
                example = "Capítulo importante sobre o ponto de vista do narrador.", maxLength = 400)
        @NotBlank(message = "O texto é obrigatório")
        @Size(max = 400, message = "O texto deve ter no máximo 400 caracteres")
        String texto
) {
}
