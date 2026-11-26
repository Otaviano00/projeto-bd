package com.projectBD.demo.controller;

import com.projectBD.demo.entity.Campanha;
import com.projectBD.demo.entity.CampanhaCenario;
import com.projectBD.demo.entity.CampanhaNpc;
import com.projectBD.demo.entity.Cenario;
import com.projectBD.demo.entity.NPC;
import com.projectBD.demo.entity.enums.StatusCampanhaNPC;
import com.projectBD.demo.service.CampanhaService;
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
@RequestMapping("/campanhas")
public class CampanhaController {

    @Autowired
    private CampanhaService service;

    @GetMapping
    public List<Campanha> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{idCampanha}")
    public Campanha buscar(@PathVariable Long idCampanha) {
        return service.buscarPorId(idCampanha);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Campanha criar(@RequestBody Campanha campanha) {
        return service.criar(campanha);
    }

    @PutMapping("/{idCampanha}")
    public Campanha atualizar(@PathVariable Long idCampanha, @RequestBody Campanha campanha) {
        return service.atualizar(idCampanha, campanha);
    }

    @DeleteMapping("/{idCampanha}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idCampanha) {
        service.remover(idCampanha);
    }

    @GetMapping("/{idCampanha}/npcs")
    public List<NPC> buscarNPCs(@PathVariable Long idCampanha) {
        return service.buscarNPCs(idCampanha);
    }

    @PostMapping("/{idCampanha}/npcs/{idNpc}")
    @ResponseStatus(HttpStatus.CREATED)
    public CampanhaNpc associarNPC(@PathVariable Long idCampanha, @PathVariable Long idNpc, @RequestParam(required = false) StatusCampanhaNPC status) {
        return service.associarNPC(idCampanha, idNpc, status);
    }

    @DeleteMapping("/{idCampanha}/npcs/{idNpc}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarNPC(@PathVariable Long idCampanha, @PathVariable Long idNpc) {
        service.desassociarNPC(idCampanha, idNpc);
    }

    @GetMapping("/{idCampanha}/cenarios")
    public List<Cenario> buscarCenarios(@PathVariable Long idCampanha) {
        return service.buscarCenarios(idCampanha);
    }

    @PostMapping("/{idCampanha}/cenarios/{idCenario}")
    @ResponseStatus(HttpStatus.CREATED)
    public CampanhaCenario associarCenario(@PathVariable Long idCampanha, @PathVariable Long idCenario) {
        return service.associarCenario(idCampanha, idCenario);
    }

    @DeleteMapping("/{idCampanha}/cenarios/{idCenario}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarCenario(@PathVariable Long idCampanha, @PathVariable Long idCenario) {
        service.desassociarCenario(idCampanha, idCenario);
    }
}
