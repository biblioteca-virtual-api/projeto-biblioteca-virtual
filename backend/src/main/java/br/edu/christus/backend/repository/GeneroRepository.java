package br.edu.christus.backend.repository;

import br.edu.christus.backend.domain.Genero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneroRepository
        extends JpaRepository<Genero, Long> {

}

//isso cria um repositório para trabalhar com a entidade Genero, cujo identificador é do tipo Long
//O JpaRepository já fornece operações básicas de save, findall, delete..não precisa escrever