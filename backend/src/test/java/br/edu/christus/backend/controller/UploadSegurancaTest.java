package br.edu.christus.backend.controller;

import br.edu.christus.backend.domain.TipoUsuario;
import br.edu.christus.backend.domain.Usuario;
import br.edu.christus.backend.repository.UsuarioRepository;
import br.edu.christus.backend.security.TokenService;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * RF36 a RF39: só o ADMINISTRADOR acessa as rotas de análise de upload.
 * Usa a cadeia de segurança de verdade (filtro JWT + regras do SecurityConfig) e tokens reais.
 * @Transactional: os usuários criados aqui são desfeitos no fim de cada teste.
 */
@SpringBootTest
@Transactional
class UploadSegurancaTest {

    @Autowired
    private WebApplicationContext contexto;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto)
                .addFilters(contexto.getBean("springSecurityFilterChain", Filter.class))
                .build();
    }

    // Cria um usuário do perfil pedido e devolve o cabeçalho Authorization com o token dele
    private String tokenDe(TipoUsuario tipo) {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário " + tipo);
        usuario.setEmail(tipo.name().toLowerCase() + "." + System.nanoTime() + "@teste.local");
        usuario.setSenha("senha-nao-usada");
        usuario.setTipoUsuario(tipo);
        usuarioRepository.save(usuario);
        return "Bearer " + tokenService.gerarToken(usuario);
    }

    // As três rotas de análise, com corpos válidos (para a resposta depender só da permissão)
    private List<MockHttpServletRequestBuilder> rotasDeAnalise() {
        return List.of(
                get("/api/v1/uploads/pendentes"),
                put("/api/v1/uploads/-1/aprovar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\": \"123\", \"novoAutor\": true}"),
                put("/api/v1/uploads/-1/rejeitar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\": \"motivo\"}"));
    }

    @Test
    void professorEAlunoRecebem403() throws Exception {
        for (TipoUsuario tipo : List.of(TipoUsuario.PROFESSOR, TipoUsuario.ALUNO)) {
            String token = tokenDe(tipo);
            for (MockHttpServletRequestBuilder rota : rotasDeAnalise()) {
                mvc.perform(rota.header("Authorization", token)).andExpect(status().isForbidden());
            }
        }
    }

    @Test
    void semTokenRecebe403() throws Exception {
        for (MockHttpServletRequestBuilder rota : rotasDeAnalise()) {
            mvc.perform(rota).andExpect(status().isForbidden());
        }
    }

    @Test
    void administradorPassaPelaSeguranca() throws Exception {
        String token = tokenDe(TipoUsuario.ADMINISTRADOR);

        mvc.perform(get("/api/v1/uploads/pendentes").header("Authorization", token))
                .andExpect(status().isOk());
        // O upload -1 não existe: 404 mostra que a requisição passou da segurança e chegou ao service
        for (MockHttpServletRequestBuilder rota : rotasDeAnalise().subList(1, 3)) {
            mvc.perform(rota.header("Authorization", token)).andExpect(status().isNotFound());
        }
    }
}
