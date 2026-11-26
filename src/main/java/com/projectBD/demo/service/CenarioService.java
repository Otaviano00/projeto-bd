package com.projectBD.demo.service;

import com.projectBD.demo.dao.CenarioDao;
import com.projectBD.demo.entity.Campanha;
import com.projectBD.demo.entity.CampanhaCenario;
import com.projectBD.demo.entity.Cenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CenarioService {

    @Autowired
    private CenarioDao dao;

    public Cenario criar(Cenario cenario) {
        validar(cenario);
        return dao.inserir(cenario);
    }

    public Cenario buscarPorId(Long idCenario) {
        return dao.buscarPorId(idCenario).orElseThrow(
                () -> new RecursoNaoEncontradoException("Cenario nao encontrado: id=" + idCenario));
    }

    public List<Cenario> listarTodos() {
        return dao.listarTodos();
    }

    public Cenario atualizar(Long idCenario, Cenario cenario) {
        cenario.setIdCenario(idCenario);
        validar(cenario);
        if (!dao.atualizar(cenario)) {
            throw new RecursoNaoEncontradoException("Cenario nao encontrado: id=" + idCenario);
        }
        return buscarPorId(idCenario);
    }

    public void remover(Long idCenario) {
        if (!dao.deletar(idCenario)) {
            throw new RecursoNaoEncontradoException("Cenario nao encontrado: id=" + idCenario);
        }
    }

    public List<Campanha> buscarCampanhas(Long idCenario) {
        buscarPorId(idCenario);
        return dao.buscarCampanhas(idCenario);
    }

    public CampanhaCenario associarCampanha(Long idCenario, Long idCampanha) {
        buscarPorId(idCenario);
        return dao.associarCampanha(idCenario, idCampanha);
    }

    public void desassociarCampanha(Long idCenario, Long idCampanha) {
        if (!dao.desassociarCampanha(idCenario, idCampanha)) {
            throw new RecursoNaoEncontradoException("Vinculo entre Cenario e Campanha nao encontrado: " + idCenario + "/" + idCampanha);
        }
    }

    private void validar(Cenario cenario) {
        Validacao.exigirTexto(cenario.getNome(), "nome");
        Validacao.exigirTexto(cenario.getDescricao(), "descricao");
    }
}
