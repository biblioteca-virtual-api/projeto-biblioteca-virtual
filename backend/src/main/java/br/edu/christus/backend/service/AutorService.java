package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.Autor;
import br.edu.christus.backend.exception.ResourceNotFoundException;
import br.edu.christus.backend.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorService {

    @Autowired
    private AutorRepository repository;

    public Autor create(Autor autor) {
        return repository.save(autor);
    }

    public List<Autor> findAll() {
        return repository.findAll();
    }

    public Autor findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado. Id: " + id));
    }

    public Autor update(Long id, Autor autor) {
        Autor existingAutor = findById(id);
        existingAutor.setNome(autor.getNome());
        existingAutor.setBiografia(autor.getBiografia());
        return repository.save(existingAutor);
    }

    public void delete(Long id) {
        Autor autor = findById(id);
        repository.delete(autor);
    }
}
