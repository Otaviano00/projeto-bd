package com.projectBD.demo.controller;

import com.projectBD.demo.entity.Item;
import com.projectBD.demo.entity.Monstro;
import com.projectBD.demo.entity.NPC;
import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaItem;
import com.projectBD.demo.entity.SistemaMonstro;
import com.projectBD.demo.entity.SistemaNpc;
import com.projectBD.demo.entity.SistemaPersonagem;
import com.projectBD.demo.service.SistemaService;
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
@RequestMapping("/sistemas")
public class SistemaController {

    @Autowired
    private SistemaService service;

    @GetMapping
    public List<Sistema> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{idSistema}")
    public Sistema buscar(@PathVariable Long idSistema) {
        return service.buscarPorId(idSistema);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sistema criar(@RequestBody Sistema sistema) {
        return service.criar(sistema);
    }

    @PutMapping("/{idSistema}")
    public Sistema atualizar(@PathVariable Long idSistema, @RequestBody Sistema sistema) {
        return service.atualizar(idSistema, sistema);
    }

    @DeleteMapping("/{idSistema}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idSistema) {
        service.remover(idSistema);
    }

    @GetMapping("/{idSistema}/monstros")
    public List<Monstro> buscarMonstros(@PathVariable Long idSistema) {
        return service.buscarMonstros(idSistema);
    }

    @PostMapping("/{idSistema}/monstros/{idMonstro}")
    @ResponseStatus(HttpStatus.CREATED)
    public SistemaMonstro associarMonstro(@PathVariable Long idSistema, @PathVariable Long idMonstro, @RequestBody(required = false) byte[] fichaArquivo) {
        return service.associarMonstro(idSistema, idMonstro, fichaArquivo);
    }

    @DeleteMapping("/{idSistema}/monstros/{idMonstro}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarMonstro(@PathVariable Long idSistema, @PathVariable Long idMonstro) {
        service.desassociarMonstro(idSistema, idMonstro);
    }

    @GetMapping("/{idSistema}/itens")
    public List<Item> buscarItens(@PathVariable Long idSistema) {
        return service.buscarItens(idSistema);
    }

    @PostMapping("/{idSistema}/itens/{idItem}")
    @ResponseStatus(HttpStatus.CREATED)
    public SistemaItem associarItem(@PathVariable Long idSistema, @PathVariable Long idItem, @RequestBody(required = false) byte[] fichaArquivo) {
        return service.associarItem(idSistema, idItem, fichaArquivo);
    }

    @DeleteMapping("/{idSistema}/itens/{idItem}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarItem(@PathVariable Long idSistema, @PathVariable Long idItem) {
        service.desassociarItem(idSistema, idItem);
    }

    @GetMapping("/{idSistema}/personagens")
    public List<Personagem> buscarPersonagens(@PathVariable Long idSistema) {
        return service.buscarPersonagens(idSistema);
    }

    @PostMapping("/{idSistema}/personagens/{idPersonagem}")
    @ResponseStatus(HttpStatus.CREATED)
    public SistemaPersonagem associarPersonagem(@PathVariable Long idSistema, @PathVariable Long idPersonagem, @RequestBody(required = false) byte[] fichaArquivo) {
        return service.associarPersonagem(idSistema, idPersonagem, fichaArquivo);
    }

    @DeleteMapping("/{idSistema}/personagens/{idPersonagem}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarPersonagem(@PathVariable Long idSistema, @PathVariable Long idPersonagem) {
        service.desassociarPersonagem(idSistema, idPersonagem);
    }

    @GetMapping("/{idSistema}/npcs")
    public List<NPC> buscarNPCs(@PathVariable Long idSistema) {
        return service.buscarNPCs(idSistema);
    }

    @PostMapping("/{idSistema}/npcs/{idNpc}")
    @ResponseStatus(HttpStatus.CREATED)
    public SistemaNpc associarNPC(@PathVariable Long idSistema, @PathVariable Long idNpc, @RequestBody(required = false) byte[] fichaArquivo) {
        return service.associarNPC(idSistema, idNpc, fichaArquivo);
    }

    @DeleteMapping("/{idSistema}/npcs/{idNpc}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarNPC(@PathVariable Long idSistema, @PathVariable Long idNpc) {
        service.desassociarNPC(idSistema, idNpc);
    }
}
