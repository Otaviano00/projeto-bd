package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Monstro;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaMonstro;
import com.projectBD.demo.entity.SistemaMonstro.SistemaMonstroPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;

@Repository
class SistemaMonstroDao {

    private static final String INSERT = """
            INSERT INTO SISTEMA_MONSTRO (id_sistema, id_monstro, ficha_arquivo)
            VALUES (:idSistema, :idMonstro, :fichaArquivo)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_sistema, id_monstro, ficha_arquivo
              FROM SISTEMA_MONSTRO
             WHERE id_sistema = :idSistema AND id_monstro = :idMonstro
            """;

    private static final String SELECT_MONSTRO_POR_SISTEMA = """
            SELECT t.id_monstro, t.nome, t.descricao, t.historia, t.imagem
              FROM SISTEMA_MONSTRO r
              JOIN MONSTRO t ON t.id_monstro = r.id_monstro
             WHERE r.id_sistema = :idSistema
             ORDER BY t.id_monstro
            """;

    private static final String SELECT_SISTEMA_POR_MONSTRO = """
            SELECT t.id_sistema, t.nome, t.editora, t.edicao, t.livro_regras
              FROM SISTEMA_MONSTRO r
              JOIN SISTEMA t ON t.id_sistema = r.id_sistema
             WHERE r.id_monstro = :idMonstro
             ORDER BY t.id_sistema
            """;

    private static final String UPDATE = """
            UPDATE SISTEMA_MONSTRO
               SET ficha_arquivo = :fichaArquivo
             WHERE id_sistema = :idSistema AND id_monstro = :idMonstro
            """;

    private static final String DELETE = """
            DELETE FROM SISTEMA_MONSTRO
             WHERE id_sistema = :idSistema AND id_monstro = :idMonstro
            """;

    @Autowired
    private DataSource dataSource;

    SistemaMonstro inserir(SistemaMonstro vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        SistemaMonstroPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT)) {

            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idMonstro", pk.getIdMonstro());
            ps.setParam("fichaArquivo", vinculo.getFichaArquivo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir SISTEMA_MONSTRO", e);
        }

        return buscarPorId(pk).orElseThrow(
                () -> new DaoException("SISTEMA_MONSTRO inserido nao foi encontrado: " + pk));
    }

    Optional<SistemaMonstro> buscarPorId(SistemaMonstroPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idMonstro", pk.getIdMonstro());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar SISTEMA_MONSTRO " + pk, e);
        }
    }

    List<Monstro> listarMonstrosPorSistema(Long idSistema) {
        Objects.requireNonNull(idSistema, "idSistema");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_MONSTRO_POR_SISTEMA)) {

            ps.setParam("idSistema", idSistema);
            try (ResultSet rs = ps.executeQuery()) {
                List<Monstro> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(MonstroDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar MONSTRO de SISTEMA id= " + idSistema, e);
        }
    }

    List<Sistema> listarSistemasPorMonstro(Long idMonstro) {
        Objects.requireNonNull(idMonstro, "idMonstro");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_SISTEMA_POR_MONSTRO)) {

            ps.setParam("idMonstro", idMonstro);
            try (ResultSet rs = ps.executeQuery()) {
                List<Sistema> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(SistemaDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar SISTEMA de MONSTRO id= " + idMonstro, e);
        }
    }

    boolean atualizar(SistemaMonstro vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        SistemaMonstroPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("fichaArquivo", vinculo.getFichaArquivo());
            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idMonstro", pk.getIdMonstro());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar SISTEMA_MONSTRO " + pk, e);
        }
    }

    boolean deletar(SistemaMonstroPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idMonstro", pk.getIdMonstro());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar SISTEMA_MONSTRO " + pk, e);
        }
    }

    private SistemaMonstro mapear(ResultSet rs) throws SQLException {
        SistemaMonstro vinculo = new SistemaMonstro();
        long idSistema = rs.getLong("id_sistema");
        long idMonstro = rs.getLong("id_monstro");
        vinculo.setId(new SistemaMonstroPK(idSistema, idMonstro));
        vinculo.setSistema(Sistema.ref(idSistema));
        vinculo.setMonstro(Monstro.ref(idMonstro));
        vinculo.setFichaArquivo(rs.getBytes("ficha_arquivo"));
        return vinculo;
    }
}
