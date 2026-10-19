package br.edu.christus.backend.repository;

import br.edu.christus.backend.domain.Anotacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnotacaoRepository extends JpaRepository<Anotacao, Long> {

    // Todas as anotações de um usuário (RN17/RN19: nunca lista as de outros)
    List<Anotacao> findByUsuario_IdOrderByIdDesc(Long usuarioId);

    // Anotações de um usuário em um livro específico
    List<Anotacao> findByUsuario_IdAndLivro_IdOrderByPaginaAscIdAsc(Long usuarioId, Long livroId);
}
