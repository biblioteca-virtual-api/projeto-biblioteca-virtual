package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.Genero;
import br.edu.christus.backend.repository.GeneroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneroService {

    private final GeneroRepository generoRepository;
    //declaração do repositorio
    //ponte que traduz os comandos para SQL no bd.

    //Service controla quando e como o GeneroRepository vai conversar com o banco
    //usa final pra generoRepository só pode ser atribuido uma unica vez
    //final tb garante que generoRepository sempre estara disponivel para GeneroService

    public GeneroService(GeneroRepository generoRepository){
        this.generoRepository = generoRepository;
    }

    //criar genero
    public Genero create(Genero genero){
        genero.setAtivo(true);
        return generoRepository.save(genero);
    }

    //Listar generos
    public List<Genero> listar(){
        return generoRepository.findAll();
    }

    //buscar pelo id (feito por IA - exceção)
    public Genero buscarPorId(Long id_genero){
        return generoRepository.findById(id_genero)
                .orElseThrow(() -> new RuntimeException("Gênero não encontrado."));
    }

    //Atualizar
    public Genero update(Long id_genero, Genero generoAtualizado){
        Genero genero = buscarPorId(id_genero);
        genero.setName(generoAtualizado.getName());
        return generoRepository.save(genero);
    }

    //excluir
    public void delete(Long id_genero){
        Genero genero = buscarPorId(id_genero);
        generoRepository.delete(genero);
    }
    //OBS FALTA CONCLUIR!!! se tiver livro associado nao pode excluir, apenas desativar boolean ativo = false



}

//OBS: FALTA Implementar a regra de exclusão/desativação do gênero.
    /*Depende da implementação da entidade Livro.
    Se o gênero possuir livros associados, não deve ser excluído fisicamente;
    nesse caso, deve ser apenas desativado (ativo = false). */
