package br.edu.christus.backend.dto;

/**
 * DTO de SAÍDA para a listagem e adição de favoritos.
 * Padrão do projeto: DTOs são representados como `record` (imutáveis e concisos).
 */
public record FavoritoResponseDTO(
        Long livroId,
        String livroTitulo,
        String livroIsbn
) {
}
