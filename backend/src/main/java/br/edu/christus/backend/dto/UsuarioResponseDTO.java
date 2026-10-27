package br.edu.christus.backend.dto;

import br.edu.christus.backend.domain.TipoUsuario;

/**
 * DTO de SAÍDA de usuários: é o que a API devolve no cadastro e na listagem.
 * Não tem a senha: nem o hash BCrypt pode sair da API, porque com ele dá para tentar
 * descobrir a senha por tentativa e erro fora do sistema. Montado no UsuarioService.toResponse.
 */
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        TipoUsuario tipoUsuario
) {
}
