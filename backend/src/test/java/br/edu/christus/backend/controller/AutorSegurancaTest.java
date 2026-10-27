package br.edu.christus.backend.controller;

import br.edu.christus.backend.domain.Autor;
import br.edu.christus.backend.domain.TipoUsuario;
import br.edu.christus.backend.domain.Usuario;
import br.edu.christus.backend.repository.AutorRepository;
import br.edu.christus.backend.repository.UsuarioRepository;
import br.edu.christus.backend.security.TokenService;
import jakarta.persistence.EntityManager;
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

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CRUD de autores (A03, RF26/RN02) com a cadeia de segurança de verdade:
 * filtro JWT + regras do SecurityConfig, com tokens reais. Cobre as falhas corrigidas:
 * ALUNO e PROFESSOR cadastrando, editando e excluindo autores, e o "id" no corpo do POST
 * sobrescrevendo um autor existente.
 * @Transactional: os usuários e autores criados aqui são desfeitos no fim de cada teste.
 */
@SpringBootTest
@Transactional
class AutorSegurancaTest {

    private static final String BIOGRAFIA = "Biografia original";

    @Autowired
    private WebApplicationContext contexto;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AutorRepository autorRepository;
    @Autowired
    private EntityManager em;

    private MockMvc mvc;
    private Autor autor;
    private String nomeOriginal;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto)
                .addFilters(contexto.getBean("springSecurityFilterChain", Filter.class))
                .build();

        // Autor já cadastrado que os testes tentam consultar, editar e excluir. O nome fica guardado à parte:
        // se uma alteração indevida passasse, o próprio objeto autor seria alterado junto.
        nomeOriginal = "Machado de Assis " + System.nanoTime();
        autor = new Autor();
        autor.setNome(nomeOriginal);
        autor.setBiografia(BIOGRAFIA);
        autorRepository.save(autor);
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

    // As rotas que alteram autores, com corpos válidos (para a resposta depender só da permissão).
    // O PATCH nem existe no controller: está aqui para mostrar que todo método que não é GET
    // cai na regra do administrador.
    private List<MockHttpServletRequestBuilder> rotasDeEscrita(String nome) {
        String corpo = "{\"nome\": \"" + nome + "\", \"biografia\": \"Outra biografia\"}";
        return List.of(
                post("/api/v1/autores").contentType(MediaType.APPLICATION_JSON).content(corpo),
                put("/api/v1/autores/" + autor.getId()).contentType(MediaType.APPLICATION_JSON).content(corpo),
                patch("/api/v1/autores/" + autor.getId()).contentType(MediaType.APPLICATION_JSON).content(corpo),
                delete("/api/v1/autores/" + autor.getId()));
    }

    // Autores do banco com o nome pedido (os nomes dos testes são únicos por causa do nanoTime)
    private List<Autor> autoresComNome(String nome) {
        return autorRepository.findAll().stream().filter(a -> nome.equals(a.getNome())).toList();
    }

    @Test
    void alunoEProfessorNaoCadastramEditamNemExcluemAutores() throws Exception {
        String nomeIndevido = "Autor Indevido " + System.nanoTime();

        for (TipoUsuario tipo : List.of(TipoUsuario.ALUNO, TipoUsuario.PROFESSOR)) {
            String token = tokenDe(tipo);
            for (MockHttpServletRequestBuilder rota : rotasDeEscrita(nomeIndevido)) {
                mvc.perform(rota.header("Authorization", token)).andExpect(status().isForbidden());
            }
        }
        // Grava no banco e esquece o que estava em memória: a leitura abaixo vem do banco de verdade
        em.flush();
        em.clear();

        // Nada mudou: nenhum autor foi criado ou renomeado, e o existente continua lá, igual
        assertTrue(autoresComNome(nomeIndevido).isEmpty());
        Autor noBanco = autorRepository.findById(autor.getId()).orElseThrow();
        assertEquals(nomeOriginal, noBanco.getNome());
        assertEquals(BIOGRAFIA, noBanco.getBiografia());
    }

    @Test
    void semTokenRecebe403() throws Exception {
        mvc.perform(get("/api/v1/autores")).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/autores/" + autor.getId())).andExpect(status().isForbidden());
        for (MockHttpServletRequestBuilder rota : rotasDeEscrita("Sem Token")) {
            mvc.perform(rota).andExpect(status().isForbidden());
        }
    }

    @Test
    void qualquerPerfilLogadoListaEConsultaAutores() throws Exception {
        for (TipoUsuario tipo : TipoUsuario.values()) {
            String token = tokenDe(tipo);
            mvc.perform(get("/api/v1/autores").header("Authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[*].nome", hasItem(nomeOriginal)));
            mvc.perform(get("/api/v1/autores/" + autor.getId()).header("Authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value(nomeOriginal))
                    .andExpect(jsonPath("$.biografia").value(BIOGRAFIA));
        }
    }

    @Test
    void administradorCadastraEditaEExcluiAutores() throws Exception {
        String token = tokenDe(TipoUsuario.ADMINISTRADOR);
        String nomeNovo = "Clarice Lispector " + System.nanoTime();

        mvc.perform(post("/api/v1/autores").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"" + nomeNovo + "\", \"biografia\": \"Escritora\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value(nomeNovo))
                .andExpect(jsonPath("$.biografia").value("Escritora"));

        mvc.perform(put("/api/v1/autores/" + autor.getId()).header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Nome Editado\", \"biografia\": \"Biografia editada\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(autor.getId()))
                .andExpect(jsonPath("$.nome").value("Nome Editado"))
                .andExpect(jsonPath("$.biografia").value("Biografia editada"));

        mvc.perform(delete("/api/v1/autores/" + autor.getId()).header("Authorization", token))
                .andExpect(status().isNoContent());
        em.flush();
        em.clear();
        assertTrue(autorRepository.findById(autor.getId()).isEmpty());
    }

    @Test
    void idNoCorpoNaoSobrescreveOutroAutor() throws Exception {
        String token = tokenDe(TipoUsuario.ADMINISTRADOR);
        String nomeNovo = "Autor Novo " + System.nanoTime();

        // O ataque antigo: mandar o id de um autor existente para o save() virar UPDATE
        mvc.perform(post("/api/v1/autores").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + autor.getId() + ", \"nome\": \"" + nomeNovo + "\"}"))
                .andExpect(status().isOk());
        em.flush();
        em.clear();

        // O autor existente continua igual; o "id" foi ignorado e um autor novo foi criado
        Autor existente = autorRepository.findById(autor.getId()).orElseThrow();
        assertEquals(nomeOriginal, existente.getNome());
        assertEquals(BIOGRAFIA, existente.getBiografia());
        List<Autor> criados = autoresComNome(nomeNovo);
        assertEquals(1, criados.size());
        assertNotEquals(autor.getId(), criados.get(0).getId());
    }
}
