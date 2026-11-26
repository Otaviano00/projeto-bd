package com.projectBD.demo.controller;

import com.projectBD.demo.entity.Item;
import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.PersonagemItem;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaItem;
import com.projectBD.demo.service.ItemService;
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
@RequestMapping("/itens")
public class ItemController {

    @Autowired
    private ItemService service;

    @GetMapping
    public List<Item> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{idItem}")
    public Item buscar(@PathVariable Long idItem) {
        return service.buscarPorId(idItem);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item criar(@RequestBody Item item) {
        return service.criar(item);
    }

    @PutMapping("/{idItem}")
    public Item atualizar(@PathVariable Long idItem, @RequestBody Item item) {
        return service.atualizar(idItem, item);
    }

    @DeleteMapping("/{idItem}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idItem) {
        service.remover(idItem);
    }

    @GetMapping("/{idItem}/personagens")
    public List<Personagem> buscarPersonagens(@PathVariable Long idItem) {
        return service.buscarPersonagens(idItem);
    }

    @PostMapping("/{idItem}/personagens/{idPersonagem}")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonagemItem associarPersonagem(@PathVariable Long idItem, @PathVariable Long idPersonagem, @RequestParam(required = false) Long quantidade) {
        return service.associarPersonagem(idItem, idPersonagem, quantidade);
    }

    @DeleteMapping("/{idItem}/personagens/{idPersonagem}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarPersonagem(@PathVariable Long idItem, @PathVariable Long idPersonagem) {
        service.desassociarPersonagem(idItem, idPersonagem);
    }

    @GetMapping("/{idItem}/sistemas")
    public List<Sistema> buscarSistemas(@PathVariable Long idItem) {
        return service.buscarSistemas(idItem);
    }

    @PostMapping("/{idItem}/sistemas/{idSistema}")
    @ResponseStatus(HttpStatus.CREATED)
    public SistemaItem associarSistema(@PathVariable Long idItem, @PathVariable Long idSistema, @RequestBody(required = false) byte[] fichaArquivo) {
        return service.associarSistema(idItem, idSistema, fichaArquivo);
    }

    @DeleteMapping("/{idItem}/sistemas/{idSistema}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarSistema(@PathVariable Long idItem, @PathVariable Long idSistema) {
        service.desassociarSistema(idItem, idSistema);
    }
}
