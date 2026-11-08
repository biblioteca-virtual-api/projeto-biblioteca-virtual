package br.edu.christus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de SAÍDA das anotações: é o que a API devolve ao cliente.
 * Em vez de expor a entidade Anotacao (com usuário, senha, proxies LAZY...),
 * devolvemos só o necessário. A conversão entidade -> DTO é feita no AnotacaoService.toResponse.
 */
@Schema(description = "Dados da anotação privada de leitura retornados pela API")
public record AnotacaoResponseDTO(
        @Schema(description = "Identificador único da anotação", example = "5")
        Long id,
        @Schema(description = "ID do livro", example = "10")
        Long livroId,
        @Schema(description = "Título do livro", example = "Dom Casmurro")
        String livroTitulo,
        @Schema(description = "Número da página do livro", example = "42")
        Integer pagina,
        @Schema(description = "Texto da anotação", example = "Capítulo importante sobre o ponto de vista do narrador.")
        String texto
) {
}
