package br.edu.christus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de SAÍDA de autores: tem os mesmos campos que a API já devolvia (id, nome e biografia),
 * então o JSON não muda para quem já usa a rota. Montado no AutorService.toResponse.
 */
@Schema(description = "Dados do autor retornados pela API")
public record AutorResponseDTO(
        @Schema(description = "Identificador único do autor", example = "1")
        Long id,
        @Schema(description = "Nome completo do autor", example = "Machado de Assis")
        String nome,
        @Schema(description = "Biografia resumida do autor",
                example = "Escritor brasileiro, fundador da Academia Brasileira de Letras.")
        String biografia
) {
}
