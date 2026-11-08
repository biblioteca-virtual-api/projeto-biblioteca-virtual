package br.edu.christus.backend.controller;

import br.edu.christus.backend.config.doc.ErroAcessoNegado;
import br.edu.christus.backend.config.doc.ErroDadosInvalidos;
import br.edu.christus.backend.config.doc.ErroNaoEncontrado;
import br.edu.christus.backend.dto.GeneroRequestDTO;
import br.edu.christus.backend.dto.GeneroResponseDTO;
import br.edu.christus.backend.dto.GeneroUpdateDTO;
import br.edu.christus.backend.service.GeneroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Recebe e devolve DTOs, nunca a entidade Genero: assim o POST não sobrescreve um gênero existente.
// Só o ADMINISTRADOR cadastra, edita e exclui (SecurityConfig); qualquer usuário logado lista.
@Tag(name = "Gêneros", description = "Consulta e gestão de gêneros literários (RF27 a RF29, RN13, RN25)")
@RestController
@RequestMapping("/api/v1/generos/")
public class GeneroController {

    @Autowired
    private GeneroService service;

    @Operation(
            summary = "Cadastrar gênero",
            description = "**Perfil:** ADMINISTRADOR (RF27 a RF29). Todo gênero nasce ativo."
    )
    @ApiResponse(responseCode = "200", description = "Gênero cadastrado com sucesso")
    @ErroDadosInvalidos
    @ErroAcessoNegado
    @PostMapping
    public GeneroResponseDTO create(@Valid @RequestBody GeneroRequestDTO dto) {
        return service.create(dto);
    }

    // A rota não tem {id}: o id do gênero vem no corpo (GeneroUpdateDTO)
    @Operation(
            summary = "Editar gênero",
            description = "**Perfil:** ADMINISTRADOR (RF27 a RF29). O `id_genero` vai no corpo (a rota não tem `{id}`). "
                    + "Só o nome muda; `ativo` continua como está."
    )
    @ApiResponse(responseCode = "200", description = "Gênero atualizado com sucesso")
    @ErroDadosInvalidos
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @PutMapping
    public GeneroResponseDTO update(@Valid @RequestBody GeneroUpdateDTO dto) {
        return service.update(dto);
    }

    @Operation(
            summary = "Listar gêneros",
            description = "**Perfil:** qualquer usuário logado (RF27 a RF29). Inclui os inativos; o campo `ativo` indica o estado."
    )
    @ApiResponse(responseCode = "200", description = "Lista de gêneros")
    @ErroAcessoNegado
    @GetMapping
    public List<GeneroResponseDTO> findAll() {
        return service.listar();
    }

    @Operation(
            summary = "Excluir ou desativar gênero",
            description = "**Perfil:** ADMINISTRADOR (RF27 a RF29, RN25). Sem livros nem uploads, o gênero é excluído. "
                    + "Com livros ou uploads, é apenas desativado (`ativo = false`): continua ligado aos livros antigos, "
                    + "mas não aceita livros novos (RN12, RN13). Responde 200 sem corpo nos dois casos."
    )
    @ApiResponse(responseCode = "200", description = "Gênero excluído ou desativado com sucesso")
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
