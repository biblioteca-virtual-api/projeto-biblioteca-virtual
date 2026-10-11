package br.edu.christus.backend.repository;

import br.edu.christus.backend.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

}
