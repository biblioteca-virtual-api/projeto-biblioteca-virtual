package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.Autor;
import br.edu.christus.backend.dto.AutorRequestDTO;
import br.edu.christus.backend.dto.AutorResponseDTO;
import br.edu.christus.backend.exception.ResourceNotFoundException;
import br.edu.christus.backend.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorService {

    @Autowired
    private AutorRepository repository;

    // O autor é montado do zero a partir do DTO (que não tem id),
    // então o save() sempre faz INSERT e nunca sobrescreve um autor existente.
    public AutorResponseDTO create(AutorRequestDTO dto) {
        Autor autor = new Autor();
        autor.setNome(dto.nome());
        autor.setBiografia(dto.biografia());
        return toResponse(repository.save(autor));
    }

    public List<AutorResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public AutorResponseDTO findById(Long id) {
        return toResponse(buscar(id));
    }

    public AutorResponseDTO update(Long id, AutorRequestDTO dto) {
        Autor existingAutor = buscar(id);
        existingAutor.setNome(dto.nome());
        existingAutor.setBiografia(dto.biografia());
        return toResponse(repository.save(existingAutor));
    }

    public void delete(Long id) {
        Autor autor = buscar(id);
        repository.delete(autor);
    }

    private Autor buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado. Id: " + id));
    }

    // Conversão ENTIDADE -> DTO de saída
    private AutorResponseDTO toResponse(Autor a) {
        return new AutorResponseDTO(a.getId(), a.getNome(), a.getBiografia());
    }
}
