package br.edu.christus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA do PUT /api/v1/anotacoes/{id}.
 * Não tem livroId: uma anotação não muda de livro depois de criada.
 */
@Schema(description = "Dados de entrada para atualizar página e texto de uma anotação")
public record AnotacaoUpdateDTO(

        @Schema(description = "Nova página da anotação (maior ou igual a 1)", example = "45", minimum = "1")
        @NotNull(message = "A página é obrigatória")
        @Min(value = 1, message = "A página deve ser maior ou igual a 1")
        Integer pagina,

        @Schema(description = "Novo texto da anotação (até 400 caracteres, RN18)",
                example = "Revisão da nota após reler o capítulo.", maxLength = 400)
        @NotBlank(message = "O texto é obrigatório")
        @Size(max = 400, message = "O texto deve ter no máximo 400 caracteres")
        String texto
) {
}
