package br.edu.christus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de SAÍDA de gêneros. Os campos têm os mesmos nomes da entidade (id_genero, name e ativo),
 * então o JSON não muda para quem já usa a rota. Montado no GeneroService.toResponse.
 */
@Schema(description = "Dados do gênero literário retornados pela API")
public record GeneroResponseDTO(
        @Schema(description = "Identificador único do gênero", example = "2")
        Long id_genero,
        @Schema(description = "Nome do gênero literário", example = "Romance")
        String name,
        @Schema(description = "Indica se o gênero está ativo para novos livros (RN12, RN25)", example = "true")
        Boolean ativo
) {
}
