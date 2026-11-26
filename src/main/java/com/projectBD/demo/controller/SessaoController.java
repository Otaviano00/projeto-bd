package com.projectBD.demo.controller;

import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.PersonagemSessao;
import com.projectBD.demo.entity.Sessao;
import com.projectBD.demo.service.SessaoService;
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
@RequestMapping("/campanhas/{idCampanha}/sessoes")
public class SessaoController {

    @Autowired
    private SessaoService service;

    @GetMapping
    public List<Sessao> listar(@PathVariable Long idCampanha) {
        return service.listarPorCampanha(idCampanha);
    }

    @GetMapping("/{numeroSessao}")
    public Sessao buscar(@PathVariable Long idCampanha, @PathVariable Long numeroSessao) {
        return service.buscarPorId(idCampanha, numeroSessao);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sessao criar(@PathVariable Long idCampanha, @RequestBody Sessao sessao) {
        return service.criar(idCampanha, sessao);
    }

    @PutMapping("/{numeroSessao}")
    public Sessao atualizar(@PathVariable Long idCampanha, @PathVariable Long numeroSessao, @RequestBody Sessao sessao) {
        return service.atualizar(idCampanha, numeroSessao, sessao);
    }

    @DeleteMapping("/{numeroSessao}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idCampanha, @PathVariable Long numeroSessao) {
        service.remover(idCampanha, numeroSessao);
    }

    @GetMapping("/{numeroSessao}/personagens")
    public List<Personagem> buscarPersonagens(@PathVariable Long idCampanha, @PathVariable Long numeroSessao) {
        return service.buscarPersonagens(idCampanha, numeroSessao);
    }

    @PostMapping("/{numeroSessao}/personagens/{idPersonagem}")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonagemSessao associarPersonagem(@PathVariable Long idCampanha, @PathVariable Long numeroSessao,
                                               @PathVariable Long idPersonagem) {
        return service.associarPersonagem(idCampanha, numeroSessao, idPersonagem);
    }

    @DeleteMapping("/{numeroSessao}/personagens/{idPersonagem}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarPersonagem(@PathVariable Long idCampanha, @PathVariable Long numeroSessao,
                                      @PathVariable Long idPersonagem) {
        service.desassociarPersonagem(idCampanha, numeroSessao, idPersonagem);
    }
}
