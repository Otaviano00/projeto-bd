package com.projectBD.demo.service;

import com.projectBD.demo.dao.CampanhaDao;
import com.projectBD.demo.dao.SessaoDao;
import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.PersonagemSessao;
import com.projectBD.demo.entity.Sessao;
import com.projectBD.demo.entity.Sessao.SessaoPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessaoService {

    @Autowired
    private SessaoDao dao;

    @Autowired
    private CampanhaDao campanhaDao;

    public Sessao criar(Long idCampanha, Sessao sessao) {
        exigirCampanha(idCampanha);
        sessao.setId(new SessaoPK(idCampanha, 0L));
        validar(sessao);
        return dao.inserir(sessao);
    }

    public Sessao buscarPorId(Long idCampanha, Long numeroSessao) {
        return dao.buscarPorId(new SessaoPK(idCampanha, numeroSessao)).orElseThrow(
                () -> new RecursoNaoEncontradoException("Sessao nao encontrado: " + idCampanha + "/" + numeroSessao));
    }

    public List<Sessao> listarPorCampanha(Long idCampanha) {
        exigirCampanha(idCampanha);
        return dao.listarPorCampanha(idCampanha);
    }

    public Sessao atualizar(Long idCampanha, Long numeroSessao, Sessao sessao) {
        sessao.setId(new SessaoPK(idCampanha, numeroSessao));
        validar(sessao);
        if (!dao.atualizar(sessao)) {
            throw new RecursoNaoEncontradoException("Sessao nao encontrado: " + idCampanha + "/" + numeroSessao);
        }
        return buscarPorId(idCampanha, numeroSessao);
    }

    public void remover(Long idCampanha, Long numeroSessao) {
        if (!dao.deletar(new SessaoPK(idCampanha, numeroSessao))) {
            throw new RecursoNaoEncontradoException("Sessao nao encontrado: " + idCampanha + "/" + numeroSessao);
        }
    }

    public List<Personagem> buscarPersonagens(Long idCampanha, Long numeroSessao) {
        buscarPorId(idCampanha, numeroSessao);
        return dao.buscarPersonagens(new SessaoPK(idCampanha, numeroSessao));
    }

    public PersonagemSessao associarPersonagem(Long idCampanha, Long numeroSessao, Long idPersonagem) {
        buscarPorId(idCampanha, numeroSessao);
        return dao.associarPersonagem(new SessaoPK(idCampanha, numeroSessao), idPersonagem);
    }

    public void desassociarPersonagem(Long idCampanha, Long numeroSessao, Long idPersonagem) {
        if (!dao.desassociarPersonagem(new SessaoPK(idCampanha, numeroSessao), idPersonagem)) {
            throw new RecursoNaoEncontradoException("Vinculo entre Sessao e Personagem nao encontrado: " + idCampanha + "/" + numeroSessao + "/" + idPersonagem);
        }
    }

    private void exigirCampanha(Long idCampanha) {
        campanhaDao.buscarPorId(idCampanha).orElseThrow(
                () -> new RecursoNaoEncontradoException("Campanha nao encontrado: id=" + idCampanha));
    }

    private void validar(Sessao sessao) {
        Validacao.exigir(sessao.getDataHora(), "dataHora");
    }
}
