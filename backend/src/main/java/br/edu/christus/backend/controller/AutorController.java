package br.edu.christus.backend.controller;

import br.edu.christus.backend.config.doc.ErroAcessoNegado;
import br.edu.christus.backend.config.doc.ErroConflito;
import br.edu.christus.backend.config.doc.ErroDadosInvalidos;
import br.edu.christus.backend.config.doc.ErroNaoEncontrado;
import br.edu.christus.backend.dto.AutorRequestDTO;
import br.edu.christus.backend.dto.AutorResponseDTO;
import br.edu.christus.backend.service.AutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

// Recebe e devolve DTOs, nunca a entidade Autor: assim o POST não sobrescreve um autor existente.
// Só o ADMINISTRADOR cadastra, edita e exclui (SecurityConfig); qualquer usuário logado lista e consulta.
@Tag(name = "Autores", description = "Consulta e gestão de autores (RF26, RN24)")
@RestController
@RequestMapping("/api/v1/autores")
public class AutorController {

    @Autowired
    private AutorService service;

    @Operation(
            summary = "Cadastrar autor",
            description = "**Perfil:** ADMINISTRADOR (RF26, RN02). Sempre cria um autor novo (o corpo não aceita `id`)."
    )
    @ApiResponse(responseCode = "200", description = "Autor cadastrado com sucesso")
    @ErroDadosInvalidos
    @ErroAcessoNegado
    @PostMapping
    public AutorResponseDTO create(@Valid @RequestBody AutorRequestDTO dto) {
        return service.create(dto);
    }

    @Operation(
            summary = "Listar autores",
            description = "**Perfil:** qualquer usuário logado."
    )
    @ApiResponse(responseCode = "200", description = "Lista de autores")
    @ErroAcessoNegado
    @GetMapping
    public List<AutorResponseDTO> findAll() {
        return service.findAll();
    }

    @Operation(
            summary = "Consultar autor por ID",
            description = "**Perfil:** qualquer usuário logado."
    )
    @ApiResponse(responseCode = "200", description = "Autor encontrado")
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @GetMapping("/{id}")
    public AutorResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(
            summary = "Editar autor",
            description = "**Perfil:** ADMINISTRADOR (RF26). Substitui nome e biografia: sem biografia, a biografia salva é apagada."
    )
    @ApiResponse(responseCode = "200", description = "Autor atualizado com sucesso")
    @ErroDadosInvalidos
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @PutMapping("/{id}")
    public AutorResponseDTO update(@PathVariable Long id, @Valid @RequestBody AutorRequestDTO dto) {
        return service.update(id, dto);
    }

    @Operation(
            summary = "Excluir autor",
            description = "**Perfil:** ADMINISTRADOR (RF26). Só é permitido se o autor não tiver livros (RN24); caso contrário retorna 409."
    )
    @ApiResponse(responseCode = "204", description = "Autor excluído com sucesso")
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @ErroConflito
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
