package br.edu.christus.backend.controller;

import br.edu.christus.backend.domain.TipoUsuario;
import br.edu.christus.backend.domain.Usuario;
import br.edu.christus.backend.repository.UsuarioRepository;
import br.edu.christus.backend.security.TokenService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cadastro e listagem de usuários (A02, RF03) com a cadeia de segurança de verdade:
 * filtro JWT + regras do SecurityConfig, com tokens reais. Cobre as falhas corrigidas:
 * perfil escolhido no cadastro público, "id" no corpo sobrescrevendo outra conta
 * e listagem aberta com e-mail e senha de todos.
 * @Transactional: os usuários criados aqui são desfeitos no fim de cada teste.
 */
@SpringBootTest
@Transactional
class UsuarioSegurancaTest {

    private static final String SENHA = "senha-forte-123";
    private static final String SO_ADMIN = "Só um administrador pode cadastrar professores e administradores.";

    @Autowired
    private WebApplicationContext contexto;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EntityManager em;

    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto)
                .addFilters(contexto.getBean("springSecurityFilterChain", Filter.class))
                .build();
    }

    // E-mail é UNIQUE no banco: o nanoTime evita colisão com dados que já existam
    private String emailNovo() {
        return "usuario." + System.nanoTime() + "@teste.local";
    }

    // Cria direto no banco um usuário do perfil pedido
    private Usuario usuarioDe(TipoUsuario tipo) {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário " + tipo);
        usuario.setEmail(emailNovo());
        usuario.setSenha("senha-nao-usada");
        usuario.setTipoUsuario(tipo);
        return usuarioRepository.save(usuario);
    }

    // Cria um usuário do perfil pedido e devolve o cabeçalho Authorization com o token dele
    private String tokenDe(TipoUsuario tipo) {
        return "Bearer " + tokenService.gerarToken(usuarioDe(tipo));
    }

    // POST de cadastro; com tipoUsuario null, o campo não vai no corpo
    private MockHttpServletRequestBuilder cadastro(String email, String tipoUsuario) {
        String perfil = (tipoUsuario == null) ? "" : ", \"tipoUsuario\": \"" + tipoUsuario + "\"";
        return post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\": \"Usuário Novo\", \"email\": \"" + email + "\", \"senha\": \"" + SENHA + "\""
                        + perfil + "}");
    }

    @Test
    void cadastroPublicoCriaAlunoEGuardaSoOHashDaSenha() throws Exception {
        String email = emailNovo();

        mvc.perform(cadastro(email, null))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.tipoUsuario").value("ALUNO"))
                .andExpect(jsonPath("$.senha").doesNotExist());

        // No banco fica só o hash BCrypt, nunca a senha digitada
        Usuario salvo = usuarioRepository.findByEmail(email).orElseThrow();
        assertEquals(TipoUsuario.ALUNO, salvo.getTipoUsuario());
        assertNotEquals(SENHA, salvo.getSenha());
        assertTrue(passwordEncoder.matches(SENHA, salvo.getSenha()));
    }

    @Test
    void cadastroSemTokenNaoEscolhePerfilPrivilegiado() throws Exception {
        // RF03: no cadastro público, pedir PROFESSOR ou ADMINISTRADOR dá 403 e ninguém é criado
        for (String perfil : List.of("PROFESSOR", "ADMINISTRADOR")) {
            String email = emailNovo();
            mvc.perform(cadastro(email, perfil))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(SO_ADMIN));
            assertTrue(usuarioRepository.findByEmail(email).isEmpty());
        }
    }

    @Test
    void alunoEProfessorLogadosTambemNaoCriamPerfilPrivilegiado() throws Exception {
        for (TipoUsuario quemPede : List.of(TipoUsuario.ALUNO, TipoUsuario.PROFESSOR)) {
            String token = tokenDe(quemPede);
            for (String perfil : List.of("PROFESSOR", "ADMINISTRADOR")) {
                String email = emailNovo();
                mvc.perform(cadastro(email, perfil).header("Authorization", token))
                        .andExpect(status().isForbidden());
                assertTrue(usuarioRepository.findByEmail(email).isEmpty());
            }
        }
    }

    @Test
    void administradorCadastraQualquerPerfil() throws Exception {
        String token = tokenDe(TipoUsuario.ADMINISTRADOR);

        for (TipoUsuario perfil : TipoUsuario.values()) {
            mvc.perform(cadastro(emailNovo(), perfil.name()).header("Authorization", token))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.tipoUsuario").value(perfil.name()));
        }
    }

    @Test
    void idNoCorpoNaoSobrescreveOutraConta() throws Exception {
        Usuario vitima = usuarioDe(TipoUsuario.ADMINISTRADOR);
        // Guarda os valores antes: se o ataque funcionasse, o próprio objeto vitima seria alterado
        Long idVitima = vitima.getId();
        String nomeAntes = vitima.getNome();
        String emailAntes = vitima.getEmail();
        String senhaAntes = vitima.getSenha();
        String emailAtacante = emailNovo();

        // O ataque antigo: mandar o id de uma conta existente para o save() virar UPDATE
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + idVitima + ", \"nome\": \"Atacante\", \"email\": \"" + emailAtacante
                                + "\", \"senha\": \"" + SENHA + "\", \"tipoUsuario\": \"ADMINISTRADOR\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + idVitima + ", \"nome\": \"Atacante\", \"email\": \"" + emailAtacante
                                + "\", \"senha\": \"" + SENHA + "\"}"))
                .andExpect(status().isCreated());
        // Grava no banco e esquece o que estava em memória: a leitura abaixo vem do banco de verdade
        em.flush();
        em.clear();

        // A conta da vítima continua igual; o "id" foi ignorado e o atacante virou um ALUNO novo
        Usuario vitimaNoBanco = usuarioRepository.findById(idVitima).orElseThrow();
        assertEquals(nomeAntes, vitimaNoBanco.getNome());
        assertEquals(emailAntes, vitimaNoBanco.getEmail());
        assertEquals(senhaAntes, vitimaNoBanco.getSenha());
        assertEquals(TipoUsuario.ADMINISTRADOR, vitimaNoBanco.getTipoUsuario());

        Usuario atacante = usuarioRepository.findByEmail(emailAtacante).orElseThrow();
        assertNotEquals(idVitima, atacante.getId());
        assertEquals(TipoUsuario.ALUNO, atacante.getTipoUsuario());
    }

    @Test
    void emailJaCadastradoRetorna409() throws Exception {
        Usuario existente = usuarioDe(TipoUsuario.ALUNO);

        mvc.perform(cadastro(existente.getEmail(), null))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe um usuário cadastrado com este e-mail."));
    }

    @Test
    void senhaComMaisDe72BytesRetorna400() throws Exception {
        // 40 caracteres passam no @Size do DTO, mas cada "ç" ocupa 2 bytes: 80 bytes, acima do limite do BCrypt
        String email = emailNovo();

        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Ana\", \"email\": \"" + email + "\", \"senha\": \"" + "ç".repeat(40) + "\"}"))
                .andExpect(status().isBadRequest());
        assertTrue(usuarioRepository.findByEmail(email).isEmpty());
    }

    @Test
    void listagemSoParaAdministradorESemSenha() throws Exception {
        // RF03: sem token, ALUNO e PROFESSOR recebem 403, tanto no GET quanto no HEAD
        mvc.perform(get("/api/v1/users")).andExpect(status().isForbidden());
        for (TipoUsuario tipo : List.of(TipoUsuario.ALUNO, TipoUsuario.PROFESSOR)) {
            String token = tokenDe(tipo);
            mvc.perform(get("/api/v1/users").header("Authorization", token)).andExpect(status().isForbidden());
            mvc.perform(head("/api/v1/users").header("Authorization", token)).andExpect(status().isForbidden());
        }

        Usuario admin = usuarioDe(TipoUsuario.ADMINISTRADOR);
        mvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + tokenService.gerarToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", hasItem(admin.getEmail())))
                .andExpect(jsonPath("$[*].senha").doesNotExist());
    }
}
