package br.edu.christus.backend.controller;

import br.edu.christus.backend.exception.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Validações de entrada do CRUD de gêneros, só na camada web: sem banco e sem segurança.
 * O service fica null de propósito: em todos estes casos a requisição é barrada antes de chegar nele
 * (se chegasse, o teste quebraria com NullPointerException).
 * As rotas usam a barra final (/api/v1/generos/), como no GeneroController.
 */
class GeneroControllerTest {

    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new GeneroController())
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    private ResultActions cadastrar(String json) throws Exception {
        return mvc.perform(post("/api/v1/generos/").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions editar(String json) throws Exception {
        return mvc.perform(put("/api/v1/generos/").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void cadastrarSemNomeRetorna400() throws Exception {
        cadastrar("{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("name: O nome é obrigatório"));
    }

    @Test
    void cadastrarComNomeMaiorQue100CaracteresRetorna400() throws Exception {
        cadastrar("{\"name\": \"" + "a".repeat(101) + "\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("name: O nome deve ter no máximo 100 caracteres"));
    }

    @Test
    void editarSemIdRetorna400() throws Exception {
        // Antes, o id nulo ia direto para o findById, que lança exceção: a API respondia erro 500
        editar("{\"name\": \"Terror\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("id_genero: O id do gênero é obrigatório"));
    }

    @Test
    void editarComNomeEmBrancoRetorna400() throws Exception {
        editar("{\"id_genero\": 1, \"name\": \"   \"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("name: O nome é obrigatório"));
    }
}
