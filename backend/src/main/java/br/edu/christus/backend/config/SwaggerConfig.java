package br.edu.christus.backend.config;

import br.edu.christus.backend.controller.AuthController;
import br.edu.christus.backend.controller.UsuarioController;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

// Configuração do Swagger / OpenAPI (issue #14): passo a passo de autenticação JWT,
// ordem das tags e resposta 500 global para todas as rotas.
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("API - Biblioteca Virtual Escolar")
                        .version("v1.0.0")
                        .description("""
                                API REST desenvolvida para o projeto de Biblioteca Virtual Escolar (NP2).

                                ### Como se autenticar (JWT)
                                1. Faça `POST /api/v1/auth/login` com seu e-mail e senha.
                                2. Copie o valor do campo `token` retornado na resposta.
                                3. Clique no botão **Authorize** no topo da página e cole **só o token**, sem a palavra `Bearer`.
                                4. O token vale por **2 horas**; com `persist-authorization` ativo, ele permanece salvo ao recarregar a página.

                                ### Perfis de acesso (`TipoUsuario`)
                                - `ALUNO`: consulta autores/gêneros e gerencia seus próprios favoritos e anotações.
                                - `PROFESSOR`: além das permissões de leitura, envia livros em PDF para análise (`POST /api/v1/uploads`), consulta seus envios e corrige/reenvia uploads rejeitados.
                                - `ADMINISTRADOR`: gerencia usuários, autores, gêneros, acervo de livros e analisa a fila de uploads pendentes.
                                """)
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("seu.email@christus.edu.br")))
                .tags(List.of(
                        new Tag().name("Autenticação").description("Login e emissão de token JWT (RF01, RF02)"),
                        new Tag().name("Usuários").description("Cadastro público de alunos e gestão de contas pelo administrador (RF03, RN01)"),
                        new Tag().name("Livros").description("Gestão do acervo de livros pelo administrador (RF04 a RF08, RN02, RN12, RN14, RN22)"),
                        new Tag().name("Autores").description("Consulta e gestão de autores (RF26, RN24)"),
                        new Tag().name("Gêneros").description("Consulta e gestão de gêneros literários (RF27 a RF29, RN13, RN25)"),
                        new Tag().name("Anotações").description("Anotações privadas de leitura do próprio usuário (RF18 a RF25, RN17 a RN19)"),
                        new Tag().name("Favoritos").description("Lista de livros favoritos do próprio usuário (RF14 a RF17, RN20)"),
                        new Tag().name("Uploads").description("Envio de PDFs pelo professor e análise pelo administrador (RF30, RF31, RF36 a RF44, RN04 a RN10)")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    // Adiciona a resposta 500 (StandardError) em todas as operações, remove o 200 padrão
    // quando a operação já declarou 201 (Created) ou 204 (No Content) e garante security: []
    // nas rotas públicas de login e cadastro de usuário.
    @Bean
    public OperationCustomizer globalResponsesCustomizer() {
        return (operation, handlerMethod) -> {
            if ((handlerMethod.getBeanType().equals(AuthController.class)
                    && "login".equals(handlerMethod.getMethod().getName()))
                    || (handlerMethod.getBeanType().equals(UsuarioController.class)
                    && "create".equals(handlerMethod.getMethod().getName()))) {
                operation.setSecurity(Collections.emptyList());
            }
            ApiResponses responses = operation.getResponses();
            if (responses != null) {
                if ((responses.containsKey("201") || responses.containsKey("204"))
                        && responses.containsKey("200")) {
                    responses.remove("200");
                }
                responses.putIfAbsent("500", new ApiResponse()
                        .description("Erro interno inesperado")
                        .content(new Content().addMediaType("application/json",
                                new MediaType().schema(new Schema<>().$ref("#/components/schemas/StandardError")))));
            }
            return operation;
        };
    }
}
