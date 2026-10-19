package br.edu.christus.backend.controller;
import br.edu.christus.backend.domain.Genero;
import br.edu.christus.backend.service.FavoritoService;
import br.edu.christus.backend.service.GeneroService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.edu.christus.backend.dto.FavoritoResponseDTO;
import br.edu.christus.backend.service.FavoritoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;

    @PostMapping("/{livroId}")
    public ResponseEntity<FavoritoResponseDTO> adicionar(
            @PathVariable Long livroId) {


        FavoritoResponseDTO resposta = favoritoService.adicionar(livroId);


        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping // Recebe GET /api/v1/favoritos.
    public List<FavoritoResponseDTO> listarMeusFavoritos() {


        return favoritoService.listarMeusFavoritos();
    }

    @DeleteMapping("/{livroId}") // Recebe DELETE /api/v1/favoritos/{livroId}.
    public ResponseEntity<Void> remover(@PathVariable Long livroId) {

        favoritoService.remover(livroId);

        // Retorna HTTP 204
        return ResponseEntity.noContent().build();
    }
}
