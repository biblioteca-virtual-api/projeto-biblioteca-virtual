package br.edu.christus.backend.controller;

import br.edu.christus.backend.domain.Usuario;
import br.edu.christus.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UsuarioController {

    @Autowired
    private UserService service;

    @PostMapping
    public Usuario create(@RequestBody Usuario Usuario){
        return service.create(Usuario);
    }

    @GetMapping
    public List<Usuario> findAll(){
        return service.findUsers();
    }






}
