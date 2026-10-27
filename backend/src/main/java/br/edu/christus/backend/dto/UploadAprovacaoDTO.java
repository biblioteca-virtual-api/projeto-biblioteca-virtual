package br.edu.christus.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA do PUT /api/v1/uploads/{id}/aprovar.
 * O Livro é criado com os dados do próprio upload (RF40); aqui vêm só as decisões do administrador:
 * - isbn: o Livro exige ISBN único e o upload não tem esse campo;
 * - autor (RF44), exatamente uma das opções:
 *   autorId = id de um Autor já cadastrado, ou
 *   novoAutor = true para cadastrar um Autor novo com o nome informado no upload (RF43).
 * A escolha é explícita para que um campo esquecido (ou com nome errado) não cadastre
 * um autor duplicado sem ninguém perceber. A checagem fica no UploadService.aprovar.
 */
public record UploadAprovacaoDTO(

        @NotBlank(message = "O ISBN é obrigatório")
        @Size(max = 20, message = "O ISBN deve ter no máximo 20 caracteres")
        String isbn,

        Long autorId,

        Boolean novoAutor
) {
    // Espaços nas pontas não fazem parte do ISBN ("978... " e "978..." seriam ISBNs diferentes no banco).
    public UploadAprovacaoDTO {
        isbn = (isbn == null) ? null : isbn.strip();
    }
}
