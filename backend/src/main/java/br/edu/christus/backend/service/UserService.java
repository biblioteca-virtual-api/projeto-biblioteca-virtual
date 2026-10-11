package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.Usuario;
import br.edu.christus.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UsuarioRepository repository;

    public Usuario create(Usuario entity){
        return repository.save(entity);
    }

    public List<Usuario> findUsers(){
        return repository.findAll();
    }

    public void delete(Long id){
        repository.deleteById(id);
    }
    




}
