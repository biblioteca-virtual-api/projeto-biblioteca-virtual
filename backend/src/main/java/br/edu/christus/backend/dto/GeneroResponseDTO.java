package br.edu.christus.backend.dto;

/**
 * DTO de SAÍDA de gêneros. Os campos têm os mesmos nomes da entidade (id_genero, name e ativo),
 * então o JSON não muda para quem já usa a rota. Montado no GeneroService.toResponse.
 */
public record GeneroResponseDTO(
        Long id_genero,
        String name,
        Boolean ativo
) {
}
