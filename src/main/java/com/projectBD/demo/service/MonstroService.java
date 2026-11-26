package com.projectBD.demo.service;

import com.projectBD.demo.dao.MonstroDao;
import com.projectBD.demo.entity.Monstro;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaMonstro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonstroService {

    @Autowired
    private MonstroDao dao;

    public Monstro criar(Monstro monstro) {
        validar(monstro);
        return dao.inserir(monstro);
    }

    public Monstro buscarPorId(Long idMonstro) {
        return dao.buscarPorId(idMonstro).orElseThrow(
                () -> new RecursoNaoEncontradoException("Monstro nao encontrado: id=" + idMonstro));
    }

    public List<Monstro> listarTodos() {
        return dao.listarTodos();
    }

    public Monstro atualizar(Long idMonstro, Monstro monstro) {
        monstro.setIdMonstro(idMonstro);
        validar(monstro);
        if (!dao.atualizar(monstro)) {
            throw new RecursoNaoEncontradoException("Monstro nao encontrado: id=" + idMonstro);
        }
        return buscarPorId(idMonstro);
    }

    public void remover(Long idMonstro) {
        if (!dao.deletar(idMonstro)) {
            throw new RecursoNaoEncontradoException("Monstro nao encontrado: id=" + idMonstro);
        }
    }

    public List<Sistema> buscarSistemas(Long idMonstro) {
        buscarPorId(idMonstro);
        return dao.buscarSistemas(idMonstro);
    }

    public SistemaMonstro associarSistema(Long idMonstro, Long idSistema, byte[] fichaArquivo) {
        buscarPorId(idMonstro);
        return dao.associarSistema(idMonstro, idSistema, fichaArquivo);
    }

    public void desassociarSistema(Long idMonstro, Long idSistema) {
        if (!dao.desassociarSistema(idMonstro, idSistema)) {
            throw new RecursoNaoEncontradoException("Vinculo entre Monstro e Sistema nao encontrado: " + idMonstro + "/" + idSistema);
        }
    }

    private void validar(Monstro monstro) {
        Validacao.exigirTexto(monstro.getNome(), "nome");
        Validacao.exigirTexto(monstro.getDescricao(), "descricao");
    }
}
