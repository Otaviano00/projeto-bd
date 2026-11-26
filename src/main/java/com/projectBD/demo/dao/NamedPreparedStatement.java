package com.projectBD.demo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class NamedPreparedStatement implements AutoCloseable {

    private final PreparedStatement statement;
    private final Map<String, List<Integer>> posicoes;
    private final Set<String> definidos = new HashSet<>();

    private NamedPreparedStatement(PreparedStatement statement, Map<String, List<Integer>> posicoes) {
        this.statement = statement;
        this.posicoes = posicoes;
    }

    static NamedPreparedStatement prepare(Connection conn, String sql, String... colunasGeradas)
            throws SQLException {
        Map<String, List<Integer>> posicoes = new LinkedHashMap<>();
        String sqlJdbc = converter(sql, posicoes);
        PreparedStatement statement = colunasGeradas.length == 0
                ? conn.prepareStatement(sqlJdbc)
                : conn.prepareStatement(sqlJdbc, colunasGeradas);
        return new NamedPreparedStatement(statement, posicoes);
    }

    void setParam(String nome, Object valor) throws SQLException {
        List<Integer> indices = posicoes.get(nome);
        if (indices == null) {
            throw new IllegalArgumentException("Parametro inexistente na query: :" + nome);
        }
        Object convertido = valor instanceof Enum<?> e ? e.name() : valor;
        for (int indice : indices) {
            statement.setObject(indice, convertido);
        }
        definidos.add(nome);
    }

    int executeUpdate() throws SQLException {
        validarParametros();
        return statement.executeUpdate();
    }

    ResultSet executeQuery() throws SQLException {
        validarParametros();
        return statement.executeQuery();
    }

    ResultSet getGeneratedKeys() throws SQLException {
        return statement.getGeneratedKeys();
    }

    @Override
    public void close() throws SQLException {
        statement.close();
    }

    private void validarParametros() {
        Set<String> faltando = new HashSet<>(posicoes.keySet());
        faltando.removeAll(definidos);
        if (!faltando.isEmpty()) {
            throw new IllegalStateException("Parametros sem valor: " + faltando);
        }
    }

    private static String converter(String sql, Map<String, List<Integer>> posicoes) {
        StringBuilder jdbc = new StringBuilder(sql.length());
        char aspas = 0;
        int proximoIndice = 1;

        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);

            if (aspas != 0) {
                jdbc.append(c);
                if (c == aspas) {
                    aspas = 0;
                }
                continue;
            }
            if (c == '\'' || c == '"' || c == '`') {
                aspas = c;
                jdbc.append(c);
                continue;
            }
            if (c == ':' && i + 1 < sql.length() && sql.charAt(i + 1) == ':') {
                jdbc.append("::");
                i++;
                continue;
            }
            if (c == ':' && i + 1 < sql.length() && inicioDeNome(sql.charAt(i + 1))) {
                int fim = i + 1;
                while (fim < sql.length() && parteDeNome(sql.charAt(fim))) {
                    fim++;
                }
                posicoes.computeIfAbsent(sql.substring(i + 1, fim), nome -> new ArrayList<>())
                        .add(proximoIndice++);
                jdbc.append('?');
                i = fim - 1;
                continue;
            }
            jdbc.append(c);
        }
        return jdbc.toString();
    }

    private static boolean inicioDeNome(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private static boolean parteDeNome(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }
}
