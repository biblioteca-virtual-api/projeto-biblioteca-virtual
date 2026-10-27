package br.edu.christus.backend.controller;

import br.edu.christus.backend.dto.AutorRequestDTO;
import br.edu.christus.backend.dto.AutorResponseDTO;
import br.edu.christus.backend.service.AutorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Recebe e devolve DTOs, nunca a entidade Autor: assim o POST não sobrescreve um autor existente.
// Só o ADMINISTRADOR cadastra, edita e exclui (SecurityConfig); qualquer usuário logado lista e consulta.
@RestController
@RequestMapping("/api/v1/autores")
public class AutorController {

    @Autowired
    private AutorService service;

    @PostMapping
    public AutorResponseDTO create(@Valid @RequestBody AutorRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<AutorResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public AutorResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public AutorResponseDTO update(@PathVariable Long id, @Valid @RequestBody AutorRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
