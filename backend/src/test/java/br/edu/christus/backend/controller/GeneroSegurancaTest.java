package br.edu.christus.backend.controller;

import br.edu.christus.backend.domain.Genero;
import br.edu.christus.backend.domain.TipoUsuario;
import br.edu.christus.backend.domain.Usuario;
import br.edu.christus.backend.repository.GeneroRepository;
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
 * CRUD de gêneros (A04, RF27/RF28) com a cadeia de segurança de verdade:
 * filtro JWT + regras do SecurityConfig, com tokens reais. Cobre as falhas corrigidas:
 * ALUNO e PROFESSOR cadastrando, editando e excluindo gêneros, e o "id_genero" no corpo do POST
 * sobrescrevendo (e até reativando) um gênero existente.
 * As rotas mantêm a barra final de /api/v1/generos/, como no GeneroController.
 * @Transactional: os usuários e gêneros criados aqui são desfeitos no fim de cada teste.
 */
@SpringBootTest
@Transactional
class GeneroSegurancaTest {

    @Autowired
    private WebApplicationContext contexto;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private GeneroRepository generoRepository;
    @Autowired
    private EntityManager em;

    private MockMvc mvc;
    private Genero genero;
    private String nomeOriginal;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto)
                .addFilters(contexto.getBean("springSecurityFilterChain", Filter.class))
                .build();

        // Gênero já cadastrado (ativo) que os testes tentam listar, editar e excluir. O nome fica guardado
        // à parte: se uma alteração indevida passasse, o próprio objeto genero seria alterado junto.
        nomeOriginal = "Romance " + System.nanoTime();
        genero = new Genero();
        genero.setName(nomeOriginal);
        generoRepository.save(genero);
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

    // As rotas que alteram gêneros, com corpos válidos (para a resposta depender só da permissão).
    // O PATCH nem existe no controller: está aqui para mostrar que todo método que não é GET
    // cai na regra do administrador.
    private List<MockHttpServletRequestBuilder> rotasDeEscrita(String nome) {
        String corpoEdicao = "{\"id_genero\": " + genero.getId_genero() + ", \"name\": \"" + nome + "\"}";
        return List.of(
                post("/api/v1/generos/").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"" + nome + "\"}"),
                put("/api/v1/generos/").contentType(MediaType.APPLICATION_JSON).content(corpoEdicao),
                patch("/api/v1/generos/").contentType(MediaType.APPLICATION_JSON).content(corpoEdicao),
                delete("/api/v1/generos/" + genero.getId_genero()));
    }

    // Gêneros do banco com o nome pedido (os nomes dos testes são únicos por causa do nanoTime)
    private List<Genero> generosComNome(String nome) {
        return generoRepository.findAll().stream().filter(g -> nome.equals(g.getName())).toList();
    }

    @Test
    void alunoEProfessorNaoCadastramEditamNemExcluemGeneros() throws Exception {
        String nomeIndevido = "Genero Indevido " + System.nanoTime();

        for (TipoUsuario tipo : List.of(TipoUsuario.ALUNO, TipoUsuario.PROFESSOR)) {
            String token = tokenDe(tipo);
            for (MockHttpServletRequestBuilder rota : rotasDeEscrita(nomeIndevido)) {
                mvc.perform(rota.header("Authorization", token)).andExpect(status().isForbidden());
            }
        }
        // Grava no banco e esquece o que estava em memória: a leitura abaixo vem do banco de verdade
        em.flush();
        em.clear();

        // Nada mudou: nenhum gênero foi criado ou renomeado, e o existente continua lá, ativo
        assertTrue(generosComNome(nomeIndevido).isEmpty());
        Genero noBanco = generoRepository.findById(genero.getId_genero()).orElseThrow();
        assertEquals(nomeOriginal, noBanco.getName());
        assertTrue(noBanco.getAtivo());
    }

    @Test
    void semTokenRecebe403() throws Exception {
        mvc.perform(get("/api/v1/generos/")).andExpect(status().isForbidden());
        for (MockHttpServletRequestBuilder rota : rotasDeEscrita("Sem Token")) {
            mvc.perform(rota).andExpect(status().isForbidden());
        }
    }

    @Test
    void qualquerPerfilLogadoListaGeneros() throws Exception {
        // O professor escolhe o gênero ao enviar um upload (RF30): a listagem fica aberta aos perfis logados
        for (TipoUsuario tipo : TipoUsuario.values()) {
            mvc.perform(get("/api/v1/generos/").header("Authorization", tokenDe(tipo)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[*].name", hasItem(nomeOriginal)));
        }
    }

    @Test
    void administradorCadastraEditaEExcluiGeneros() throws Exception {
        String token = tokenDe(TipoUsuario.ADMINISTRADOR);
        String nomeNovo = "Fantasia " + System.nanoTime();

        // Todo gênero nasce ativo
        mvc.perform(post("/api/v1/generos/").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"" + nomeNovo + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_genero").isNumber())
                .andExpect(jsonPath("$.name").value(nomeNovo))
                .andExpect(jsonPath("$.ativo").value(true));

        mvc.perform(put("/api/v1/generos/").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id_genero\": " + genero.getId_genero() + ", \"name\": \"Nome Editado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_genero").value(genero.getId_genero()))
                .andExpect(jsonPath("$.name").value("Nome Editado"))
                .andExpect(jsonPath("$.ativo").value(true));

        // Sem livros nem uploads, o gênero é excluído de verdade (com vínculos, só seria desativado: RN25)
        mvc.perform(delete("/api/v1/generos/" + genero.getId_genero()).header("Authorization", token))
                .andExpect(status().isOk());
        em.flush();
        em.clear();
        assertTrue(generoRepository.findById(genero.getId_genero()).isEmpty());
    }

    @Test
    void idNoCorpoNaoSobrescreveNemReativaOutroGenero() throws Exception {
        // Gênero desativado (RN25). O POST antigo com o id dele trocava o nome e ainda o reativava,
        // porque o cadastro sempre marca ativo = true.
        genero.setAtivo(false);
        generoRepository.save(genero);
        String token = tokenDe(TipoUsuario.ADMINISTRADOR);
        String nomeNovo = "Genero Novo " + System.nanoTime();

        mvc.perform(post("/api/v1/generos/").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id_genero\": " + genero.getId_genero() + ", \"name\": \"" + nomeNovo + "\"}"))
                .andExpect(status().isOk());
        em.flush();
        em.clear();

        // O gênero existente continua igual e desativado; o "id_genero" foi ignorado e um gênero novo foi criado
        Genero existente = generoRepository.findById(genero.getId_genero()).orElseThrow();
        assertEquals(nomeOriginal, existente.getName());
        assertFalse(existente.getAtivo());
        List<Genero> criados = generosComNome(nomeNovo);
        assertEquals(1, criados.size());
        assertNotEquals(genero.getId_genero(), criados.get(0).getId_genero());
        assertTrue(criados.get(0).getAtivo());
    }
}
