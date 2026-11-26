package com.projectBD.demo.service;

import com.projectBD.demo.dao.PersonagemDao;
import com.projectBD.demo.entity.Item;
import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.PersonagemItem;
import com.projectBD.demo.entity.PersonagemSessao;
import com.projectBD.demo.entity.Sessao;
import com.projectBD.demo.entity.Sessao.SessaoPK;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaPersonagem;
import com.projectBD.demo.entity.enums.StatusPersonagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonagemService {

    @Autowired
    private PersonagemDao dao;

    public Personagem criar(Personagem personagem) {
        validar(personagem);
        return dao.inserir(personagem);
    }

    public Personagem buscarPorId(Long idPersonagem) {
        return dao.buscarPorId(idPersonagem).orElseThrow(
                () -> new RecursoNaoEncontradoException("Personagem nao encontrado: id=" + idPersonagem));
    }

    public List<Personagem> listarTodos() {
        return dao.listarTodos();
    }

    public Personagem atualizar(Long idPersonagem, Personagem personagem) {
        personagem.setIdPersonagem(idPersonagem);
        validar(personagem);
        if (!dao.atualizar(personagem)) {
            throw new RecursoNaoEncontradoException("Personagem nao encontrado: id=" + idPersonagem);
        }
        return buscarPorId(idPersonagem);
    }

    public void remover(Long idPersonagem) {
        if (!dao.deletar(idPersonagem)) {
            throw new RecursoNaoEncontradoException("Personagem nao encontrado: id=" + idPersonagem);
        }
    }

    public List<Item> buscarItens(Long idPersonagem) {
        buscarPorId(idPersonagem);
        return dao.buscarItens(idPersonagem);
    }

    public PersonagemItem associarItem(Long idPersonagem, Long idItem, Long quantidade) {
        buscarPorId(idPersonagem);
        return dao.associarItem(idPersonagem, idItem, quantidade);
    }

    public void desassociarItem(Long idPersonagem, Long idItem) {
        if (!dao.desassociarItem(idPersonagem, idItem)) {
            throw new RecursoNaoEncontradoException("Vinculo entre Personagem e Item nao encontrado: " + idPersonagem + "/" + idItem);
        }
    }

    public List<Sistema> buscarSistemas(Long idPersonagem) {
        buscarPorId(idPersonagem);
        return dao.buscarSistemas(idPersonagem);
    }

    public SistemaPersonagem associarSistema(Long idPersonagem, Long idSistema, byte[] fichaArquivo) {
        buscarPorId(idPersonagem);
        return dao.associarSistema(idPersonagem, idSistema, fichaArquivo);
    }

    public void desassociarSistema(Long idPersonagem, Long idSistema) {
        if (!dao.desassociarSistema(idPersonagem, idSistema)) {
            throw new RecursoNaoEncontradoException("Vinculo entre Personagem e Sistema nao encontrado: " + idPersonagem + "/" + idSistema);
        }
    }

    public List<Sessao> buscarSessoes(Long idPersonagem) {
        buscarPorId(idPersonagem);
        return dao.buscarSessoes(idPersonagem);
    }

    public PersonagemSessao associarSessao(Long idPersonagem, Long idCampanha, Long numeroSessao) {
        buscarPorId(idPersonagem);
        return dao.associarSessao(idPersonagem, new SessaoPK(idCampanha, numeroSessao));
    }

    public void desassociarSessao(Long idPersonagem, Long idCampanha, Long numeroSessao) {
        if (!dao.desassociarSessao(idPersonagem, new SessaoPK(idCampanha, numeroSessao))) {
            throw new RecursoNaoEncontradoException("Vinculo entre Personagem e Sessao nao encontrado: " + idPersonagem + "/" + idCampanha + "/" + numeroSessao);
        }
    }

    private void validar(Personagem personagem) {
        Validacao.exigir(personagem.getUsuario() == null ? null : personagem.getUsuario().getIdUsuario(), "usuario.idUsuario");
        Validacao.exigirTexto(personagem.getNome(), "nome");
        if (personagem.getStatus() == null) {
            personagem.setStatus(StatusPersonagem.ATIVO);
        }
    }
}
