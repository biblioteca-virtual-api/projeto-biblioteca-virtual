package br.edu.christus.backend.controller;

import br.edu.christus.backend.config.doc.ErroAcessoNegado;
import br.edu.christus.backend.config.doc.ErroConflito;
import br.edu.christus.backend.config.doc.ErroNaoEncontrado;
import br.edu.christus.backend.dto.FavoritoResponseDTO;
import br.edu.christus.backend.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// RF14 a RF17: lista de livros favoritos do próprio usuário logado.
@Tag(name = "Favoritos", description = "Lista de livros favoritos do próprio usuário (RF14 a RF17, RN20)")
@RestController
@RequestMapping("/api/v1/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;

    @Operation(
            summary = "Favoritar livro",
            description = "**Perfil:** qualquer usuário logado (RF14 a RF17). Livro já favoritado retorna 409 (RN20)."
    )
    @ApiResponse(responseCode = "201", description = "Livro adicionado aos favoritos")
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @ErroConflito
    @PostMapping("/{livroId}")
    public ResponseEntity<FavoritoResponseDTO> adicionar(@PathVariable Long livroId) {
        FavoritoResponseDTO resposta = favoritoService.adicionar(livroId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @Operation(
            summary = "Listar meus favoritos",
            description = "**Perfil:** qualquer usuário logado (RF14 a RF17). Só os do usuário do token."
    )
    @ApiResponse(responseCode = "200", description = "Lista de favoritos do usuário")
    @ErroAcessoNegado
    @GetMapping
    public List<FavoritoResponseDTO> listarMeusFavoritos() {
        return favoritoService.listarMeusFavoritos();
    }

    @Operation(
            summary = "Remover favorito",
            description = "**Perfil:** qualquer usuário logado (RF14 a RF17). Retorna 404 se o livro não estiver nos favoritos."
    )
    @ApiResponse(responseCode = "204", description = "Favorito removido com sucesso")
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @DeleteMapping("/{livroId}")
    public ResponseEntity<Void> remover(@PathVariable Long livroId) {
        favoritoService.remover(livroId);
        return ResponseEntity.noContent().build();
    }
}
