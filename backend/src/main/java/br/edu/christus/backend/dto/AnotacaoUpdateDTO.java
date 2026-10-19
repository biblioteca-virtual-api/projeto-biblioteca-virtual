package br.edu.christus.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA do PUT /api/v1/anotacoes/{id}.
 * Não tem livroId: uma anotação não muda de livro depois de criada.
 */
public record AnotacaoUpdateDTO(

        @NotNull(message = "A página é obrigatória")
        @Min(value = 1, message = "A página deve ser maior ou igual a 1")
        Integer pagina,

        @NotBlank(message = "O texto é obrigatório")
        @Size(max = 400, message = "O texto deve ter no máximo 400 caracteres")
        String texto
) {
}
