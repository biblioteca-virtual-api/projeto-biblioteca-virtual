package br.edu.christus.backend.controller;

import br.edu.christus.backend.config.doc.ErroAcessoNegado;
import br.edu.christus.backend.config.doc.ErroConflito;
import br.edu.christus.backend.config.doc.ErroDadosInvalidos;
import br.edu.christus.backend.config.doc.ErroNaoEncontrado;
import br.edu.christus.backend.config.doc.ErroRegraDeNegocio;
import br.edu.christus.backend.dto.LivroRequestDTO;
import br.edu.christus.backend.dto.LivroResponseDTO;
import br.edu.christus.backend.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Livros", description = "Gestão do acervo de livros pelo administrador (RF04 a RF08, RN02, RN12, RN14, RN22)")
@RestController
@RequestMapping("/api/v1/livros")
public class LivroController {

    @Autowired
    private LivroService service;

    @Operation(
            summary = "Cadastrar livro",
            description = "**Perfil:** ADMINISTRADOR (RF04, RN02). Informe `autorId` e `generoId` existentes (RN14). "
                    + "O gênero precisa estar ativo (RN12). ISBN único. Edição e PDF são opcionais."
    )
    @ApiResponse(responseCode = "201", description = "Livro cadastrado com sucesso")
    @ErroDadosInvalidos
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @ErroConflito
    @ErroRegraDeNegocio
    @PostMapping
    public ResponseEntity<LivroResponseDTO> create(@Valid @RequestBody LivroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @Operation(
            summary = "Listar livros",
            description = "**Perfil:** ADMINISTRADOR (RF05, RN02). Lista todos os livros."
    )
    @ApiResponse(responseCode = "200", description = "Lista de livros")
    @ErroAcessoNegado
    @GetMapping
    public List<LivroResponseDTO> findAll() {
        return service.findAll();
    }

    @Operation(
            summary = "Consultar livro por ID",
            description = "**Perfil:** ADMINISTRADOR (RF06, RN02)."
    )
    @ApiResponse(responseCode = "200", description = "Livro encontrado")
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @GetMapping("/{id}")
    public LivroResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(
            summary = "Editar livro",
            description = "**Perfil:** ADMINISTRADOR (RF07). Substitui todos os campos: reenvie edição, PDF, sinopse e ano "
                    + "para não apagá-los. ISBN não pode ser de outro livro; gênero precisa estar ativo (RN12)."
    )
    @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso")
    @ErroDadosInvalidos
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @ErroConflito
    @ErroRegraDeNegocio
    @PutMapping("/{id}")
    public LivroResponseDTO update(@PathVariable Long id, @Valid @RequestBody LivroRequestDTO dto) {
        return service.update(id, dto);
    }

    @Operation(
            summary = "Excluir livro",
            description = "**Perfil:** ADMINISTRADOR (RF08, RN22). Anotações e favoritos do livro são removidos junto; "
                    + "uploads aprovados que o geraram continuam como histórico, sem o vínculo (RN09)."
    )
    @ApiResponse(responseCode = "204", description = "Livro excluído com sucesso")
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
