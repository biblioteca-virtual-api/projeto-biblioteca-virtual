package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.Genero;
import br.edu.christus.backend.dto.GeneroRequestDTO;
import br.edu.christus.backend.dto.GeneroResponseDTO;
import br.edu.christus.backend.dto.GeneroUpdateDTO;
import br.edu.christus.backend.exception.ResourceNotFoundException;
import br.edu.christus.backend.repository.GeneroRepository;
import br.edu.christus.backend.repository.LivroRepository;
import br.edu.christus.backend.repository.UploadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneroService {

    private final GeneroRepository generoRepository;
    private final LivroRepository livroRepository;
    private final UploadRepository uploadRepository;
    //declaração do repositorio
    //ponte que traduz os comandos para SQL no bd.

    //Service controla quando e como o GeneroRepository vai conversar com o banco
    //usa final pra generoRepository só pode ser atribuido uma unica vez
    //final tb garante que generoRepository sempre estara disponivel para GeneroService

    public GeneroService(GeneroRepository generoRepository,
                         LivroRepository livroRepository,
                         UploadRepository uploadRepository){

        this.generoRepository = generoRepository;
        this.livroRepository = livroRepository;
        this.uploadRepository = uploadRepository;
    }

    // O gênero é montado do zero a partir do DTO (que não tem id), então o save() sempre faz INSERT
    // e nunca sobrescreve (nem reativa) um gênero existente. Todo gênero nasce ativo.
    public GeneroResponseDTO create(GeneroRequestDTO dto){
        Genero genero = new Genero();
        genero.setName(dto.name());
        genero.setAtivo(true);
        return toResponse(generoRepository.save(genero));
    }


    public List<GeneroResponseDTO> listar(){

        return generoRepository.findAll().stream().map(this::toResponse).toList();
    }

    //buscar pelo id (feito por IA - exceção)
    public Genero buscarPorId(Long id_genero) {
        return generoRepository.findById(id_genero)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gênero não encontrado. Id: " + id_genero
                ));
    }


    // O id vem no DTO porque a rota PUT não tem {id}. Só o nome muda: o "ativo" continua como está.
    public GeneroResponseDTO update(GeneroUpdateDTO dto){
        Genero genero = buscarPorId(dto.id_genero());
        genero.setName(dto.name());
        return toResponse(generoRepository.save(genero));
    }


    public void delete(Long id_genero){
        Genero genero = buscarPorId(id_genero);

        // Uploads também contam: o upload é histórico (RN09) e a FK de tb_upload impede a exclusão
        boolean possuiLivrosOuUploads = livroRepository.existsByGenero_Id_genero(id_genero)
                || uploadRepository.existsByGenero_Id_genero(id_genero);

        if (possuiLivrosOuUploads){
            genero.setAtivo(false);
            generoRepository.save(genero);

        } else {
            generoRepository.delete(genero);
        }
    }
    //OBS se tiver livro (ou upload) associado nao pode excluir, apenas desativar boolean ativo = false

    // Conversão ENTIDADE -> DTO de saída
    private GeneroResponseDTO toResponse(Genero genero){
        return new GeneroResponseDTO(genero.getId_genero(), genero.getName(), genero.getAtivo());
    }


}


