package br.edu.christus.backend.controller;

import br.edu.christus.backend.dto.AnotacaoRequestDTO;
import br.edu.christus.backend.dto.AnotacaoResponseDTO;
import br.edu.christus.backend.dto.AnotacaoUpdateDTO;
import br.edu.christus.backend.service.AnotacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Rota fora de /livros/** de propósito: aquele prefixo é exclusivo de ADMINISTRADOR (RN02).
@RestController
@RequestMapping("/api/v1/anotacoes")
@RequiredArgsConstructor
public class AnotacaoController {

    private final AnotacaoService service;

    @PostMapping
    public ResponseEntity<AnotacaoResponseDTO> create(@Valid @RequestBody AnotacaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    // GET /api/v1/anotacoes ou GET /api/v1/anotacoes?livroId=1
    @GetMapping
    public List<AnotacaoResponseDTO> listar(@RequestParam(required = false) Long livroId) {
        return service.listar(livroId);
    }

    @GetMapping("/{id}")
    public AnotacaoResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public AnotacaoResponseDTO update(@PathVariable Long id, @Valid @RequestBody AnotacaoUpdateDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
