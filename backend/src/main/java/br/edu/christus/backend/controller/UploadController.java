package br.edu.christus.backend.controller;

import br.edu.christus.backend.dto.UploadAprovacaoDTO;
import br.edu.christus.backend.dto.UploadRejeicaoDTO;
import br.edu.christus.backend.dto.UploadResponseDTO;
import br.edu.christus.backend.exception.BadRequestException;
import br.edu.christus.backend.service.UploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Análise de uploads pelo ADMINISTRADOR (RF36 a RF44); o acesso é restrito no SecurityConfig.
// As rotas do professor (enviar, consultar, corrigir, excluir e reenviar) ficam para a A12.
@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService service;

    // RF36: fila de uploads PENDENTES
    @GetMapping("/pendentes")
    public List<UploadResponseDTO> listarPendentes() {
        return service.listarPendentes();
    }

    // RF37/RF40: aprova e cria o Livro automaticamente
    @PutMapping("/{id}/aprovar")
    public UploadResponseDTO aprovar(@PathVariable Long id, @Valid @RequestBody UploadAprovacaoDTO dto) {
        return service.aprovar(id, dto);
    }

    // RF38/RF39: rejeita; sem motivo retorna 400 (RN06)
    @PutMapping("/{id}/rejeitar")
    public UploadResponseDTO rejeitar(@PathVariable Long id,
                                      @Valid @RequestBody(required = false) UploadRejeicaoDTO dto) {
        // Sem corpo também é "rejeição sem motivo": mesma resposta do @NotBlank do DTO
        if (dto == null) {
            throw new BadRequestException("motivo: Informe o motivo da rejeição.");
        }
        return service.rejeitar(id, dto);
    }
}
