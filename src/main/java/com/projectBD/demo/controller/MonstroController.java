package com.projectBD.demo.controller;

import com.projectBD.demo.entity.Monstro;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaMonstro;
import com.projectBD.demo.service.MonstroService;
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
@RequestMapping("/monstros")
public class MonstroController {

    @Autowired
    private MonstroService service;

    @GetMapping
    public List<Monstro> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{idMonstro}")
    public Monstro buscar(@PathVariable Long idMonstro) {
        return service.buscarPorId(idMonstro);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Monstro criar(@RequestBody Monstro monstro) {
        return service.criar(monstro);
    }

    @PutMapping("/{idMonstro}")
    public Monstro atualizar(@PathVariable Long idMonstro, @RequestBody Monstro monstro) {
        return service.atualizar(idMonstro, monstro);
    }

    @DeleteMapping("/{idMonstro}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idMonstro) {
        service.remover(idMonstro);
    }

    @GetMapping("/{idMonstro}/sistemas")
    public List<Sistema> buscarSistemas(@PathVariable Long idMonstro) {
        return service.buscarSistemas(idMonstro);
    }

    @PostMapping("/{idMonstro}/sistemas/{idSistema}")
    @ResponseStatus(HttpStatus.CREATED)
    public SistemaMonstro associarSistema(@PathVariable Long idMonstro, @PathVariable Long idSistema, @RequestBody(required = false) byte[] fichaArquivo) {
        return service.associarSistema(idMonstro, idSistema, fichaArquivo);
    }

    @DeleteMapping("/{idMonstro}/sistemas/{idSistema}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarSistema(@PathVariable Long idMonstro, @PathVariable Long idSistema) {
        service.desassociarSistema(idMonstro, idSistema);
    }
}
