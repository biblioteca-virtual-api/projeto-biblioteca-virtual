package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.Autor;
import br.edu.christus.backend.domain.Genero;
import br.edu.christus.backend.domain.StatusUpload;
import br.edu.christus.backend.domain.TipoUsuario;
import br.edu.christus.backend.domain.Upload;
import br.edu.christus.backend.domain.Usuario;
import br.edu.christus.backend.dto.LivroResponseDTO;
import br.edu.christus.backend.dto.UploadAprovacaoDTO;
import br.edu.christus.backend.dto.UploadRejeicaoDTO;
import br.edu.christus.backend.dto.UploadResponseDTO;
import br.edu.christus.backend.exception.BadRequestException;
import br.edu.christus.backend.exception.BusinessException;
import br.edu.christus.backend.exception.ConflictException;
import br.edu.christus.backend.exception.ResourceNotFoundException;
import br.edu.christus.backend.repository.AutorRepository;
import br.edu.christus.backend.repository.GeneroRepository;
import br.edu.christus.backend.repository.UploadRepository;
import br.edu.christus.backend.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração do fluxo Upload -> Livro (A13, A14 e A15), usando o banco do .env.
 * @Transactional: as linhas criadas são desfeitas no fim de cada teste. Atenção: ao subir o contexto,
 * o Flyway aplica no banco do .env as migrations que ainda não rodaram (como no BackendApplicationTests).
 * Os uploads são salvos direto pelo repositório porque o envio pelo professor é da A12.
 */
@SpringBootTest
@Transactional
class UploadFluxoTest {

    @Autowired
    private UploadService uploadService;
    @Autowired
    private LivroService livroService;
    @Autowired
    private GeneroService generoService;
    @Autowired
    private UploadRepository uploadRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private GeneroRepository generoRepository;
    @Autowired
    private AutorRepository autorRepository;
    @Autowired
    private EntityManager em;

    private String sufixo;
    private Usuario professor;
    private Genero genero;

    @BeforeEach
    void preparar() {
        // E-mail e ISBN são UNIQUE no banco: o sufixo evita colisão com dados que já existam
        sufixo = String.valueOf(System.nanoTime());

        professor = new Usuario();
        professor.setNome("Professora Teste");
        professor.setEmail("professora." + sufixo + "@teste.local");
        professor.setSenha("senha-nao-usada");
        professor.setTipoUsuario(TipoUsuario.PROFESSOR);
        usuarioRepository.save(professor);

        genero = new Genero();
        genero.setName("Gênero Teste " + sufixo);
        generoRepository.save(genero);
    }

    // Upload PENDENTE (RN04: é o status padrão) enviado pela professora do teste
    private Upload novoUpload(String titulo, String autor) {
        Upload upload = new Upload();
        upload.setTitulo(titulo);
        upload.setAutor(autor);
        upload.setGenero(genero);
        upload.setEdicao("2ª edição");
        upload.setAnoPublicacao(1899);
        upload.setPdf("uploads/" + sufixo + "/livro.pdf");
        upload.setProfessor(professor);
        return uploadRepository.save(upload);
    }

    // ISBN único e com no máximo 20 caracteres
    private String isbn(String prefixo) {
        return prefixo + sufixo.substring(sufixo.length() - 12);
    }

    // Grava no banco o que está pendente e esquece o que estava em memória:
    // as leituras seguintes vêm do banco de verdade, não do cache do Hibernate.
    private void gravarELimparMemoria() {
        em.flush();
        em.clear();
    }

    private Upload relerDoBanco(Long id) {
        return uploadRepository.findById(id).orElseThrow();
    }

    private long autoresComNome(String nome) {
        return autorRepository.findAll().stream().filter(a -> a.getNome().equals(nome)).count();
    }

