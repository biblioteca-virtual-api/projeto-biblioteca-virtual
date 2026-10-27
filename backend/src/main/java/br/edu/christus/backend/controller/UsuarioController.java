package br.edu.christus.backend.controller;

import br.edu.christus.backend.dto.UsuarioRequestDTO;
import br.edu.christus.backend.dto.UsuarioResponseDTO;
import br.edu.christus.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Recebe e devolve DTOs, nunca a entidade Usuario: assim o cliente não envia id nem recebe a senha.
@RestController
@RequestMapping("/api/v1/users")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // Rota pública (SecurityConfig): sem token de ADMINISTRADOR, só cria ALUNO (RF03, regra no UsuarioService)
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    // Só ADMINISTRADOR (SecurityConfig): a listagem mostra o e-mail de todos os usuários
    @GetMapping
    public List<UsuarioResponseDTO> findAll() {
        return service.findUsers();
    }
}
