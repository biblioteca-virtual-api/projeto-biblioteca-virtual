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
@RequestMapping("/api/v1/favoritos/")
@RequiredArgsConstructor
public class FavoritoController {
    private FavoritoService favoritoService;

    @PostMapping("/{livroId}").
    public ResponseEntity<FavoritoResponseDTO> adicionar(@PathVariable Long livroId) {
        // Recebe o ID do livro pela URL e solicita ao Service que o favorite.
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoritoService.adicionar(livroId));
        // Retorna HTTP 201 (Created) e os dados do favorito criado.
    }

    @GetMapping
    public List<FavoritoResponseDTO> listarMeusFavoritos() {
        return favoritoService.listarMeusFavoritos();
    }

    @DeleteMapping("/{livroId}")
    public ResponseEntity<Void> remover(@PathVariable Long livroId) {
        favoritoService.remover(livroId);

        return ResponseEntity.noContent().build();
        // Retorna HTTP 204
    }
}

}
