package br.edu.christus.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA do POST e do PUT de livros.
 * O cliente envia só os IDs de autor e gênero (RN14); o LivroService busca as
 * entidades e aplica a RN12 (gênero ativo).
 */
public record LivroRequestDTO(

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 255, message = "O título deve ter no máximo 255 caracteres")
        String titulo,

        @NotBlank(message = "O ISBN é obrigatório")
        @Size(max = 20, message = "O ISBN deve ter no máximo 20 caracteres")
        String isbn,

        Integer anoPublicacao,

        String sinopse,

        @NotNull(message = "O autor é obrigatório")
        Long autorId,

        @NotNull(message = "O gênero é obrigatório")
        Long generoId
) {
}
