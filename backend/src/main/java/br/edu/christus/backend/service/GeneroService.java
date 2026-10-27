package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.Genero;
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

    public Genero create(Genero genero){
        genero.setAtivo(true);
        return generoRepository.save(genero);
    }


    public List<Genero> listar(){

        return generoRepository.findAll();
    }

    //buscar pelo id (feito por IA - exceção)
    public Genero buscarPorId(Long id_genero) {
        return generoRepository.findById(id_genero)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gênero não encontrado. Id: " + id_genero
                ));
    }


    public Genero update(Long id_genero, Genero generoAtualizado){
        Genero genero = buscarPorId(id_genero);
        genero.setName(generoAtualizado.getName());
        return generoRepository.save(genero);
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



}


