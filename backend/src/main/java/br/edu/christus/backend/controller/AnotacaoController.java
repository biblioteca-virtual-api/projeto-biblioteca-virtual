package br.edu.christus.backend.controller;

import br.edu.christus.backend.config.doc.ErroAcessoNegado;
import br.edu.christus.backend.config.doc.ErroDadosInvalidos;
import br.edu.christus.backend.config.doc.ErroNaoEncontrado;
import br.edu.christus.backend.dto.AnotacaoRequestDTO;
import br.edu.christus.backend.dto.AnotacaoResponseDTO;
import br.edu.christus.backend.dto.AnotacaoUpdateDTO;
import br.edu.christus.backend.service.AnotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Rota fora de /livros/** de propósito: aquele prefixo é exclusivo de ADMINISTRADOR (RN02).
@Tag(name = "Anotações", description = "Anotações privadas de leitura do próprio usuário (RF18 a RF25, RN17 a RN19)")
@RestController
@RequestMapping("/api/v1/anotacoes")
@RequiredArgsConstructor
public class AnotacaoController {

    private final AnotacaoService service;

    @Operation(
            summary = "Criar anotação",
            description = "**Perfil:** qualquer usuário logado (RF18 a RF25). A anotação pertence ao dono do token (RN17, RN19). "
                    + "Página maior ou igual a 1; texto de até 400 caracteres (RN18)."
    )
    @ApiResponse(responseCode = "201", description = "Anotação criada com sucesso")
    @ErroDadosInvalidos
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @PostMapping
    public ResponseEntity<AnotacaoResponseDTO> create(@Valid @RequestBody AnotacaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    // GET /api/v1/anotacoes ou GET /api/v1/anotacoes?livroId=1
    @Operation(
            summary = "Listar minhas anotações",
            description = "**Perfil:** qualquer usuário logado. Devolve só as do usuário do token; `livroId` filtra por livro."
    )
    @ApiResponse(responseCode = "200", description = "Lista de anotações do usuário")
    @ErroAcessoNegado
    @GetMapping
    public List<AnotacaoResponseDTO> listar(
            @Parameter(description = "Filtra as anotações por ID de livro (opcional)", example = "10")
            @RequestParam(required = false) Long livroId) {
        return service.listar(livroId);
    }

    @Operation(
            summary = "Consultar anotação por ID",
            description = "**Perfil:** dono da anotação. Anotação de outro usuário retorna 403 (RN17, RN19)."
    )
    @ApiResponse(responseCode = "200", description = "Anotação encontrada")
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @GetMapping("/{id}")
    public AnotacaoResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(
            summary = "Editar anotação",
            description = "**Perfil:** dono da anotação. Muda página e texto; o livro não muda."
    )
    @ApiResponse(responseCode = "200", description = "Anotação atualizada com sucesso")
    @ErroDadosInvalidos
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @PutMapping("/{id}")
    public AnotacaoResponseDTO update(@PathVariable Long id, @Valid @RequestBody AnotacaoUpdateDTO dto) {
        return service.update(id, dto);
    }

    @Operation(
            summary = "Excluir anotação",
            description = "**Perfil:** dono da anotação."
    )
    @ApiResponse(responseCode = "204", description = "Anotação excluída com sucesso")
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
