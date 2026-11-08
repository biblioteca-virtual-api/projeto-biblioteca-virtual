package br.edu.christus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA do POST /api/v1/generos/ (cadastro de gênero).
 *
 * Só tem o nome: o id é gerado pelo banco e todo gênero nasce ativo. Quando o controller recebia
 * a entidade Genero, um POST com o "id_genero" de um gênero existente fazia o save() virar UPDATE:
 * trocava o nome dele e ainda o reativava, se estivesse desativado (RN25).
 * O campo se chama "name", como na entidade, para o JSON continuar o mesmo de antes.
 */
@Schema(description = "Dados de entrada para cadastro de gênero literário")
public record GeneroRequestDTO(

        @Schema(description = "Nome do gênero literário", example = "Romance", maxLength = 100)
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String name
) {
}
