package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.StatusUpload;
import br.edu.christus.backend.domain.Upload;
import br.edu.christus.backend.dto.UploadRejeicaoDTO;
import br.edu.christus.backend.dto.UploadResponseDTO;
import br.edu.christus.backend.exception.ConflictException;
import br.edu.christus.backend.exception.ResourceNotFoundException;
import br.edu.christus.backend.repository.UploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Análise dos uploads pelo administrador (RF36 a RF44).
@Service
@RequiredArgsConstructor
public class UploadService {

    private final UploadRepository repository;

    // RF36: uploads aguardando análise.
    @Transactional(readOnly = true)
    public List<UploadResponseDTO> listarPendentes() {
        return repository.findByStatusOrderByIdAsc(StatusUpload.PENDENTE).stream()
                .map(this::toResponse).toList();
    }

    // RF38/RF39: rejeita o upload. O motivo é obrigatório (RN06), validado no DTO.
    @Transactional
    public UploadResponseDTO rejeitar(Long id, UploadRejeicaoDTO dto) {
        Upload upload = buscarPendente(id);
        upload.setStatus(StatusUpload.REJEITADO);
        upload.setMotivoRejeicao(dto.motivo()); // já chega sem espaços nas pontas (ver UploadRejeicaoDTO)
        return toResponse(upload);
    }

    // 404 se não existir; 409 se já foi analisado. A trava do findByIdParaAnalise impede
    // que duas aprovações simultâneas do mesmo upload criem dois livros (RN10).
    private Upload buscarPendente(Long id) {
        Upload upload = repository.findByIdParaAnalise(id)
                .orElseThrow(() -> new ResourceNotFoundException("Upload não encontrado. Id: " + id));
        if (upload.getStatus() != StatusUpload.PENDENTE) {
            throw new ConflictException("Só é possível analisar uploads PENDENTES. Este upload está "
                    + upload.getStatus() + ".");
        }
        return upload;
    }

    // Conversão manual, como no LivroService: o Genero usa id_genero/name.
    private UploadResponseDTO toResponse(Upload u) {
        return new UploadResponseDTO(
                u.getId(), u.getTitulo(), u.getAutor(),
                u.getGenero().getId_genero(), u.getGenero().getName(),
                u.getEdicao(), u.getAnoPublicacao(), u.getPdf(),
                u.getProfessor().getId(), u.getProfessor().getNome(),
                u.getStatus(), u.getMotivoRejeicao(),
                (u.getLivro() != null) ? u.getLivro().getId() : null);
    }
}
