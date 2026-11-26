package com.projectBD.demo.controller;

import com.projectBD.demo.entity.FichasTemplate;
import com.projectBD.demo.service.FichasTemplateService;
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
@RequestMapping("/sistemas/{idSistema}/fichas")
public class FichasTemplateController {

    @Autowired
    private FichasTemplateService service;

    @GetMapping
    public List<FichasTemplate> listar(@PathVariable Long idSistema) {
        return service.listarPorSistema(idSistema);
    }

    @GetMapping("/{numeroFicha}")
    public FichasTemplate buscar(@PathVariable Long idSistema, @PathVariable Long numeroFicha) {
        return service.buscarPorId(idSistema, numeroFicha);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FichasTemplate criar(@PathVariable Long idSistema, @RequestBody FichasTemplate ficha) {
        return service.criar(idSistema, ficha);
    }

    @PutMapping("/{numeroFicha}")
    public FichasTemplate atualizar(@PathVariable Long idSistema, @PathVariable Long numeroFicha, @RequestBody FichasTemplate ficha) {
        return service.atualizar(idSistema, numeroFicha, ficha);
    }

    @DeleteMapping("/{numeroFicha}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idSistema, @PathVariable Long numeroFicha) {
        service.remover(idSistema, numeroFicha);
    }
}
