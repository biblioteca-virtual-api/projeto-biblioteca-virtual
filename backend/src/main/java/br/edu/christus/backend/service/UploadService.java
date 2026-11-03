package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.Autor;
import br.edu.christus.backend.domain.StatusUpload;
import br.edu.christus.backend.domain.Upload;
import br.edu.christus.backend.dto.LivroRequestDTO;
import br.edu.christus.backend.dto.LivroResponseDTO;
import br.edu.christus.backend.dto.UploadAprovacaoDTO;
import br.edu.christus.backend.dto.UploadRejeicaoDTO;
import br.edu.christus.backend.dto.UploadResponseDTO;
import br.edu.christus.backend.exception.BadRequestException;
import br.edu.christus.backend.exception.ConflictException;
import br.edu.christus.backend.exception.ResourceNotFoundException;
import br.edu.christus.backend.repository.AutorRepository;
import br.edu.christus.backend.repository.LivroRepository;
import br.edu.christus.backend.repository.UploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.christus.backend.domain.Genero;
import br.edu.christus.backend.domain.Usuario;
import br.edu.christus.backend.repository.GeneroRepository;
import br.edu.christus.backend.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

// Análise dos uploads pelo administrador (RF36 a RF44).
@Service
@RequiredArgsConstructor
public class UploadService {

    private final UploadRepository repository;
    private final LivroService livroService;
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;
    private final GeneroRepository generoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UploadArquivoService uploadArquivoService;

    // RF36: uploads aguardando análise.
    @Transactional(readOnly = true)
    public List<UploadResponseDTO> listarPendentes() {
        return repository.findByStatusOrderByIdAsc(StatusUpload.PENDENTE).stream()
                .map(this::toResponse).toList();
    }

    // RF37 e RF40 a RF44: aprova o upload e cria o Livro com os dados dele.
    // Tudo numa transação só: se a criação do livro falhar, o upload continua PENDENTE
    // e o autor novo (se houver) não fica cadastrado.
    @Transactional
    public UploadResponseDTO aprovar(Long id, UploadAprovacaoDTO dto) {
        // RF44: o administrador escolhe, de forma explícita, um Autor existente OU um novo.
        boolean novoAutor = Boolean.TRUE.equals(dto.novoAutor());
        if ((dto.autorId() != null) == novoAutor) {
            throw new BadRequestException("Escolha o autor do livro: informe autorId (autor já cadastrado) "
                    + "ou novoAutor = true (cadastrar o autor informado no upload).");
        }

        Upload upload = buscarPendente(id);

        // Autor novo: cadastrado com o nome que o professor informou no upload (RF43).
        Long autorId = novoAutor ? cadastrarAutor(upload.getAutor()) : dto.autorId();

        // RF40: os dados do upload são copiados para o Livro. O LivroService aplica as mesmas regras
        // do cadastro manual: ISBN único (409), autor e gênero existentes (404) e gênero ativo (RN12).
        LivroResponseDTO livro = livroService.create(new LivroRequestDTO(
                upload.getTitulo(), dto.isbn(), upload.getAnoPublicacao(), null,
                autorId, upload.getGenero().getId_genero(), upload.getEdicao(), upload.getPdf()));

        // RF41: o upload guarda a referência do Livro e continua existindo como histórico (RN09).
        upload.setLivro(livroRepository.getReferenceById(livro.id()));
        upload.setStatus(StatusUpload.APROVADO);
        upload.setMotivoRejeicao(null);
        return toResponse(upload);
    }

    // RF38/RF39: rejeita o upload. O motivo é obrigatório (RN06), validado no DTO.
    @Transactional
    public UploadResponseDTO rejeitar(Long id, UploadRejeicaoDTO dto) {
        Upload upload = buscarPendente(id);
        upload.setStatus(StatusUpload.REJEITADO);
        upload.setMotivoRejeicao(dto.motivo()); // já chega sem espaços nas pontas (ver UploadRejeicaoDTO)
        return toResponse(upload);
    }

