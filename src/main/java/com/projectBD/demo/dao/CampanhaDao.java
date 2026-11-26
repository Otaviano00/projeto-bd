package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Campanha;
import com.projectBD.demo.entity.CampanhaCenario;
import com.projectBD.demo.entity.CampanhaCenario.CampanhaCenarioPK;
import com.projectBD.demo.entity.CampanhaNpc;
import com.projectBD.demo.entity.CampanhaNpc.CampanhaNpcPK;
import com.projectBD.demo.entity.Cenario;
import com.projectBD.demo.entity.NPC;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.Usuario;
import com.projectBD.demo.entity.enums.StatusCampanha;
import com.projectBD.demo.entity.enums.StatusCampanhaNPC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;

@Repository
public class CampanhaDao {

    private static final String INSERT = """
            INSERT INTO CAMPANHA (id_mestre, id_sistema, nome, descricao, historia, status,
                finished_at)
            VALUES (:idMestre, :idSistema, :nome, :descricao, :historia, :status,
                   :finishedAt)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_campanha, id_mestre, id_sistema, nome, descricao, historia, status, created_at,
                   finished_at
              FROM CAMPANHA
             WHERE id_campanha = :idCampanha
            """;

    private static final String SELECT_ALL = """
            SELECT id_campanha, id_mestre, id_sistema, nome, descricao, historia, status, created_at,
                   finished_at
              FROM CAMPANHA
             ORDER BY id_campanha
            """;

    private static final String UPDATE = """
            UPDATE CAMPANHA
               SET id_mestre = :idMestre, id_sistema = :idSistema, nome = :nome,
                   descricao = :descricao, historia = :historia, status = :status,
                   finished_at = :finishedAt
             WHERE id_campanha = :idCampanha
            """;

    private static final String DELETE = """
            DELETE FROM CAMPANHA
             WHERE id_campanha = :idCampanha
            """;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CampanhaNpcDao campanhaNpcDao;

    @Autowired
    private CampanhaCenarioDao campanhaCenarioDao;

    public Campanha inserir(Campanha campanha) {
        Objects.requireNonNull(campanha, "campanha");

        long id;
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT, "id_campanha")) {

            ps.setParam("idMestre", campanha.getMestre().getIdUsuario());
            ps.setParam("idSistema", campanha.getSistema().getIdSistema());
            ps.setParam("nome", campanha.getNome());
            ps.setParam("descricao", campanha.getDescricao());
            ps.setParam("historia", campanha.getHistoria());
            ps.setParam("status", campanha.getStatus());
            ps.setParam("finishedAt", campanha.getFinishedAt());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new DaoException("O banco nao retornou o id gerado de CAMPANHA");
                }
                id = chaves.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir CAMPANHA", e);
        }

        return buscarPorId(id).orElseThrow(
                () -> new DaoException("CAMPANHA inserido nao foi encontrado: id=" + id));
    }

    public Optional<Campanha> buscarPorId(Long idCampanha) {
        Objects.requireNonNull(idCampanha, "idCampanha");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idCampanha", idCampanha);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar CAMPANHA id=" + idCampanha, e);
        }
    }

    public List<Campanha> listarTodos() {
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Campanha> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar CAMPANHA", e);
        }
    }

    public boolean atualizar(Campanha campanha) {
        Objects.requireNonNull(campanha, "campanha");
        Objects.requireNonNull(campanha.getIdCampanha(), "campanha.idCampanha");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("idMestre", campanha.getMestre().getIdUsuario());
            ps.setParam("idSistema", campanha.getSistema().getIdSistema());
            ps.setParam("nome", campanha.getNome());
            ps.setParam("descricao", campanha.getDescricao());
            ps.setParam("historia", campanha.getHistoria());
            ps.setParam("status", campanha.getStatus());
            ps.setParam("finishedAt", campanha.getFinishedAt());
            ps.setParam("idCampanha", campanha.getIdCampanha());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar CAMPANHA id=" + campanha.getIdCampanha(), e);
        }
    }

    public boolean deletar(Long idCampanha) {
        Objects.requireNonNull(idCampanha, "idCampanha");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idCampanha", idCampanha);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar CAMPANHA id=" + idCampanha, e);
        }
    }

    public List<NPC> buscarNPCs(Long idCampanha) {
        Objects.requireNonNull(idCampanha, "idCampanha");
        return campanhaNpcDao.listarNPCsPorCampanha(idCampanha);
    }

    public CampanhaNpc associarNPC(Long idCampanha, Long idNpc, StatusCampanhaNPC status) {
        Objects.requireNonNull(idCampanha, "idCampanha");
        Objects.requireNonNull(idNpc, "idNpc");

        CampanhaNpc vinculo = new CampanhaNpc();
        vinculo.setId(new CampanhaNpcPK(idCampanha, idNpc));
        if (status != null) {
            vinculo.setStatus(status);
        }
        return campanhaNpcDao.inserir(vinculo);
    }

    public boolean desassociarNPC(Long idCampanha, Long idNpc) {
        Objects.requireNonNull(idCampanha, "idCampanha");
        Objects.requireNonNull(idNpc, "idNpc");
        return campanhaNpcDao.deletar(new CampanhaNpcPK(idCampanha, idNpc));
    }

    public List<Cenario> buscarCenarios(Long idCampanha) {
        Objects.requireNonNull(idCampanha, "idCampanha");
        return campanhaCenarioDao.listarCenariosPorCampanha(idCampanha);
    }

    public CampanhaCenario associarCenario(Long idCampanha, Long idCenario) {
        Objects.requireNonNull(idCampanha, "idCampanha");
        Objects.requireNonNull(idCenario, "idCenario");

        CampanhaCenario vinculo = new CampanhaCenario();
        vinculo.setId(new CampanhaCenarioPK(idCampanha, idCenario));
        return campanhaCenarioDao.inserir(vinculo);
    }

    public boolean desassociarCenario(Long idCampanha, Long idCenario) {
        Objects.requireNonNull(idCampanha, "idCampanha");
        Objects.requireNonNull(idCenario, "idCenario");
        return campanhaCenarioDao.deletar(new CampanhaCenarioPK(idCampanha, idCenario));
    }

    static Campanha mapear(ResultSet rs) throws SQLException {
        Campanha campanha = new Campanha();
        campanha.setIdCampanha(rs.getLong("id_campanha"));
        campanha.setMestre(Usuario.ref(rs.getLong("id_mestre")));
        campanha.setSistema(Sistema.ref(rs.getLong("id_sistema")));
        campanha.setNome(rs.getString("nome"));
        campanha.setDescricao(rs.getString("descricao"));
        campanha.setHistoria(rs.getString("historia"));
        campanha.setStatus(StatusCampanha.valueOf(rs.getString("status")));
        campanha.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        campanha.setFinishedAt(rs.getObject("finished_at", LocalDateTime.class));
        return campanha;
    }
}
