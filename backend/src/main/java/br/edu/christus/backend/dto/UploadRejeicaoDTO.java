package br.edu.christus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA do PUT /api/v1/uploads/{id}/rejeitar.
 * RN06: a rejeição exige motivo; sem ele o @Valid do controller devolve 400.
 */
@Schema(description = "Motivo obrigatório para rejeição de um upload pelo administrador (RN06)")
public record UploadRejeicaoDTO(

        @Schema(description = "Justificativa da rejeição exibida ao professor (até 500 caracteres)",
                example = "O arquivo PDF enviado está incompleto; reenvie com todas as páginas.", maxLength = 500)
        @NotBlank(message = "Informe o motivo da rejeição.")
        @Size(max = 500, message = "O motivo deve ter no máximo 500 caracteres")
        String motivo
) {
    // Tira os espaços das pontas antes da validação: o @NotBlank e o @Size avaliam o texto
    // que será gravado. strip() usa a mesma noção de espaço do @NotBlank, então o motivo nunca fica vazio.
    public UploadRejeicaoDTO {
        motivo = (motivo == null) ? null : motivo.strip();
    }
}
