package br.edu.christus.backend.dto;

import br.edu.christus.backend.domain.TipoUsuario;

public record LoginResponseDTO(
        String token,
        Long id,
        String nome,
        String email,
        TipoUsuario tipoUsuario
) {
}