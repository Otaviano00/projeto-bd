package com.projectBD.demo.service;

import com.projectBD.demo.dao.NPCDao;
import com.projectBD.demo.entity.Campanha;
import com.projectBD.demo.entity.CampanhaNpc;
import com.projectBD.demo.entity.NPC;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaNpc;
import com.projectBD.demo.entity.enums.StatusCampanhaNPC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NPCService {

    @Autowired
    private NPCDao dao;

    public NPC criar(NPC npc) {
        validar(npc);
        return dao.inserir(npc);
    }

    public NPC buscarPorId(Long idNpc) {
        return dao.buscarPorId(idNpc).orElseThrow(
                () -> new RecursoNaoEncontradoException("NPC nao encontrado: id=" + idNpc));
    }

    public List<NPC> listarTodos() {
        return dao.listarTodos();
    }

    public NPC atualizar(Long idNpc, NPC npc) {
        npc.setIdNpc(idNpc);
        validar(npc);
        if (!dao.atualizar(npc)) {
            throw new RecursoNaoEncontradoException("NPC nao encontrado: id=" + idNpc);
        }
        return buscarPorId(idNpc);
    }

    public void remover(Long idNpc) {
        if (!dao.deletar(idNpc)) {
            throw new RecursoNaoEncontradoException("NPC nao encontrado: id=" + idNpc);
        }
    }

    public List<Campanha> buscarCampanhas(Long idNpc) {
        buscarPorId(idNpc);
        return dao.buscarCampanhas(idNpc);
    }

    public CampanhaNpc associarCampanha(Long idNpc, Long idCampanha, StatusCampanhaNPC status) {
        buscarPorId(idNpc);
        return dao.associarCampanha(idNpc, idCampanha, status);
    }

    public void desassociarCampanha(Long idNpc, Long idCampanha) {
        if (!dao.desassociarCampanha(idNpc, idCampanha)) {
            throw new RecursoNaoEncontradoException("Vinculo entre NPC e Campanha nao encontrado: " + idNpc + "/" + idCampanha);
        }
    }

    public List<Sistema> buscarSistemas(Long idNpc) {
        buscarPorId(idNpc);
        return dao.buscarSistemas(idNpc);
    }

    public SistemaNpc associarSistema(Long idNpc, Long idSistema, byte[] fichaArquivo) {
        buscarPorId(idNpc);
        return dao.associarSistema(idNpc, idSistema, fichaArquivo);
    }

    public void desassociarSistema(Long idNpc, Long idSistema) {
        if (!dao.desassociarSistema(idNpc, idSistema)) {
            throw new RecursoNaoEncontradoException("Vinculo entre NPC e Sistema nao encontrado: " + idNpc + "/" + idSistema);
        }
    }

    private void validar(NPC npc) {
        Validacao.exigirTexto(npc.getNome(), "nome");
        Validacao.exigirTexto(npc.getDescricao(), "descricao");
    }
}
