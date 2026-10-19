package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.Favorito;
import br.edu.christus.backend.domain.FavoritoId;
import br.edu.christus.backend.domain.Livro;
import br.edu.christus.backend.domain.Usuario;
import br.edu.christus.backend.dto.FavoritoResponseDTO;
import br.edu.christus.backend.repository.FavoritoRepository;
import br.edu.christus.backend.repository.LivroRepository;
import br.edu.christus.backend.repository.UsuarioRepository;
import br.edu.christus.backend.exception.ConflictException;
import br.edu.christus.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;

    private Usuario buscarUsuarioAutenticado() {
        Authentication autenticacao =
                SecurityContextHolder.getContext().getAuthentication();

        String email = autenticacao.getName();

        // Busca o usuário pelo e-mail ou lança uma exceção se não existir.
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário autenticado não encontrado."));
    }
    private FavoritoResponseDTO converterParaDTO(Favorito favorito) {
        return new FavoritoResponseDTO(
                favorito.getLivro().getId(),
                favorito.getLivro().getTitulo(),
                favorito.getLivro().getIsbn()
        );
    }

    public FavoritoResponseDTO adicionar(Long livroId) {
        Usuario usuario = buscarUsuarioAutenticado();

        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Livro não encontrado. Id: " + livroId));

        if (favoritoRepository.existsByUsuario_IdAndLivro_Id(
                usuario.getId(), livro.getId())) {
            throw new ConflictException("Este livro já está nos seus favoritos.");
        }

        //chave primária composta
        FavoritoId id = new FavoritoId();
        id.setUsuarioId(usuario.getId());
        id.setLivroId(livro.getId());

        // Cria a entidade que representa a associação entre usuário e livro.
        Favorito favorito = new Favorito();
        favorito.setId(id);
        favorito.setUsuario(usuario);
        favorito.setLivro(livro);

        Favorito favoritoSalvo = favoritoRepository.save(favorito);
        return converterParaDTO(favoritoSalvo);
    }

    public List<FavoritoResponseDTO> listarMeusFavoritos() {
        Usuario usuario = buscarUsuarioAutenticado();
        return favoritoRepository.findByUsuario_Id(usuario.getId())
                .stream() // Permite processar cada favorito da lista.
                .map(this::converterParaDTO)
                .toList(); // Retorna uma lista de DTOs para o Controller.
    }
    public void remover(Long livroId) {
        Usuario usuario = buscarUsuarioAutenticado();
        Favorito favorito = favoritoRepository
                .findByUsuario_IdAndLivro_Id(usuario.getId(), livroId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Este livro não está nos seus favoritos."));
        favoritoRepository.delete(favorito);
    }
}