    @Test
    void aprovarCriaLivroComOsDadosDoUploadEAutorNovo() {
        Upload upload = novoUpload("Dom Casmurro", "Machado de Assis " + sufixo);

        UploadResponseDTO aprovado = uploadService.aprovar(upload.getId(),
                new UploadAprovacaoDTO(isbn("N"), null, true));
        gravarELimparMemoria();

        // A13 e RF41: no banco, o status mudou e o upload guarda o livro criado e a professora
        Upload salvo = relerDoBanco(upload.getId());
        assertEquals(StatusUpload.APROVADO, salvo.getStatus());
        assertNotNull(aprovado.livroId());
        assertEquals(aprovado.livroId(), salvo.getLivro().getId());
        assertEquals(professor.getId(), salvo.getProfessor().getId());

        // A14 (RF40 e RF43): dados copiados do upload e autor novo com o nome informado no upload
        LivroResponseDTO livro = livroService.findById(aprovado.livroId());
        assertEquals("Dom Casmurro", livro.titulo());
        assertEquals(isbn("N"), livro.isbn());
        assertEquals("2ª edição", livro.edicao());
        assertEquals(1899, livro.anoPublicacao());
        assertEquals(upload.getPdf(), livro.pdf());
        assertEquals(genero.getId_genero(), livro.generoId());
        assertEquals("Machado de Assis " + sufixo, livro.autorNome());
        assertEquals(1, autoresComNome("Machado de Assis " + sufixo));

        // A15: o livro aparece na listagem e a resposta mostra a professora que enviou
        assertTrue(livroService.findAll().stream().anyMatch(l -> l.id().equals(livro.id())));
        assertEquals(professor.getId(), aprovado.professorId());
        assertEquals("Professora Teste", aprovado.professorNome());
    }

    @Test
    void aprovarComAutorExistenteUsaOAutorEscolhido() {
        Autor autor = new Autor();
        autor.setNome("Machado de Assis " + sufixo);
        autorRepository.save(autor);
        // Texto diferente do nome cadastrado: prova que vale o autorId, e não uma busca pelo nome
        Upload upload = novoUpload("Com autor existente", "M. de Assis " + sufixo);

        UploadResponseDTO aprovado = uploadService.aprovar(upload.getId(),
                new UploadAprovacaoDTO(isbn("E"), autor.getId(), null));
        gravarELimparMemoria();

        // RF44: o livro usa o autor escolhido pelo administrador e nenhum autor novo foi cadastrado
        assertEquals(autor.getId(), livroService.findById(aprovado.livroId()).autorId());
        assertEquals(0, autoresComNome("M. de Assis " + sufixo));
    }

    @Test
    void aprovarExigeEscolhaExplicitaDoAutor() {
        Long id = novoUpload("Sem escolha de autor", "Fulano").getId();

        // RF44: nem autorId nem novoAutor, ou os dois ao mesmo tempo -> 400
        assertThrows(BadRequestException.class,
                () -> uploadService.aprovar(id, new UploadAprovacaoDTO(isbn("A"), null, null)));
        assertThrows(BadRequestException.class,
                () -> uploadService.aprovar(id, new UploadAprovacaoDTO(isbn("B"), 1L, true)));
        gravarELimparMemoria();
        assertEquals(StatusUpload.PENDENTE, relerDoBanco(id).getStatus());
    }

    @Test
    void aprovarComGeneroInativoMantemUploadPendente() {
        Long id = novoUpload("Gênero desativado depois do envio", "Fulano").getId();
        genero.setAtivo(false);
        generoRepository.save(genero);

        // RN12: livro novo exige gênero ativo
        assertThrows(BusinessException.class,
                () -> uploadService.aprovar(id, new UploadAprovacaoDTO(isbn("G"), null, true)));
        gravarELimparMemoria();
        Upload salvo = relerDoBanco(id);
        assertEquals(StatusUpload.PENDENTE, salvo.getStatus());
        assertNull(salvo.getLivro());
    }

