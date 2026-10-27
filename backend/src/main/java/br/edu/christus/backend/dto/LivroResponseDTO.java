package br.edu.christus.backend.dto;

/**
 * DTO de SAÍDA de livros: devolve ids e nomes de autor e gênero sem expor as entidades.
 * Montado manualmente no LivroService.toResponse.
 */
public record LivroResponseDTO(
        Long id,
        String titulo,
        String isbn,
        Integer anoPublicacao,
        String sinopse,
        Long autorId,
        String autorNome,
        Long generoId,
        String generoNome,
        String edicao,
        String pdf
) {
}
