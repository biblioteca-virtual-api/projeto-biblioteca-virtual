package br.edu.christus.backend.dto;

import br.edu.christus.backend.domain.StatusUpload;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de SAÍDA dos uploads: devolve ids e nomes de gênero e professor sem expor as entidades.
 * livroId só é preenchido depois da aprovação (RF41). Montado manualmente no UploadService.toResponse.
 */
@Schema(description = "Dados do upload de livro retornados pela API")
public record UploadResponseDTO(
        @Schema(description = "Identificador único do upload", example = "3")
        Long id,
        @Schema(description = "Título informado no upload", example = "Dom Casmurro")
        String titulo,
        @Schema(description = "Nome do autor informado no upload", example = "Machado de Assis")
        String autor,
        @Schema(description = "ID do gênero literário", example = "2")
        Long generoId,
        @Schema(description = "Nome do gênero literário", example = "Romance")
        String generoNome,
        @Schema(description = "Edição informada no upload", example = "2ª edição")
        String edicao,
        @Schema(description = "Ano de publicação", example = "1899")
        Integer anoPublicacao,
        @Schema(description = "Nome do arquivo PDF salvo no servidor", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf")
        String pdf,
        @Schema(description = "ID do professor que enviou", example = "4")
        Long professorId,
        @Schema(description = "Nome do professor que enviou", example = "Carlos Lima")
        String professorNome,
        @Schema(description = "Status atual do upload (PENDENTE, APROVADO ou REJEITADO)", example = "PENDENTE")
        StatusUpload status,
        @Schema(description = "Motivo da rejeição (preenchido apenas quando REJEITADO)", nullable = true, example = "null")
        String motivoRejeicao,
        @Schema(description = "ID do Livro criado no acervo (preenchido apenas quando APROVADO)", nullable = true, example = "10")
        Long livroId
) {
}