    @Test
    void aprovarComIsbnJaUsadoMantemUploadPendente() {
        Autor autor = new Autor();
        autor.setNome("Autor " + sufixo);
        autorRepository.save(autor);
        uploadService.aprovar(novoUpload("Primeiro", "Autor " + sufixo).getId(),
                new UploadAprovacaoDTO(isbn("D"), autor.getId(), null));
        Long segundo = novoUpload("Segundo com o mesmo ISBN", "Autor " + sufixo).getId();

        // ISBN é único no acervo: o LivroService recusa com 409 e o upload não é aprovado
        assertThrows(ConflictException.class,
                () -> uploadService.aprovar(segundo, new UploadAprovacaoDTO(isbn("D"), autor.getId(), null)));
        gravarELimparMemoria();
        assertEquals(StatusUpload.PENDENTE, relerDoBanco(segundo).getStatus());
    }

    @Test
    void rejeitarGuardaOMotivoSemEspacosNasPontas() {
        Long id = novoUpload("Para rejeitar", "Fulano").getId();

        UploadResponseDTO rejeitado = uploadService.rejeitar(id,
                new UploadRejeicaoDTO("  PDF ilegível, envie novamente.  "));
        gravarELimparMemoria();

        // A13 (RF38 e RN06): no banco, status REJEITADO com o motivo e sem livro
        Upload salvo = relerDoBanco(id);
        assertEquals(StatusUpload.REJEITADO, salvo.getStatus());
        assertEquals("PDF ilegível, envie novamente.", salvo.getMotivoRejeicao());
        assertNull(salvo.getLivro());
        assertEquals(StatusUpload.REJEITADO, rejeitado.status());
        assertNull(rejeitado.livroId());
    }

    @Test
    void filaTrazSoPendentesDoEnvioMaisAntigoParaOMaisNovo() {
        Long primeiro = novoUpload("Primeiro pendente", "Fulano").getId();
        Long segundo = novoUpload("Segundo pendente", "Fulano").getId();
        Long rejeitado = novoUpload("Rejeitado", "Fulano").getId();
        uploadService.rejeitar(rejeitado, new UploadRejeicaoDTO("motivo"));
        gravarELimparMemoria();

        // RF36: o banco do .env pode ter outros pendentes, então olhamos só os deste teste
        List<Long> fila = uploadService.listarPendentes().stream()
                .map(UploadResponseDTO::id)
                .filter(idDaFila -> List.of(primeiro, segundo, rejeitado).contains(idDaFila))
                .toList();
        assertEquals(List.of(primeiro, segundo), fila);
    }

    @Test
    void uploadJaAnalisadoNaoPodeSerAnalisadoDeNovo() {
        Long aprovado = novoUpload("Já aprovado", "Fulano " + sufixo).getId();
        uploadService.aprovar(aprovado, new UploadAprovacaoDTO(isbn("U"), null, true));
        Long rejeitado = novoUpload("Já rejeitado", "Fulano").getId();
        uploadService.rejeitar(rejeitado, new UploadRejeicaoDTO("motivo"));

        // RN10: aprovar de novo criaria um segundo livro. RN09: o aprovado fica como histórico.
        // Um rejeitado só volta para análise pelo reenvio do professor (RN07, A12).
        for (Long id : List.of(aprovado, rejeitado)) {
            assertThrows(ConflictException.class,
                    () -> uploadService.aprovar(id, new UploadAprovacaoDTO(isbn("V"), null, true)));
            assertThrows(ConflictException.class,
                    () -> uploadService.rejeitar(id, new UploadRejeicaoDTO("outro motivo")));
        }
    }

    @Test
    void uploadInexistenteRetornaNaoEncontrado() {
        assertThrows(ResourceNotFoundException.class,
                () -> uploadService.aprovar(-1L, new UploadAprovacaoDTO(isbn("X"), null, true)));
        assertThrows(ResourceNotFoundException.class,
                () -> uploadService.rejeitar(-1L, new UploadRejeicaoDTO("motivo")));
    }

    @Test
    void generoComUploadEDesativadoEmVezDeExcluido() {
        novoUpload("Gênero só com upload", "Fulano");

        // RN25: o gênero não tem livros, mas tem upload. Excluir violaria a FK de tb_upload
        generoService.delete(genero.getId_genero());
        gravarELimparMemoria();

        assertFalse(generoRepository.findById(genero.getId_genero()).orElseThrow().getAtivo());
    }
}
