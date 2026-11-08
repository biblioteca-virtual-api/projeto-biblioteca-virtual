package br.edu.christus.backend.controller;

import br.edu.christus.backend.config.doc.ErroAcessoNegado;
import br.edu.christus.backend.config.doc.ErroArquivoGrande;
import br.edu.christus.backend.config.doc.ErroConflito;
import br.edu.christus.backend.config.doc.ErroDadosInvalidos;
import br.edu.christus.backend.config.doc.ErroNaoEncontrado;
import br.edu.christus.backend.config.doc.ErroRegraDeNegocio;
import br.edu.christus.backend.config.doc.ErroTipoNaoSuportado;
import br.edu.christus.backend.dto.UploadAprovacaoDTO;
import br.edu.christus.backend.dto.UploadRejeicaoDTO;
import br.edu.christus.backend.dto.UploadResponseDTO;
import br.edu.christus.backend.exception.BadRequestException;
import br.edu.christus.backend.service.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// Análise de uploads pelo ADMINISTRADOR (RF36 a RF44) e envio pelo PROFESSOR (RF30, RF31, RN07); o acesso é restrito no SecurityConfig.
@Tag(name = "Uploads", description = "Envio de PDFs pelo professor e análise pelo administrador (RF30, RF31, RF36 a RF44, RN04 a RN10)")
@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService service;

    // RF36: fila de uploads PENDENTES
    @Operation(
            summary = "Listar uploads pendentes",
            description = "**Perfil:** ADMINISTRADOR (RF36). Fila do envio mais antigo para o mais novo."
    )
    @ApiResponse(responseCode = "200", description = "Lista de uploads pendentes de análise")
    @ErroAcessoNegado
    @GetMapping("/pendentes")
    public List<UploadResponseDTO> listarPendentes() {
        return service.listarPendentes();
    }

    // RF37/RF40: aprova e cria o Livro automaticamente
    @Operation(
            summary = "Aprovar upload",
            description = "**Perfil:** ADMINISTRADOR (RF37, RF40 a RF44, RN10). Cria o Livro automaticamente com os dados do upload. "
                    + "Informe o ISBN e escolha o autor: `autorId` (já cadastrado) **ou** `novoAutor = true`, "
                    + "nunca os dois nem nenhum. Se algo falhar, o upload continua PENDENTE."
    )
    @ApiResponse(responseCode = "200", description = "Upload aprovado e Livro criado com sucesso")
    @ErroDadosInvalidos
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @ErroConflito
    @ErroRegraDeNegocio
    @PutMapping("/{id}/aprovar")
    public UploadResponseDTO aprovar(@PathVariable Long id, @Valid @RequestBody UploadAprovacaoDTO dto) {
        return service.aprovar(id, dto);
    }

    // RF38/RF39: rejeita; sem motivo retorna 400 (RN06)
    @Operation(
            summary = "Rejeitar upload",
            description = "**Perfil:** ADMINISTRADOR (RF38, RF39, RN06). O motivo é obrigatório (até 500 caracteres)."
    )
    @ApiResponse(responseCode = "200", description = "Upload rejeitado com motivo registrado")
    @ErroDadosInvalidos
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @ErroConflito
    @PutMapping("/{id}/rejeitar")
    public UploadResponseDTO rejeitar(@PathVariable Long id,
                                      @Valid @RequestBody(required = false) UploadRejeicaoDTO dto) {
        // Sem corpo também é "rejeição sem motivo": mesma resposta do @NotBlank do DTO
        if (dto == null) {
            throw new BadRequestException("motivo: Informe o motivo da rejeição.");
        }
        return service.rejeitar(id, dto);
    }

    // multipart (envia o pdf e dados na mesma requisição HTTP)
    @Operation(
            summary = "Enviar livro para análise",
            description = "**Perfil:** PROFESSOR (RF30, RN04). Formulário multipart com os dados do livro e o PDF (obrigatório, até 5 MB). O upload nasce PENDENTE."
    )
    @ApiResponse(responseCode = "200", description = "Upload enviado para análise com status PENDENTE")
    @ErroDadosInvalidos
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @ErroArquivoGrande
    @ErroTipoNaoSuportado
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadResponseDTO criar(
            @Parameter(description = "Título do livro", example = "Dom Casmurro") @RequestParam String titulo,
            @Parameter(description = "Nome do autor do livro", example = "Machado de Assis") @RequestParam String autor,
            @Parameter(description = "ID de um gênero cadastrado", example = "2") @RequestParam Long generoId,
            @Parameter(description = "Edição do livro", example = "2ª edição") @RequestParam String edicao,
            @Parameter(description = "Ano de publicação", example = "1899") @RequestParam Integer anoPublicacao,
            @Parameter(description = "Arquivo PDF do livro (até 5 MB)", schema = @Schema(type = "string", format = "binary")) @RequestParam MultipartFile arquivo) {

        return service.criar(
                titulo,
                autor,
                generoId,
                edicao,
                anoPublicacao,
                arquivo
        );
    }

    // Lista somente os uploads do prof autenticado
    @Operation(
            summary = "Listar meus uploads",
            description = "**Perfil:** PROFESSOR (RF31). Só os uploads do professor logado, do mais novo para o mais antigo."
    )
    @ApiResponse(responseCode = "200", description = "Lista de uploads do professor autenticado")
    @ErroAcessoNegado
    @GetMapping("/meus")
    public List<UploadResponseDTO> listarMeusUploads() {
        return service.listarMeusUploads();
    }

    // Professor edita somente uploads REJEITADOS e o PDF é opcional
    @Operation(
            summary = "Corrigir upload rejeitado",
            description = "**Perfil:** PROFESSOR dono do upload (RN07). Só funciona com upload REJEITADO; o PDF é opcional (sem PDF, mantém o atual). Mesmo formulário do envio."
    )
    @ApiResponse(responseCode = "200", description = "Upload corrigido com sucesso")
    @ErroDadosInvalidos
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @ErroConflito
    @ErroArquivoGrande
    @ErroTipoNaoSuportado
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadResponseDTO editar(
            @PathVariable Long id,
            @Parameter(description = "Título do livro", example = "Dom Casmurro") @RequestParam String titulo,
            @Parameter(description = "Nome do autor do livro", example = "Machado de Assis") @RequestParam String autor,
            @Parameter(description = "ID de um gênero cadastrado", example = "2") @RequestParam Long generoId,
            @Parameter(description = "Edição do livro", example = "2ª edição") @RequestParam String edicao,
            @Parameter(description = "Ano de publicação", example = "1899") @RequestParam Integer anoPublicacao,
            @Parameter(description = "Novo arquivo PDF (opcional na edição)", schema = @Schema(type = "string", format = "binary")) @RequestParam(required = false) MultipartFile arquivo) {

        return service.editar(
                id, titulo, autor, generoId, edicao, anoPublicacao, arquivo);
    }

    @Operation(
            summary = "Reenviar upload rejeitado",
            description = "**Perfil:** PROFESSOR dono do upload (RN07). Volta o upload para PENDENTE e limpa o motivo da rejeição."
    )
    @ApiResponse(responseCode = "200", description = "Upload reenviado para a fila de pendentes")
    @ErroAcessoNegado
    @ErroNaoEncontrado
    @ErroConflito
    @PutMapping("/{id}/reenviar")
    public UploadResponseDTO reenviar(@PathVariable Long id) {
        return service.reenviar(id);
    }

}
