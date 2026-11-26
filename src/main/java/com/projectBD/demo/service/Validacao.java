package com.projectBD.demo.service;

final class Validacao {

    private Validacao() {
    }

    static void exigir(Object valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException("Campo obrigatorio: " + campo);
        }
    }

    static void exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Campo obrigatorio: " + campo);
        }
    }
}
