package br.edu.christus.backend.repository;

import br.edu.christus.backend.domain.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    // Usado para impedir ISBN duplicado no cadastro
    boolean existsByIsbn(String isbn);

    // Usado na edição: existe OUTRO livro (id diferente) com esse ISBN?
    boolean existsByIsbnAndIdNot(String isbn, Long id);

    //consulta o banco para verificar se existe pelo menos um livro vinculado a um gênero (regra de exclusão)
    boolean existsByGenero_Id_genero(Long id_genero);
}
