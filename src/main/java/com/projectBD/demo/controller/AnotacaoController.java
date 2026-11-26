package com.projectBD.demo.controller;

import com.projectBD.demo.entity.Anotacao;
import com.projectBD.demo.service.AnotacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios/{idUsuario}/anotacoes")
public class AnotacaoController {

    @Autowired
    private AnotacaoService service;

    @GetMapping
    public List<Anotacao> listar(@PathVariable Long idUsuario) {
        return service.listarPorUsuario(idUsuario);
    }

    @GetMapping("/{numeroAnotacao}")
    public Anotacao buscar(@PathVariable Long idUsuario, @PathVariable Long numeroAnotacao) {
        return service.buscarPorId(idUsuario, numeroAnotacao);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Anotacao criar(@PathVariable Long idUsuario, @RequestBody Anotacao anotacao) {
        return service.criar(idUsuario, anotacao);
    }

    @PutMapping("/{numeroAnotacao}")
    public Anotacao atualizar(@PathVariable Long idUsuario, @PathVariable Long numeroAnotacao, @RequestBody Anotacao anotacao) {
        return service.atualizar(idUsuario, numeroAnotacao, anotacao);
    }

    @DeleteMapping("/{numeroAnotacao}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idUsuario, @PathVariable Long numeroAnotacao) {
        service.remover(idUsuario, numeroAnotacao);
    }
}
