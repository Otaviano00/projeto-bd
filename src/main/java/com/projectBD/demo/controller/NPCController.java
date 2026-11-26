package com.projectBD.demo.controller;

import com.projectBD.demo.entity.Campanha;
import com.projectBD.demo.entity.CampanhaNpc;
import com.projectBD.demo.entity.NPC;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaNpc;
import com.projectBD.demo.entity.enums.StatusCampanhaNPC;
import com.projectBD.demo.service.NPCService;
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
@RequestMapping("/npcs")
public class NPCController {

    @Autowired
    private NPCService service;

    @GetMapping
    public List<NPC> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{idNpc}")
    public NPC buscar(@PathVariable Long idNpc) {
        return service.buscarPorId(idNpc);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NPC criar(@RequestBody NPC npc) {
        return service.criar(npc);
    }

    @PutMapping("/{idNpc}")
    public NPC atualizar(@PathVariable Long idNpc, @RequestBody NPC npc) {
        return service.atualizar(idNpc, npc);
    }

    @DeleteMapping("/{idNpc}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idNpc) {
        service.remover(idNpc);
    }

    @GetMapping("/{idNpc}/campanhas")
    public List<Campanha> buscarCampanhas(@PathVariable Long idNpc) {
        return service.buscarCampanhas(idNpc);
    }

    @PostMapping("/{idNpc}/campanhas/{idCampanha}")
    @ResponseStatus(HttpStatus.CREATED)
    public CampanhaNpc associarCampanha(@PathVariable Long idNpc, @PathVariable Long idCampanha, @RequestParam(required = false) StatusCampanhaNPC status) {
        return service.associarCampanha(idNpc, idCampanha, status);
    }

    @DeleteMapping("/{idNpc}/campanhas/{idCampanha}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarCampanha(@PathVariable Long idNpc, @PathVariable Long idCampanha) {
        service.desassociarCampanha(idNpc, idCampanha);
    }

    @GetMapping("/{idNpc}/sistemas")
    public List<Sistema> buscarSistemas(@PathVariable Long idNpc) {
        return service.buscarSistemas(idNpc);
    }

    @PostMapping("/{idNpc}/sistemas/{idSistema}")
    @ResponseStatus(HttpStatus.CREATED)
    public SistemaNpc associarSistema(@PathVariable Long idNpc, @PathVariable Long idSistema, @RequestBody(required = false) byte[] fichaArquivo) {
        return service.associarSistema(idNpc, idSistema, fichaArquivo);
    }

    @DeleteMapping("/{idNpc}/sistemas/{idSistema}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarSistema(@PathVariable Long idNpc, @PathVariable Long idSistema) {
        service.desassociarSistema(idNpc, idSistema);
    }
}
