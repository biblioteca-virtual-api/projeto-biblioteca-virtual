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
 * Validações de entrada do CRUD de autores, só na camada web: sem banco e sem segurança.
 * O service fica null de propósito: em todos estes casos a requisição é barrada antes de chegar nele
 * (se chegasse, o teste quebraria com NullPointerException).
 */
class AutorControllerTest {

    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new AutorController())
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    private ResultActions cadastrar(String json) throws Exception {
        return mvc.perform(post("/api/v1/autores").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void semNomeRetorna400() throws Exception {
        cadastrar("{\"biografia\": \"Escritor brasileiro\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("nome: O nome é obrigatório"));
    }

    @Test
    void nomeEmBrancoRetorna400() throws Exception {
        cadastrar("{\"nome\": \"   \"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("nome: O nome é obrigatório"));
    }

    @Test
    void nomeComMaisDe255CaracteresRetorna400() throws Exception {
        cadastrar("{\"nome\": \"" + "a".repeat(256) + "\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("nome: O nome deve ter no máximo 255 caracteres"));
    }

    @Test
    void editarSemNomeRetorna400() throws Exception {
        // O PUT usa o mesmo DTO do cadastro: o nome também é obrigatório na edição
        mvc.perform(put("/api/v1/autores/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("nome: O nome é obrigatório"));
    }
}
