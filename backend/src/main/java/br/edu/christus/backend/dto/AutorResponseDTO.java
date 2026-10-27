package br.edu.christus.backend.dto;

/**
 * DTO de SAÍDA de autores: tem os mesmos campos que a API já devolvia (id, nome e biografia),
 * então o JSON não muda para quem já usa a rota. Montado no AutorService.toResponse.
 */
public record AutorResponseDTO(
        Long id,
        String nome,
        String biografia
) {
}
