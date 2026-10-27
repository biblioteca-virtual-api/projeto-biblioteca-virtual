package br.edu.christus.backend.controller;

import br.edu.christus.backend.exception.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Validações de entrada das rotas de análise (A13), só na camada web: sem banco e sem segurança.
 * O service é null de propósito: em todos estes casos a requisição é barrada antes de chegar nele
 * (se chegasse, o teste quebraria com NullPointerException).
 */
class UploadControllerTest {

    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new UploadController(null))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    @Test
    void rejeitarComMotivoEmBrancoRetorna400() throws Exception {
        mvc.perform(put("/api/v1/uploads/1/rejeitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\": \"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("motivo: Informe o motivo da rejeição."));
    }

    @Test
    void rejeitarSemCorpoRetorna400ComAMesmaMensagem() throws Exception {
        mvc.perform(put("/api/v1/uploads/1/rejeitar"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("motivo: Informe o motivo da rejeição."));
    }

    @Test
    void rejeitarComMotivoMaiorQue500Retorna400() throws Exception {
        mvc.perform(put("/api/v1/uploads/1/rejeitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\": \"" + "a".repeat(501) + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("motivo: O motivo deve ter no máximo 500 caracteres"));
    }

    @Test
    void aprovarSemIsbnRetorna400() throws Exception {
        mvc.perform(put("/api/v1/uploads/1/aprovar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"novoAutor\": true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("isbn: O ISBN é obrigatório"));
    }

    @Test
    void jsonMalformadoRetorna400() throws Exception {
        mvc.perform(put("/api/v1/uploads/1/aprovar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O corpo da requisição está ausente ou em formato inválido."));
    }

    @Test
    void idNaoNumericoRetorna400() throws Exception {
        mvc.perform(put("/api/v1/uploads/abc/aprovar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\": \"123\", \"novoAutor\": true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Valor inválido para o parâmetro 'id'."));
    }
}
