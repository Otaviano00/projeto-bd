package com.projectBD.demo.dao;

import com.projectBD.demo.entity.FichasTemplate;
import com.projectBD.demo.entity.FichasTemplate.FichasTemplatePK;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.enums.TipoFicha;
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
public class FichasTemplateDao {

    private static final String INSERT = """
            INSERT INTO FICHAS_TEMPLATE (numero_ficha, id_sistema, ficha_arquivo, tipo)
            VALUES (:numeroFicha, :idSistema, :fichaArquivo, :tipo)
            """;

    private static final String SELECT_BY_ID = """
            SELECT numero_ficha, id_sistema, ficha_arquivo, tipo
              FROM FICHAS_TEMPLATE
             WHERE numero_ficha = :numeroFicha AND id_sistema = :idSistema
            """;

    private static final String SELECT_ALL = """
            SELECT numero_ficha, id_sistema, ficha_arquivo, tipo
              FROM FICHAS_TEMPLATE
             ORDER BY numero_ficha, id_sistema
            """;

    private static final String SELECT_POR_SISTEMA = """
            SELECT numero_ficha, id_sistema, ficha_arquivo, tipo
              FROM FICHAS_TEMPLATE
             WHERE id_sistema = :idSistema
             ORDER BY numero_ficha
            """;

    private static final String NEXT_NUMERO = """
            SELECT COALESCE(MAX(numero_ficha), 0) + 1
              FROM FICHAS_TEMPLATE
             WHERE id_sistema = :idSistema
            """;

    private static final String UPDATE = """
            UPDATE FICHAS_TEMPLATE
               SET ficha_arquivo = :fichaArquivo, tipo = :tipo
             WHERE numero_ficha = :numeroFicha AND id_sistema = :idSistema
            """;

    private static final String DELETE = """
            DELETE FROM FICHAS_TEMPLATE
             WHERE numero_ficha = :numeroFicha AND id_sistema = :idSistema
            """;

    @Autowired
    private DataSource dataSource;

    public FichasTemplate inserir(FichasTemplate ficha) {
        Objects.requireNonNull(ficha, "ficha");
        FichasTemplatePK pk = ficha.getId();
        Objects.requireNonNull(pk, "ficha.id");

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (pk.getNumeroFicha() == null || pk.getNumeroFicha() == 0L) {
                    pk.setNumeroFicha(proximoNumero(conn, pk.getIdSistema()));
                }
                try (NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT)) {
                    ps.setParam("numeroFicha", pk.getNumeroFicha());
                    ps.setParam("idSistema", pk.getIdSistema());
                    ps.setParam("fichaArquivo", ficha.getFichaArquivo());
                    ps.setParam("tipo", ficha.getTipo());
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir FICHAS_TEMPLATE", e);
        }

        return buscarPorId(pk).orElseThrow(
                () -> new DaoException("FICHAS_TEMPLATE inserido nao foi encontrado: " + pk));
    }

    public Optional<FichasTemplate> buscarPorId(FichasTemplatePK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("numeroFicha", pk.getNumeroFicha());
            ps.setParam("idSistema", pk.getIdSistema());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar FICHAS_TEMPLATE " + pk, e);
        }
    }

    public List<FichasTemplate> listarPorSistema(Long idSistema) {
        Objects.requireNonNull(idSistema, "idSistema");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_POR_SISTEMA)) {

            ps.setParam("idSistema", idSistema);
            try (ResultSet rs = ps.executeQuery()) {
                List<FichasTemplate> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar FICHAS_TEMPLATE de SISTEMA " + idSistema, e);
        }
    }

    public List<FichasTemplate> listarTodos() {
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<FichasTemplate> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar FICHAS_TEMPLATE", e);
        }
    }

    public boolean atualizar(FichasTemplate ficha) {
        Objects.requireNonNull(ficha, "ficha");
        Objects.requireNonNull(ficha.getId(), "ficha.id");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("fichaArquivo", ficha.getFichaArquivo());
            ps.setParam("tipo", ficha.getTipo());
            ps.setParam("numeroFicha", ficha.getId().getNumeroFicha());
            ps.setParam("idSistema", ficha.getId().getIdSistema());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar FICHAS_TEMPLATE " + ficha.getId(), e);
        }
    }

    public boolean deletar(FichasTemplatePK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("numeroFicha", pk.getNumeroFicha());
            ps.setParam("idSistema", pk.getIdSistema());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar FICHAS_TEMPLATE " + pk, e);
        }
    }

    private long proximoNumero(Connection conn, Long idSistema) throws SQLException {
        try (NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, NEXT_NUMERO)) {
            ps.setParam("idSistema", idSistema);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    static FichasTemplate mapear(ResultSet rs) throws SQLException {
        FichasTemplate ficha = new FichasTemplate();
        long idSistema = rs.getLong("id_sistema");
        ficha.setId(new FichasTemplatePK(rs.getLong("numero_ficha"), idSistema));
        ficha.setSistema(Sistema.ref(idSistema));
        ficha.setFichaArquivo(rs.getBytes("ficha_arquivo"));
        ficha.setTipo(TipoFicha.valueOf(rs.getString("tipo")));
        return ficha;
    }
}
