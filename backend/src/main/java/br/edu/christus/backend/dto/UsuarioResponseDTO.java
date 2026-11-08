package br.edu.christus.backend.dto;

import br.edu.christus.backend.domain.TipoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de SAÍDA de usuários: é o que a API devolve no cadastro e na listagem.
 * Não tem a senha: nem o hash BCrypt pode sair da API, porque com ele dá para tentar
 * descobrir a senha por tentativa e erro fora do sistema. Montado no UsuarioService.toResponse.
 */
@Schema(description = "Dados públicos do usuário (nunca inclui a senha)")
public record UsuarioResponseDTO(
        @Schema(description = "Identificador único do usuário", example = "1")
        Long id,
        @Schema(description = "Nome completo do usuário", example = "Ana Souza")
        String nome,
        @Schema(description = "E-mail do usuário", example = "ana.souza@escola.exemplo")
        String email,
        @Schema(description = "Perfil de acesso (ALUNO, PROFESSOR ou ADMINISTRADOR)", example = "ALUNO")
        TipoUsuario tipoUsuario
) {
}
