package br.edu.christus.backend.repository;

import br.edu.christus.backend.domain.StatusUpload;
import br.edu.christus.backend.domain.Upload;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UploadRepository extends JpaRepository<Upload, Long> {

    // RF36: fila de análise do administrador, do envio mais antigo para o mais novo.
    // O EntityGraph traz gênero e professor na mesma consulta (sem uma consulta extra por upload).
    @EntityGraph(attributePaths = {"genero", "professor"})
    List<Upload> findByStatusOrderByIdAsc(StatusUpload status);

    // Trava a linha até o fim da transação (no PostgreSQL sai como SELECT ... FOR NO KEY UPDATE),
    // para que duas análises simultâneas do mesmo upload não gerem dois livros (RN10).
    // Qualquer escrita que dependa do status (aprovar, rejeitar e, na A12, corrigir, reenviar
    // ou excluir) deve carregar o upload por aqui.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Upload u WHERE u.id = :id")
    Optional<Upload> findByIdParaAnalise(@Param("id") Long id);

    // RN25: gênero com uploads é desativado em vez de excluído (ver GeneroService.delete).
    // @Query explícita pelo mesmo motivo do LivroRepository: o "_" de id_genero confunde o Spring Data.
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Upload u WHERE u.genero.id_genero = :id_genero")
    boolean existsByGenero_Id_genero(@Param("id_genero") Long id_genero);
}
