package com.projectBD.demo.controller;

import com.projectBD.demo.entity.Campanha;
import com.projectBD.demo.entity.CampanhaCenario;
import com.projectBD.demo.entity.Cenario;
import com.projectBD.demo.service.CenarioService;
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
@RequestMapping("/cenarios")
public class CenarioController {

    @Autowired
    private CenarioService service;

    @GetMapping
    public List<Cenario> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{idCenario}")
    public Cenario buscar(@PathVariable Long idCenario) {
        return service.buscarPorId(idCenario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cenario criar(@RequestBody Cenario cenario) {
        return service.criar(cenario);
    }

    @PutMapping("/{idCenario}")
    public Cenario atualizar(@PathVariable Long idCenario, @RequestBody Cenario cenario) {
        return service.atualizar(idCenario, cenario);
    }

    @DeleteMapping("/{idCenario}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idCenario) {
        service.remover(idCenario);
    }

    @GetMapping("/{idCenario}/campanhas")
    public List<Campanha> buscarCampanhas(@PathVariable Long idCenario) {
        return service.buscarCampanhas(idCenario);
    }

    @PostMapping("/{idCenario}/campanhas/{idCampanha}")
    @ResponseStatus(HttpStatus.CREATED)
    public CampanhaCenario associarCampanha(@PathVariable Long idCenario, @PathVariable Long idCampanha) {
        return service.associarCampanha(idCenario, idCampanha);
    }

    @DeleteMapping("/{idCenario}/campanhas/{idCampanha}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarCampanha(@PathVariable Long idCenario, @PathVariable Long idCampanha) {
        service.desassociarCampanha(idCenario, idCampanha);
    }
}
