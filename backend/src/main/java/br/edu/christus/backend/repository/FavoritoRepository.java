package br.edu.christus.backend.repository;

import br.edu.christus.backend.domain.Favorito;
import br.edu.christus.backend.domain.FavoritoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritoRepository extends JpaRepository<Favorito, FavoritoId> {

    // Verifica se já existe um favorito usuário-livro
    boolean existsByUsuario_IdAndLivro_Id(Long usuarioId, Long livroId);

    java.util.List<Favorito> findByUsuario_Id(Long usuarioId);

    //buscar um favorito especifico que sera removido
    java.util.Optional<Favorito> findByUsuario_IdAndLivro_Id(Long usuarioId, Long livroId);
}
