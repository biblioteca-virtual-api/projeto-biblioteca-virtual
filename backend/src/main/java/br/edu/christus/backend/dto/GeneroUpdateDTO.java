package br.edu.christus.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA do PUT /api/v1/generos/ (edição de gênero).
 *
 * A rota não tem o id na URL (ficou assim para não quebrar quem já a usa), então o id vem no corpo
 * e é obrigatório: sem ele, a busca recebia null e a API respondia erro 500.
 * Não tem o campo "ativo": a edição troca só o nome. Quem desativa é o DELETE (RN25).
 */
public record GeneroUpdateDTO(

        @NotNull(message = "O id do gênero é obrigatório")
        Long id_genero,

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String name
) {
}
