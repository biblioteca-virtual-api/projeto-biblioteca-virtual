package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.Anotacao;
import br.edu.christus.backend.domain.Livro;
import br.edu.christus.backend.domain.Usuario;
import br.edu.christus.backend.dto.AnotacaoRequestDTO;
import br.edu.christus.backend.dto.AnotacaoResponseDTO;
import br.edu.christus.backend.dto.AnotacaoUpdateDTO;
import br.edu.christus.backend.exception.ResourceNotFoundException;
import br.edu.christus.backend.repository.AnotacaoRepository;
import br.edu.christus.backend.repository.LivroRepository;
import br.edu.christus.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnotacaoService {

    private final AnotacaoRepository repository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public AnotacaoResponseDTO create(AnotacaoRequestDTO dto) {
        Usuario usuario = usuarioAutenticado();
        Livro livro = livroRepository.findById(dto.livroId())
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado. Id: " + dto.livroId()));

        Anotacao a = new Anotacao();
        a.setUsuario(usuario); // dono = usuário do token, nunca vem do corpo
        a.setLivro(livro);
        a.setPagina(dto.pagina());
        a.setTexto(dto.texto());
        return toResponse(repository.save(a));
    }

    // Lista só as anotações do próprio usuário; livroId é um filtro opcional.
    @Transactional(readOnly = true)
    public List<AnotacaoResponseDTO> listar(Long livroId) {
        Long usuarioId = usuarioAutenticado().getId();
        List<Anotacao> lista = (livroId == null)
                ? repository.findByUsuario_IdOrderByIdDesc(usuarioId)
                : repository.findByUsuario_IdAndLivro_IdOrderByPaginaAscIdAsc(usuarioId, livroId);
        return lista.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AnotacaoResponseDTO findById(Long id) {
        return toResponse(buscarDoDono(id));
    }

    @Transactional
    public AnotacaoResponseDTO update(Long id, AnotacaoUpdateDTO dto) {
        Anotacao a = buscarDoDono(id);
        a.setPagina(dto.pagina());
        a.setTexto(dto.texto());
        return toResponse(repository.save(a));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(buscarDoDono(id));
    }

    // 404 se não existir; 403 se existir mas for de outro usuário (RN17/RN19).
    private Anotacao buscarDoDono(Long id) {
        Anotacao a = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anotação não encontrada. Id: " + id));
        if (!a.getUsuario().getId().equals(usuarioAutenticado().getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar esta anotação.");
        }
        return a;
    }

    // O JWT guarda o e-mail (ver AuthorizationService); com ele achamos o usuário logado.
    private Usuario usuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));
    }

    // Conversão ENTIDADE -> DTO de saída. A entidade (Anotacao) fica restrita ao banco/service;
    // o cliente só recebe o AnotacaoResponseDTO, com os campos que escolhemos expor.
    private AnotacaoResponseDTO toResponse(Anotacao a) {
        return new AnotacaoResponseDTO(a.getId(), a.getLivro().getId(), a.getLivro().getTitulo(),
                a.getPagina(), a.getTexto());
    }
}
