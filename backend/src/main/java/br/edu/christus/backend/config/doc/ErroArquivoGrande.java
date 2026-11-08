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
@ApiResponse(responseCode = "413",
        description = "Arquivo PDF acima do limite máximo de 5 MB",
        content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = StandardError.class)))
public @interface ErroArquivoGrande {
}
