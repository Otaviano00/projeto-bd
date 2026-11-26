package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Campanha;
import com.projectBD.demo.entity.CampanhaCenario;
import com.projectBD.demo.entity.CampanhaCenario.CampanhaCenarioPK;
import com.projectBD.demo.entity.Cenario;
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
class CampanhaCenarioDao {

    private static final String INSERT = """
            INSERT INTO CAMPANHA_CENARIO (id_campanha, id_cenario)
            VALUES (:idCampanha, :idCenario)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_campanha, id_cenario
              FROM CAMPANHA_CENARIO
             WHERE id_campanha = :idCampanha AND id_cenario = :idCenario
            """;

    private static final String SELECT_CENARIO_POR_CAMPANHA = """
            SELECT t.id_cenario, t.nome, t.descricao, t.imagem
              FROM CAMPANHA_CENARIO r
              JOIN CENARIO t ON t.id_cenario = r.id_cenario
             WHERE r.id_campanha = :idCampanha
             ORDER BY t.id_cenario
            """;

    private static final String SELECT_CAMPANHA_POR_CENARIO = """
            SELECT t.id_campanha, t.id_mestre, t.id_sistema, t.nome, t.descricao, t.historia,
                   t.status, t.created_at, t.finished_at
              FROM CAMPANHA_CENARIO r
              JOIN CAMPANHA t ON t.id_campanha = r.id_campanha
             WHERE r.id_cenario = :idCenario
             ORDER BY t.id_campanha
            """;

    private static final String DELETE = """
            DELETE FROM CAMPANHA_CENARIO
             WHERE id_campanha = :idCampanha AND id_cenario = :idCenario
            """;

    @Autowired
    private DataSource dataSource;

    CampanhaCenario inserir(CampanhaCenario vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        CampanhaCenarioPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT)) {

            ps.setParam("idCampanha", pk.getIdCampanha());
            ps.setParam("idCenario", pk.getIdCenario());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir CAMPANHA_CENARIO", e);
        }

        return buscarPorId(pk).orElseThrow(
                () -> new DaoException("CAMPANHA_CENARIO inserido nao foi encontrado: " + pk));
    }

    Optional<CampanhaCenario> buscarPorId(CampanhaCenarioPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idCampanha", pk.getIdCampanha());
            ps.setParam("idCenario", pk.getIdCenario());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar CAMPANHA_CENARIO " + pk, e);
        }
    }

    List<Cenario> listarCenariosPorCampanha(Long idCampanha) {
        Objects.requireNonNull(idCampanha, "idCampanha");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_CENARIO_POR_CAMPANHA)) {

            ps.setParam("idCampanha", idCampanha);
            try (ResultSet rs = ps.executeQuery()) {
                List<Cenario> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(CenarioDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar CENARIO de CAMPANHA id= " + idCampanha, e);
        }
    }

    List<Campanha> listarCampanhasPorCenario(Long idCenario) {
        Objects.requireNonNull(idCenario, "idCenario");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_CAMPANHA_POR_CENARIO)) {

            ps.setParam("idCenario", idCenario);
            try (ResultSet rs = ps.executeQuery()) {
                List<Campanha> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(CampanhaDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar CAMPANHA de CENARIO id= " + idCenario, e);
        }
    }

    boolean deletar(CampanhaCenarioPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idCampanha", pk.getIdCampanha());
            ps.setParam("idCenario", pk.getIdCenario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar CAMPANHA_CENARIO " + pk, e);
        }
    }

    private CampanhaCenario mapear(ResultSet rs) throws SQLException {
        CampanhaCenario vinculo = new CampanhaCenario();
        long idCampanha = rs.getLong("id_campanha");
        long idCenario = rs.getLong("id_cenario");
        vinculo.setId(new CampanhaCenarioPK(idCampanha, idCenario));
        vinculo.setCampanha(Campanha.ref(idCampanha));
        vinculo.setCenario(Cenario.ref(idCenario));
        return vinculo;
    }
}
