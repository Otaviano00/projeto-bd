package com.projectBD.demo.service;

import com.projectBD.demo.dao.AnotacaoDao;
import com.projectBD.demo.dao.UsuarioDao;
import com.projectBD.demo.entity.Anotacao;
import com.projectBD.demo.entity.Anotacao.AnotacaoPK;
import com.projectBD.demo.entity.enums.Visibilidade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnotacaoService {

    @Autowired
    private AnotacaoDao dao;

    @Autowired
    private UsuarioDao usuarioDao;

    public Anotacao criar(Long idUsuario, Anotacao anotacao) {
        exigirUsuario(idUsuario);
        anotacao.setId(new AnotacaoPK(idUsuario, 0L));
        validar(anotacao);
        return dao.inserir(anotacao);
    }

    public Anotacao buscarPorId(Long idUsuario, Long numeroAnotacao) {
        return dao.buscarPorId(new AnotacaoPK(idUsuario, numeroAnotacao)).orElseThrow(
                () -> new RecursoNaoEncontradoException("Anotacao nao encontrado: " + idUsuario + "/" + numeroAnotacao));
    }

    public List<Anotacao> listarPorUsuario(Long idUsuario) {
        exigirUsuario(idUsuario);
        return dao.listarPorUsuario(idUsuario);
    }

    public Anotacao atualizar(Long idUsuario, Long numeroAnotacao, Anotacao anotacao) {
        anotacao.setId(new AnotacaoPK(idUsuario, numeroAnotacao));
        validar(anotacao);
        if (!dao.atualizar(anotacao)) {
            throw new RecursoNaoEncontradoException("Anotacao nao encontrado: " + idUsuario + "/" + numeroAnotacao);
        }
        return buscarPorId(idUsuario, numeroAnotacao);
    }

    public void remover(Long idUsuario, Long numeroAnotacao) {
        if (!dao.deletar(new AnotacaoPK(idUsuario, numeroAnotacao))) {
            throw new RecursoNaoEncontradoException("Anotacao nao encontrado: " + idUsuario + "/" + numeroAnotacao);
        }
    }

    private void exigirUsuario(Long idUsuario) {
        usuarioDao.buscarPorId(idUsuario).orElseThrow(
                () -> new RecursoNaoEncontradoException("Usuario nao encontrado: id=" + idUsuario));
    }

    private void validar(Anotacao anotacao) {
        Validacao.exigirTexto(anotacao.getTitulo(), "titulo");
        if (anotacao.getVisibilidade() == null) {
            anotacao.setVisibilidade(Visibilidade.PRIVADA);
        }
    }
}