    // 404 se não existir; 409 se já foi analisado. A trava do findByIdParaAnalise impede
    // que duas aprovações simultâneas do mesmo upload criem dois livros (RN10).
    private Upload buscarPendente(Long id) {
        Upload upload = repository.findByIdParaAnalise(id)
                .orElseThrow(() -> new ResourceNotFoundException("Upload não encontrado. Id: " + id));
        if (upload.getStatus() != StatusUpload.PENDENTE) {
            throw new ConflictException("Só é possível analisar uploads PENDENTES. Este upload está "
                    + upload.getStatus() + ".");
        }
        return upload;
    }

    private Long cadastrarAutor(String nome) {
        Autor autor = new Autor();
        autor.setNome(nome);
        return autorRepository.save(autor).getId();
    }

    // Conversão manual, como no LivroService: o Genero usa id_genero/name.
    private UploadResponseDTO toResponse(Upload u) {
        return new UploadResponseDTO(
                u.getId(), u.getTitulo(), u.getAutor(),
                u.getGenero().getId_genero(), u.getGenero().getName(),
                u.getEdicao(), u.getAnoPublicacao(), u.getPdf(),
                u.getProfessor().getId(), u.getProfessor().getNome(),
                u.getStatus(), u.getMotivoRejeicao(),
                (u.getLivro() != null) ? u.getLivro().getId() : null);
    }
    @Transactional
    public UploadResponseDTO criar(
            String titulo,
            String autor,
            Long generoId,
            String edicao,
            Integer anoPublicacao,
            MultipartFile arquivo) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Usuario professor = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário autenticado não encontrado."));

        Genero genero = generoRepository.findById(generoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gênero não encontrado. Id: " + generoId));

        // Salva o PDF no servidor e recebe o caminho do arquivo.
        String caminhoPdf = uploadArquivoService.salvar(arquivo);

        // Cria o registro do upload.
        Upload upload = new Upload();
        upload.setTitulo(titulo);
        upload.setAutor(autor);
        upload.setGenero(genero);
        upload.setEdicao(edicao);
        upload.setAnoPublicacao(anoPublicacao);
        upload.setPdf(caminhoPdf);
        upload.setProfessor(professor);
        upload.setStatus(StatusUpload.PENDENTE);

        return toResponse(repository.save(upload));
    }

    //busca o professor e utiliza o ID para consultar os registros dele
    @Transactional(readOnly = true)
    public List<UploadResponseDTO> listarMeusUploads() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Usuario professor = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário autenticado não encontrado."));

        return repository.findByProfessor_IdOrderByIdDesc(professor.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UploadResponseDTO editar(
            Long id,
            String titulo,
            String autor,
            Long generoId,
            String edicao,
            Integer anoPublicacao,
            MultipartFile arquivo) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Usuario professor = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário autenticado não encontrado."));

        Upload upload = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Upload não encontrado. Id: " + id));

        if (!upload.getProfessor().getId().equals(professor.getId())) {
            throw new AccessDeniedException(
                    "Você não tem permissão para editar este upload.");
        }

        if (upload.getStatus() != StatusUpload.REJEITADO) {
            throw new ConflictException(
                    "Somente uploads rejeitados podem ser editados.");
        }

        Genero genero = generoRepository.findById(generoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gênero não encontrado. Id: " + generoId));

        upload.setTitulo(titulo);
        upload.setAutor(autor);
        upload.setGenero(genero);
        upload.setEdicao(edicao);
        upload.setAnoPublicacao(anoPublicacao);

        if (arquivo != null && !arquivo.isEmpty()) {
            String novoCaminhoPdf = uploadArquivoService.salvar(arquivo);
            upload.setPdf(novoCaminhoPdf);
        }

        return toResponse(repository.save(upload));
    }

    @Transactional
    public UploadResponseDTO reenviar(Long id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Usuario professor = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário autenticado não encontrado."));

        Upload upload = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Upload não encontrado. Id: " + id));

        if (!upload.getProfessor().getId().equals(professor.getId())) {
            throw new AccessDeniedException(
                    "Você não tem permissão para reenviar este upload.");
        }

        if (upload.getStatus() != StatusUpload.REJEITADO) {
            throw new ConflictException(
                    "Somente uploads rejeitados podem ser reenviados.");
        }

        upload.setStatus(StatusUpload.PENDENTE);
        upload.setMotivoRejeicao(null);

        return toResponse(repository.save(upload));
    }
}
