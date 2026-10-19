package br.edu.christus.backend.repository;

import br.edu.christus.backend.domain.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    // Usado para impedir ISBN duplicado no cadastro
    boolean existsByIsbn(String isbn);

    // Usado na edição: existe OUTRO livro (id diferente) com esse ISBN?
    boolean existsByIsbnAndIdNot(String isbn, Long id);

    // consulta o banco para verificar se existe pelo menos um livro vinculado a um
    // gênero (regra de exclusão)
    // @Query explícita: o Spring Data lê "_" no nome do método como separador de propriedade
    // e procuraria "genero.id.genero", que não existe (o campo do Genero é id_genero).
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Livro l WHERE l.genero.id_genero = :id_genero")
    boolean existsByGenero_Id_genero(@Param("id_genero") Long id_genero);
}
