package br.edu.christus.backend.controller;

import br.edu.christus.backend.dto.LivroRequestDTO;
import br.edu.christus.backend.dto.LivroResponseDTO;
import br.edu.christus.backend.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/livros")
public class LivroController {

    @Autowired
    private LivroService service;

    @PostMapping
    public ResponseEntity<LivroResponseDTO> create(@Valid @RequestBody LivroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public List<LivroResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public LivroResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public LivroResponseDTO update(@PathVariable Long id, @Valid @RequestBody LivroRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
