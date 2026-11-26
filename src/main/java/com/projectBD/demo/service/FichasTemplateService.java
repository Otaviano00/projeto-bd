package com.projectBD.demo.service;

import com.projectBD.demo.dao.FichasTemplateDao;
import com.projectBD.demo.dao.SistemaDao;
import com.projectBD.demo.entity.FichasTemplate;
import com.projectBD.demo.entity.FichasTemplate.FichasTemplatePK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FichasTemplateService {

    @Autowired
    private FichasTemplateDao dao;

    @Autowired
    private SistemaDao sistemaDao;

    public FichasTemplate criar(Long idSistema, FichasTemplate ficha) {
        exigirSistema(idSistema);
        ficha.setId(new FichasTemplatePK(0L, idSistema));
        validar(ficha);
        return dao.inserir(ficha);
    }

    public FichasTemplate buscarPorId(Long idSistema, Long numeroFicha) {
        return dao.buscarPorId(new FichasTemplatePK(numeroFicha, idSistema)).orElseThrow(
                () -> new RecursoNaoEncontradoException("FichasTemplate nao encontrado: " + idSistema + "/" + numeroFicha));
    }

    public List<FichasTemplate> listarPorSistema(Long idSistema) {
        exigirSistema(idSistema);
        return dao.listarPorSistema(idSistema);
    }

    public FichasTemplate atualizar(Long idSistema, Long numeroFicha, FichasTemplate ficha) {
        ficha.setId(new FichasTemplatePK(numeroFicha, idSistema));
        validar(ficha);
        if (!dao.atualizar(ficha)) {
            throw new RecursoNaoEncontradoException("FichasTemplate nao encontrado: " + idSistema + "/" + numeroFicha);
        }
        return buscarPorId(idSistema, numeroFicha);
    }

    public void remover(Long idSistema, Long numeroFicha) {
        if (!dao.deletar(new FichasTemplatePK(numeroFicha, idSistema))) {
            throw new RecursoNaoEncontradoException("FichasTemplate nao encontrado: " + idSistema + "/" + numeroFicha);
        }
    }

    private void exigirSistema(Long idSistema) {
        sistemaDao.buscarPorId(idSistema).orElseThrow(
                () -> new RecursoNaoEncontradoException("Sistema nao encontrado: id=" + idSistema));
    }

    private void validar(FichasTemplate ficha) {
        Validacao.exigir(ficha.getFichaArquivo(), "fichaArquivo");
        Validacao.exigir(ficha.getTipo(), "tipo");
    }
}
