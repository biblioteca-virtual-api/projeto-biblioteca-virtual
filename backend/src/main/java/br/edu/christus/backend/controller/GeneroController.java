package br.edu.christus.backend.controller;

import br.edu.christus.backend.dto.GeneroRequestDTO;
import br.edu.christus.backend.dto.GeneroResponseDTO;
import br.edu.christus.backend.dto.GeneroUpdateDTO;
import br.edu.christus.backend.service.GeneroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Recebe e devolve DTOs, nunca a entidade Genero: assim o POST não sobrescreve um gênero existente.
// Só o ADMINISTRADOR cadastra, edita e exclui (SecurityConfig); qualquer usuário logado lista.
@RestController //diz que é um controller que retorna JSON
@RequestMapping("/api/v1/generos/") //define a rota
public class GeneroController {

    @Autowired
    private GeneroService service;

    @PostMapping
    public GeneroResponseDTO create(@Valid @RequestBody GeneroRequestDTO dto){
        return service.create(dto);
    }

    // A rota não tem {id}: o id do gênero vem no corpo (GeneroUpdateDTO)
    @PutMapping
    public GeneroResponseDTO update(@Valid @RequestBody GeneroUpdateDTO dto){
        return service.update(dto);
    }

    @GetMapping
    public List<GeneroResponseDTO> findAll(){
        return service.listar();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){

        service.delete(id);
    }


}
