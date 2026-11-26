package com.projectBD.demo.service;

import com.projectBD.demo.dao.CampanhaDao;
import com.projectBD.demo.entity.Campanha;
import com.projectBD.demo.entity.CampanhaCenario;
import com.projectBD.demo.entity.CampanhaNpc;
import com.projectBD.demo.entity.Cenario;
import com.projectBD.demo.entity.NPC;
import com.projectBD.demo.entity.enums.StatusCampanha;
import com.projectBD.demo.entity.enums.StatusCampanhaNPC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampanhaService {

    @Autowired
    private CampanhaDao dao;

    public Campanha criar(Campanha campanha) {
        validar(campanha);
        return dao.inserir(campanha);
    }

    public Campanha buscarPorId(Long idCampanha) {
        return dao.buscarPorId(idCampanha).orElseThrow(
                () -> new RecursoNaoEncontradoException("Campanha nao encontrado: id=" + idCampanha));
    }

    public List<Campanha> listarTodos() {
        return dao.listarTodos();
    }

    public Campanha atualizar(Long idCampanha, Campanha campanha) {
        campanha.setIdCampanha(idCampanha);
        validar(campanha);
        if (!dao.atualizar(campanha)) {
            throw new RecursoNaoEncontradoException("Campanha nao encontrado: id=" + idCampanha);
        }
        return buscarPorId(idCampanha);
    }

    public void remover(Long idCampanha) {
        if (!dao.deletar(idCampanha)) {
            throw new RecursoNaoEncontradoException("Campanha nao encontrado: id=" + idCampanha);
        }
    }

    public List<NPC> buscarNPCs(Long idCampanha) {
        buscarPorId(idCampanha);
        return dao.buscarNPCs(idCampanha);
    }

    public CampanhaNpc associarNPC(Long idCampanha, Long idNpc, StatusCampanhaNPC status) {
        buscarPorId(idCampanha);
        return dao.associarNPC(idCampanha, idNpc, status);
    }

    public void desassociarNPC(Long idCampanha, Long idNpc) {
        if (!dao.desassociarNPC(idCampanha, idNpc)) {
            throw new RecursoNaoEncontradoException("Vinculo entre Campanha e NPC nao encontrado: " + idCampanha + "/" + idNpc);
        }
    }

    public List<Cenario> buscarCenarios(Long idCampanha) {
        buscarPorId(idCampanha);
        return dao.buscarCenarios(idCampanha);
    }

    public CampanhaCenario associarCenario(Long idCampanha, Long idCenario) {
        buscarPorId(idCampanha);
        return dao.associarCenario(idCampanha, idCenario);
    }

    public void desassociarCenario(Long idCampanha, Long idCenario) {
        if (!dao.desassociarCenario(idCampanha, idCenario)) {
            throw new RecursoNaoEncontradoException("Vinculo entre Campanha e Cenario nao encontrado: " + idCampanha + "/" + idCenario);
        }
    }

    private void validar(Campanha campanha) {
        Validacao.exigir(campanha.getMestre() == null ? null : campanha.getMestre().getIdUsuario(), "mestre.idUsuario");
        Validacao.exigir(campanha.getSistema() == null ? null : campanha.getSistema().getIdSistema(), "sistema.idSistema");
        Validacao.exigirTexto(campanha.getNome(), "nome");
        if (campanha.getStatus() == null) {
            campanha.setStatus(StatusCampanha.ATIVA);
        }
    }
}
