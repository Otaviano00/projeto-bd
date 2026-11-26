package com.projectBD.demo.service;

import com.projectBD.demo.dao.UsuarioDao;
import com.projectBD.demo.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioDao dao;

    public Usuario criar(Usuario usuario) {
        validar(usuario);
        return dao.inserir(usuario);
    }

    public Usuario buscarPorId(Long idUsuario) {
        return dao.buscarPorId(idUsuario).orElseThrow(
                () -> new RecursoNaoEncontradoException("Usuario nao encontrado: id=" + idUsuario));
    }

    public List<Usuario> listarTodos() {
        return dao.listarTodos();
    }

    public Usuario atualizar(Long idUsuario, Usuario usuario) {
        usuario.setIdUsuario(idUsuario);
        validar(usuario);
        if (!dao.atualizar(usuario)) {
            throw new RecursoNaoEncontradoException("Usuario nao encontrado: id=" + idUsuario);
        }
        return buscarPorId(idUsuario);
    }

    public void remover(Long idUsuario) {
        if (!dao.deletar(idUsuario)) {
            throw new RecursoNaoEncontradoException("Usuario nao encontrado: id=" + idUsuario);
        }
    }

    private void validar(Usuario usuario) {
        Validacao.exigirTexto(usuario.getNome(), "nome");
        Validacao.exigirTexto(usuario.getEmail(), "email");
        Validacao.exigirTexto(usuario.getHashSenha(), "hashSenha");
        Validacao.exigir(usuario.getDataNascimento(), "dataNascimento");
    }
}
