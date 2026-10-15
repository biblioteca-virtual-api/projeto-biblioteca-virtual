package br.edu.christus.backend.controller;

import br.edu.christus.backend.domain.Genero;
import br.edu.christus.backend.service.GeneroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //diz que é um controller que retorna JSON
@RequestMapping("/api/v1/generos/") //define a rota
public class GeneroController {

    @Autowired
    private GeneroService service;

    @PostMapping
    public Genero create(@RequestBody Genero genero){
        return service.create(genero);
    }

    @PutMapping
    public Genero update(@RequestBody Genero genero){
        return service.update(genero.getId_genero(),genero);
    }

    @GetMapping
    public List<Genero> findAll(){
        return service.listar();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){

        service.delete(id);
    }


}
