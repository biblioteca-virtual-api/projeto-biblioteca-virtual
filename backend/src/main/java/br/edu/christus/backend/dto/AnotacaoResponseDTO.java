package br.edu.christus.backend.dto;

/**
 * DTO de SAÍDA das anotações: é o que a API devolve ao cliente.
 * Em vez de expor a entidade Anotacao (com usuário, senha, proxies LAZY...),
 * devolvemos só o necessário. A conversão entidade -> DTO é feita no AnotacaoService.toResponse.
 */
public record AnotacaoResponseDTO(
        Long id,
        Long livroId,
        String livroTitulo,
        Integer pagina,
        String texto
) {
}
