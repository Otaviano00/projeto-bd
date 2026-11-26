package com.projectBD.demo.service;

import com.projectBD.demo.dao.SistemaDao;
import com.projectBD.demo.entity.Item;
import com.projectBD.demo.entity.Monstro;
import com.projectBD.demo.entity.NPC;
import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaItem;
import com.projectBD.demo.entity.SistemaMonstro;
import com.projectBD.demo.entity.SistemaNpc;
import com.projectBD.demo.entity.SistemaPersonagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SistemaService {

    @Autowired
    private SistemaDao dao;

    public Sistema criar(Sistema sistema) {
        validar(sistema);
        return dao.inserir(sistema);
    }

    public Sistema buscarPorId(Long idSistema) {
        return dao.buscarPorId(idSistema).orElseThrow(
                () -> new RecursoNaoEncontradoException("Sistema nao encontrado: id=" + idSistema));
    }

    public List<Sistema> listarTodos() {
        return dao.listarTodos();
    }

    public Sistema atualizar(Long idSistema, Sistema sistema) {
        sistema.setIdSistema(idSistema);
        validar(sistema);
        if (!dao.atualizar(sistema)) {
            throw new RecursoNaoEncontradoException("Sistema nao encontrado: id=" + idSistema);
        }
        return buscarPorId(idSistema);
    }

    public void remover(Long idSistema) {
        if (!dao.deletar(idSistema)) {
            throw new RecursoNaoEncontradoException("Sistema nao encontrado: id=" + idSistema);
        }
    }

    public List<Monstro> buscarMonstros(Long idSistema) {
        buscarPorId(idSistema);
        return dao.buscarMonstros(idSistema);
    }

    public SistemaMonstro associarMonstro(Long idSistema, Long idMonstro, byte[] fichaArquivo) {
        buscarPorId(idSistema);
        return dao.associarMonstro(idSistema, idMonstro, fichaArquivo);
    }

    public void desassociarMonstro(Long idSistema, Long idMonstro) {
        if (!dao.desassociarMonstro(idSistema, idMonstro)) {
            throw new RecursoNaoEncontradoException("Vinculo entre Sistema e Monstro nao encontrado: " + idSistema + "/" + idMonstro);
        }
    }

    public List<Item> buscarItens(Long idSistema) {
        buscarPorId(idSistema);
        return dao.buscarItens(idSistema);
    }

    public SistemaItem associarItem(Long idSistema, Long idItem, byte[] fichaArquivo) {
        buscarPorId(idSistema);
        return dao.associarItem(idSistema, idItem, fichaArquivo);
    }

    public void desassociarItem(Long idSistema, Long idItem) {
        if (!dao.desassociarItem(idSistema, idItem)) {
            throw new RecursoNaoEncontradoException("Vinculo entre Sistema e Item nao encontrado: " + idSistema + "/" + idItem);
        }
    }

    public List<Personagem> buscarPersonagens(Long idSistema) {
        buscarPorId(idSistema);
        return dao.buscarPersonagens(idSistema);
    }

    public SistemaPersonagem associarPersonagem(Long idSistema, Long idPersonagem, byte[] fichaArquivo) {
        buscarPorId(idSistema);
        return dao.associarPersonagem(idSistema, idPersonagem, fichaArquivo);
    }

    public void desassociarPersonagem(Long idSistema, Long idPersonagem) {
        if (!dao.desassociarPersonagem(idSistema, idPersonagem)) {
            throw new RecursoNaoEncontradoException("Vinculo entre Sistema e Personagem nao encontrado: " + idSistema + "/" + idPersonagem);
        }
    }

    public List<NPC> buscarNPCs(Long idSistema) {
        buscarPorId(idSistema);
        return dao.buscarNPCs(idSistema);
    }

    public SistemaNpc associarNPC(Long idSistema, Long idNpc, byte[] fichaArquivo) {
        buscarPorId(idSistema);
        return dao.associarNPC(idSistema, idNpc, fichaArquivo);
    }

    public void desassociarNPC(Long idSistema, Long idNpc) {
        if (!dao.desassociarNPC(idSistema, idNpc)) {
            throw new RecursoNaoEncontradoException("Vinculo entre Sistema e NPC nao encontrado: " + idSistema + "/" + idNpc);
        }
    }

    private void validar(Sistema sistema) {
        Validacao.exigirTexto(sistema.getNome(), "nome");
    }
}
