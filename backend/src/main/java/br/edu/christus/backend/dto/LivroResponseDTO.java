package br.edu.christus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de SAÍDA de livros: devolve ids e nomes de autor e gênero sem expor as entidades.
 * Montado manualmente no LivroService.toResponse.
 */
@Schema(description = "Dados do livro retornados pela API")
public record LivroResponseDTO(
        @Schema(description = "Identificador único do livro", example = "10")
        Long id,
        @Schema(description = "Título do livro", example = "Dom Casmurro")
        String titulo,
        @Schema(description = "Código ISBN único", example = "978-85-359-0277-5")
        String isbn,
        @Schema(description = "Ano de publicação", example = "1899")
        Integer anoPublicacao,
        @Schema(description = "Sinopse da obra", example = "Romance narrado por Bento Santiago (Bentinho).")
        String sinopse,
        @Schema(description = "ID do autor", example = "1")
        Long autorId,
        @Schema(description = "Nome do autor", example = "Machado de Assis")
        String autorNome,
        @Schema(description = "ID do gênero", example = "2")
        Long generoId,
        @Schema(description = "Nome do gênero", example = "Romance")
        String generoNome,
        @Schema(description = "Edição do livro", example = "2ª edição")
        String edicao,
        @Schema(description = "Caminho relativo do PDF armazenado", example = "a1b2c3d4.pdf")
        String pdf
) {
}
