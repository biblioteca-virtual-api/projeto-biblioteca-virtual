package br.edu.christus.backend.controller;

import br.edu.christus.backend.config.doc.ErroAcessoNegado;
import br.edu.christus.backend.config.doc.ErroConflito;
import br.edu.christus.backend.config.doc.ErroDadosInvalidos;
import br.edu.christus.backend.dto.UsuarioRequestDTO;
import br.edu.christus.backend.dto.UsuarioResponseDTO;
import br.edu.christus.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Recebe e devolve DTOs, nunca a entidade Usuario: assim o cliente não envia id nem recebe a senha.
@Tag(name = "Usuários", description = "Cadastro público de alunos e gestão de contas pelo administrador (RF03, RN01)")
@RestController
@RequestMapping("/api/v1/users")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // Rota pública (SecurityConfig): sem token de ADMINISTRADOR, só cria ALUNO (RF03, regra no UsuarioService)
    @Operation(
            summary = "Cadastrar usuário",
            description = "Cadastro público: sem token de ADMINISTRADOR, cria sempre um ALUNO. "
                    + "Só um ADMINISTRADOR logado pode cadastrar PROFESSOR ou ADMINISTRADOR (RN01, RF03). "
                    + "Senha de 8 a 72 caracteres, guardada com BCrypt; a resposta nunca devolve a senha. "
                    + "E-mail repetido retorna 409.",
            security = {}
    )
    @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso")
    @ErroDadosInvalidos
    @ErroAcessoNegado
    @ErroConflito
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    // Só ADMINISTRADOR (SecurityConfig): a listagem mostra o e-mail de todos os usuários
    @Operation(
            summary = "Listar usuários",
            description = "**Perfil:** ADMINISTRADOR (RF03). Devolve id, nome, e-mail e perfil de todos os usuários, sem a senha."
    )
    @ApiResponse(responseCode = "200", description = "Lista de usuários")
    @ErroAcessoNegado
    @GetMapping
    public List<UsuarioResponseDTO> findAll() {
        return service.findUsers();
    }
}
