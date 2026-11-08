package br.edu.christus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciais para autenticação na API (RF01)")
public record LoginRequestDTO(
        @Schema(description = "E-mail cadastrado do usuário", example = "ana.souza@escola.exemplo")
        String email,
        @Schema(description = "Senha em texto plano", example = "SenhaSegura123")
        String senha
) {
}
