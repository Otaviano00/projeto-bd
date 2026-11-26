package com.projectBD.demo.controller;

import com.projectBD.demo.entity.Item;
import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.PersonagemItem;
import com.projectBD.demo.entity.PersonagemSessao;
import com.projectBD.demo.entity.Sessao;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaPersonagem;
import com.projectBD.demo.service.PersonagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/personagens")
public class PersonagemController {

    @Autowired
    private PersonagemService service;

    @GetMapping
    public List<Personagem> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{idPersonagem}")
    public Personagem buscar(@PathVariable Long idPersonagem) {
        return service.buscarPorId(idPersonagem);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Personagem criar(@RequestBody Personagem personagem) {
        return service.criar(personagem);
    }

    @PutMapping("/{idPersonagem}")
    public Personagem atualizar(@PathVariable Long idPersonagem, @RequestBody Personagem personagem) {
        return service.atualizar(idPersonagem, personagem);
    }

    @DeleteMapping("/{idPersonagem}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idPersonagem) {
        service.remover(idPersonagem);
    }

    @GetMapping("/{idPersonagem}/itens")
    public List<Item> buscarItens(@PathVariable Long idPersonagem) {
        return service.buscarItens(idPersonagem);
    }

    @PostMapping("/{idPersonagem}/itens/{idItem}")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonagemItem associarItem(@PathVariable Long idPersonagem, @PathVariable Long idItem, @RequestParam(required = false) Long quantidade) {
        return service.associarItem(idPersonagem, idItem, quantidade);
    }

    @DeleteMapping("/{idPersonagem}/itens/{idItem}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarItem(@PathVariable Long idPersonagem, @PathVariable Long idItem) {
        service.desassociarItem(idPersonagem, idItem);
    }

    @GetMapping("/{idPersonagem}/sistemas")
    public List<Sistema> buscarSistemas(@PathVariable Long idPersonagem) {
        return service.buscarSistemas(idPersonagem);
    }

    @PostMapping("/{idPersonagem}/sistemas/{idSistema}")
    @ResponseStatus(HttpStatus.CREATED)
    public SistemaPersonagem associarSistema(@PathVariable Long idPersonagem, @PathVariable Long idSistema, @RequestBody(required = false) byte[] fichaArquivo) {
        return service.associarSistema(idPersonagem, idSistema, fichaArquivo);
    }

    @DeleteMapping("/{idPersonagem}/sistemas/{idSistema}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarSistema(@PathVariable Long idPersonagem, @PathVariable Long idSistema) {
        service.desassociarSistema(idPersonagem, idSistema);
    }

    @GetMapping("/{idPersonagem}/sessoes")
    public List<Sessao> buscarSessoes(@PathVariable Long idPersonagem) {
        return service.buscarSessoes(idPersonagem);
    }

    @PostMapping("/{idPersonagem}/sessoes/{idCampanha}/{numeroSessao}")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonagemSessao associarSessao(@PathVariable Long idPersonagem, @PathVariable Long idCampanha,
                                           @PathVariable Long numeroSessao) {
        return service.associarSessao(idPersonagem, idCampanha, numeroSessao);
    }

    @DeleteMapping("/{idPersonagem}/sessoes/{idCampanha}/{numeroSessao}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarSessao(@PathVariable Long idPersonagem, @PathVariable Long idCampanha,
                                  @PathVariable Long numeroSessao) {
        service.desassociarSessao(idPersonagem, idCampanha, numeroSessao);
    }
}
