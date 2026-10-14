package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.Usuario;
import br.edu.christus.backend.exception.ResourceNotFoundException;
import br.edu.christus.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario create(Usuario entity) {
        // Criptografa a senha antes de salvar no banco
        entity.setSenha(passwordEncoder.encode(entity.getSenha()));
        return repository.save(entity);
    }

    public List<Usuario> findUsers() {
        return repository.findAll();
    }

    public Usuario findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado. ID: " + id));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado para exclusão. ID: " + id);
        }
        repository.deleteById(id);
    }
}