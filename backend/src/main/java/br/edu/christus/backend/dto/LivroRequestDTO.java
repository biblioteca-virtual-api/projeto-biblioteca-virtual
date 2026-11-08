package br.edu.christus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Dados de entrada para cadastro e edição de livros pelo administrador")
public record LivroRequestDTO(

        @Schema(description = "Título do livro", example = "Dom Casmurro", maxLength = 255)
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 255, message = "O título deve ter no máximo 255 caracteres")
        String titulo,

        @Schema(description = "Código ISBN único do livro", example = "978-85-359-0277-5", maxLength = 20)
        @NotBlank(message = "O ISBN é obrigatório")
        @Size(max = 20, message = "O ISBN deve ter no máximo 20 caracteres")
        String isbn,

        @Schema(description = "Ano de publicação", example = "1899")
        Integer anoPublicacao,

        @Schema(description = "Sinopse ou resumo da obra", example = "Romance narrado por Bento Santiago (Bentinho).")
        String sinopse,

        @Schema(description = "ID de um autor já cadastrado (RN14)", example = "1")
        @NotNull(message = "O autor é obrigatório")
        Long autorId,

        @Schema(description = "ID de um gênero ativo (RN12, RN14)", example = "2")
        @NotNull(message = "O gênero é obrigatório")
        Long generoId,

        @Schema(description = "Edição do livro (opcional)", example = "2ª edição", maxLength = 50)
        @Size(max = 50, message = "A edição deve ter no máximo 50 caracteres")
        String edicao,

        @Schema(description = "Caminho relativo do arquivo PDF (opcional)", example = "a1b2c3d4.pdf", maxLength = 500)
        @Size(max = 500, message = "O PDF deve ter no máximo 500 caracteres")
        String pdf
) {
    // Espaços nas pontas não fazem parte do ISBN ("978... " e "978..." seriam ISBNs diferentes no banco).
    public LivroRequestDTO {
        isbn = (isbn == null) ? null : isbn.strip();
    }
}
