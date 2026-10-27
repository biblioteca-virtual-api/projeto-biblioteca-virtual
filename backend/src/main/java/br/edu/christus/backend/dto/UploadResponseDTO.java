package br.edu.christus.backend.dto;

import br.edu.christus.backend.domain.StatusUpload;

/**
 * DTO de SAÍDA dos uploads: devolve ids e nomes de gênero e professor sem expor as entidades.
 * livroId só é preenchido depois da aprovação (RF41). Montado manualmente no UploadService.toResponse.
 */
public record UploadResponseDTO(
        Long id,
        String titulo,
        String autor,
        Long generoId,
        String generoNome,
        String edicao,
        Integer anoPublicacao,
        String pdf,
        Long professorId,
        String professorNome,
        StatusUpload status,
        String motivoRejeicao,
        Long livroId
) {
}
