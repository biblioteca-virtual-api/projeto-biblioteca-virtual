package br.edu.christus.backend.controller;

import br.edu.christus.backend.exception.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Validações de entrada do cadastro de usuário, só na camada web: sem banco e sem segurança.
 * O service fica null de propósito: em todos estes casos a requisição é barrada antes de chegar nele
 * (se chegasse, o teste quebraria com NullPointerException).
 */
class UsuarioControllerTest {

    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new UsuarioController())
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    private ResultActions cadastrar(String json) throws Exception {
        return mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void semNomeRetorna400() throws Exception {
        cadastrar("{\"email\": \"ana@teste.local\", \"senha\": \"senha-forte-123\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("nome: O nome é obrigatório"));
    }

    @Test
    void emailInvalidoRetorna400() throws Exception {
        cadastrar("{\"nome\": \"Ana\", \"email\": \"ana-sem-arroba\", \"senha\": \"senha-forte-123\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("email: Informe um e-mail válido"));
    }

    @Test
    void semSenhaRetorna400() throws Exception {
        cadastrar("{\"nome\": \"Ana\", \"email\": \"ana@teste.local\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("senha: A senha é obrigatória"));
    }

    @Test
    void senhaComMenosDe8CaracteresRetorna400() throws Exception {
        cadastrar("{\"nome\": \"Ana\", \"email\": \"ana@teste.local\", \"senha\": \"1234567\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("senha: A senha deve ter entre 8 e 72 caracteres"));
    }

    @Test
    void perfilInexistenteRetorna400() throws Exception {
        cadastrar("{\"nome\": \"Ana\", \"email\": \"ana@teste.local\", \"senha\": \"senha-forte-123\", "
                + "\"tipoUsuario\": \"SUPERUSUARIO\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O corpo da requisição está ausente ou em formato inválido."));
    }
}
