package com.projectBD.demo.controller;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.projectBD.demo.dao.DaoException;
import com.projectBD.demo.service.RecursoNaoEncontradoException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail naoEncontrado(RecursoNaoEncontradoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail requisicaoInvalida(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DaoException.class)
    public ProblemDetail erroDeDados(DaoException e) {
        if (violaIntegridade(e)) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                    "Operacao viola a integridade dos dados (registro duplicado, inexistente ou em uso)");
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar o banco de dados");
    }

    private boolean violaIntegridade(Throwable erro) {
        for (Throwable atual = erro; atual != null; atual = atual.getCause()) {
            if (atual instanceof SQLException sql && sql.getSQLState() != null && sql.getSQLState().startsWith("23")) {
                return true;
            }
        }
        return false;
    }
}
