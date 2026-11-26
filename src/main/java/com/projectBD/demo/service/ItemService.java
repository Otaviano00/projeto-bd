package com.projectBD.demo.service;

import com.projectBD.demo.dao.ItemDao;
import com.projectBD.demo.entity.Item;
import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.PersonagemItem;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemDao dao;

    public Item criar(Item item) {
        validar(item);
        return dao.inserir(item);
    }

    public Item buscarPorId(Long idItem) {
        return dao.buscarPorId(idItem).orElseThrow(
                () -> new RecursoNaoEncontradoException("Item nao encontrado: id=" + idItem));
    }

    public List<Item> listarTodos() {
        return dao.listarTodos();
    }

    public Item atualizar(Long idItem, Item item) {
        item.setIdItem(idItem);
        validar(item);
        if (!dao.atualizar(item)) {
            throw new RecursoNaoEncontradoException("Item nao encontrado: id=" + idItem);
        }
        return buscarPorId(idItem);
    }

    public void remover(Long idItem) {
        if (!dao.deletar(idItem)) {
            throw new RecursoNaoEncontradoException("Item nao encontrado: id=" + idItem);
        }
    }

    public List<Personagem> buscarPersonagens(Long idItem) {
        buscarPorId(idItem);
        return dao.buscarPersonagens(idItem);
    }

    public PersonagemItem associarPersonagem(Long idItem, Long idPersonagem, Long quantidade) {
        buscarPorId(idItem);
        return dao.associarPersonagem(idItem, idPersonagem, quantidade);
    }

    public void desassociarPersonagem(Long idItem, Long idPersonagem) {
        if (!dao.desassociarPersonagem(idItem, idPersonagem)) {
            throw new RecursoNaoEncontradoException("Vinculo entre Item e Personagem nao encontrado: " + idItem + "/" + idPersonagem);
        }
    }

    public List<Sistema> buscarSistemas(Long idItem) {
        buscarPorId(idItem);
        return dao.buscarSistemas(idItem);
    }

    public SistemaItem associarSistema(Long idItem, Long idSistema, byte[] fichaArquivo) {
        buscarPorId(idItem);
        return dao.associarSistema(idItem, idSistema, fichaArquivo);
    }

    public void desassociarSistema(Long idItem, Long idSistema) {
        if (!dao.desassociarSistema(idItem, idSistema)) {
            throw new RecursoNaoEncontradoException("Vinculo entre Item e Sistema nao encontrado: " + idItem + "/" + idSistema);
        }
    }

    private void validar(Item item) {
        Validacao.exigirTexto(item.getNome(), "nome");
    }
}
