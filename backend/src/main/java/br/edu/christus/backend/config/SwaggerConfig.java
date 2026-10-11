package br.edu.christus.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Biblioteca Virtual Escolar")
                        .version("v1.0.0")
                        .description("API REST desenvolvida para o projeto de Biblioteca Virtual (NP2)")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("jose14jrff@gmail.com")));
    }
}