package br.edu.christus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA do POST e do PUT de autores.
 *
 * Não tem id de propósito: quando o controller recebia a entidade Autor, um POST com o "id"
 * de um autor existente fazia o save() virar UPDATE e sobrescrever esse autor. Com o DTO,
 * o POST sempre cria um autor novo e o PUT altera só o autor do id da URL.
 * O PUT substitui os dois campos: sem biografia, a biografia salva é apagada.
 */
@Schema(description = "Dados de entrada para cadastro e edição de autor")
public record AutorRequestDTO(

        @Schema(description = "Nome completo do autor", example = "Machado de Assis", maxLength = 255)
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String nome,

        @Schema(description = "Biografia resumida do autor (opcional)",
                example = "Escritor brasileiro, fundador da Academia Brasileira de Letras.")
        String biografia
) {
}
