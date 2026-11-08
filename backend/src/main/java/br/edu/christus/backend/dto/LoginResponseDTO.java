package br.edu.christus.backend.dto;

import br.edu.christus.backend.domain.TipoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados após login com sucesso (RF01, RF02)")
public record LoginResponseDTO(
        @Schema(description = "Token JWT válido por 2 horas (use no botão Authorize sem o prefixo Bearer)",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,
        @Schema(description = "Identificador único do usuário", example = "1")
        Long id,
        @Schema(description = "Nome completo do usuário", example = "Ana Souza")
        String nome,
        @Schema(description = "E-mail do usuário", example = "ana.souza@escola.exemplo")
        String email,
        @Schema(description = "Perfil de acesso do usuário (ALUNO, PROFESSOR ou ADMINISTRADOR)", example = "ALUNO")
        TipoUsuario tipoUsuario
) {
}
