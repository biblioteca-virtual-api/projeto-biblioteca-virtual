package br.edu.christus.backend.config.doc;

import br.edu.christus.backend.exception.StandardError;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "400",
        description = "Dados de entrada inválidos, JSON malformado ou parâmetro com tipo incorreto",
        content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = StandardError.class)))
public @interface ErroDadosInvalidos {
}
