package br.edu.christus.backend.controller;

import br.edu.christus.backend.domain.Usuario;
import br.edu.christus.backend.dto.LoginRequestDTO;
import br.edu.christus.backend.dto.LoginResponseDTO;
import br.edu.christus.backend.exception.ResourceNotFoundException;
import br.edu.christus.backend.exception.StandardError;
import br.edu.christus.backend.repository.UsuarioRepository;
import br.edu.christus.backend.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Login e emissão de token JWT (RF01, RF02)")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Operation(
            summary = "Fazer login",
            description = "Autentica com e-mail e senha (RF01) e devolve o token JWT, válido por 2 horas, "
                    + "junto com id, nome, e-mail e perfil do usuário (RF02). Use o token no botão Authorize.",
            security = {}
    )
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
    @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = StandardError.class)))
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO data) {
        // 1. Valida e-mail e senha usando o Spring Security
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        authenticationManager.authenticate(usernamePassword);

        // 2. Busca os dados completos do usuário para gerar o token e responder
        Usuario usuario = usuarioRepository.findByEmail(data.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        String token = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario()
        ));
    }
}
