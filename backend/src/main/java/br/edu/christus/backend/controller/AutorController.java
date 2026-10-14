package br.edu.christus.backend.controller;

import br.edu.christus.backend.domain.Autor;
import br.edu.christus.backend.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/autores")
public class AutorController {

    @Autowired
    private AutorService service;

    @PostMapping
    public Autor create(@RequestBody Autor autor) {
        return service.create(autor);
    }

    @GetMapping
    public List<Autor> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Autor findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Autor update(@PathVariable Long id, @RequestBody Autor autor) {
        return service.update(id, autor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
