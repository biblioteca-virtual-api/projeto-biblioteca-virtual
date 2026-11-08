package br.edu.christus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de SAÍDA para a listagem e adição de favoritos.
 * Padrão do projeto: DTOs são representados como `record` (imutáveis e concisos).
 */
@Schema(description = "Dados de um livro favoritado pelo usuário (RF14 a RF17)")
public record FavoritoResponseDTO(
        @Schema(description = "Identificador único do livro favoritado", example = "10")
        Long livroId,
        @Schema(description = "Título do livro favoritado", example = "Dom Casmurro")
        String livroTitulo,
        @Schema(description = "ISBN do livro favoritado", example = "978-85-359-0277-5")
        String livroIsbn
) {
}
