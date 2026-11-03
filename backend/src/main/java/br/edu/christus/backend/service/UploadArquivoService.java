
package br.edu.christus.backend.service;

import br.edu.christus.backend.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UploadArquivoService {

    private static final long TAMANHO_MAXIMO = 5 * 1024 * 1024;

    private final Path diretorio;

    public UploadArquivoService(
            @Value("${app.upload.dir:uploads}") String diretorio) {
        this.diretorio = Paths.get(diretorio)
                .toAbsolutePath()
                .normalize();
    }

    public String salvar(MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new BadRequestException("O arquivo PDF é obrigatório.");
        }

        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new BadRequestException(
                    "O arquivo PDF não pode ultrapassar 5 MB.");
        }

        String nomeOriginal = arquivo.getOriginalFilename();

        if (nomeOriginal == null ||
                !nomeOriginal.toLowerCase().endsWith(".pdf")) {
            throw new BadRequestException(
                    "O arquivo enviado deve estar no formato PDF.");
        }

        String nomeArquivo = UUID.randomUUID() + ".pdf";

        try {
            Files.createDirectories(diretorio);

            Path destino = diretorio.resolve(nomeArquivo).normalize();

            if (!destino.startsWith(diretorio)) {
                throw new BadRequestException("Caminho de arquivo inválido.");
            }

            Files.copy(arquivo.getInputStream(), destino);

            return diretorio.relativize(destino).toString();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível salvar o arquivo PDF.", e);
        }
    }

    public void excluir(String caminhoArquivo) {

        if (caminhoArquivo == null || caminhoArquivo.isBlank()) {
            return;
        }

        try {
            Path arquivo = diretorio.resolve(caminhoArquivo).normalize();

            if (!arquivo.startsWith(diretorio)) {
                throw new BadRequestException("Caminho de arquivo inválido.");
            }

            Files.deleteIfExists(arquivo);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível excluir o arquivo PDF.", e);
        }
    }
}
