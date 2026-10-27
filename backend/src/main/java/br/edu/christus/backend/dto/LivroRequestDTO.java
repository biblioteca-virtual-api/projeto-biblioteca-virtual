package br.edu.christus.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA do POST e do PUT de livros.
 * O cliente envia só os IDs de autor e gênero (RN14); o LivroService busca as
 * entidades e aplica a RN12 (gênero ativo).
 * Edição e PDF são opcionais aqui; o livro criado na aprovação de um upload recebe os dois (RF40).
 * O PUT substitui todos os campos: para não apagar a edição e o PDF, reenvie os valores
 * que vieram no GET (o mesmo vale para sinopse e ano de publicação).
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
        Long generoId,

        @Size(max = 50, message = "A edição deve ter no máximo 50 caracteres")
        String edicao,

        @Size(max = 500, message = "O PDF deve ter no máximo 500 caracteres")
        String pdf
) {
    // Espaços nas pontas não fazem parte do ISBN ("978... " e "978..." seriam ISBNs diferentes no banco).
    public LivroRequestDTO {
        isbn = (isbn == null) ? null : isbn.strip();
    }
}
