package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Campanha;
import com.projectBD.demo.entity.CampanhaNpc;
import com.projectBD.demo.entity.CampanhaNpc.CampanhaNpcPK;
import com.projectBD.demo.entity.NPC;
import com.projectBD.demo.entity.enums.StatusCampanhaNPC;
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
class CampanhaNpcDao {

    private static final String INSERT = """
            INSERT INTO CAMPANHA_NPC (id_campanha, id_npc, status)
            VALUES (:idCampanha, :idNpc, :status)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_campanha, id_npc, status
              FROM CAMPANHA_NPC
             WHERE id_campanha = :idCampanha AND id_npc = :idNpc
            """;

    private static final String SELECT_NPC_POR_CAMPANHA = """
            SELECT t.id_npc, t.nome, t.descricao, t.historia, t.imagem
              FROM CAMPANHA_NPC r
              JOIN NPC t ON t.id_npc = r.id_npc
             WHERE r.id_campanha = :idCampanha
             ORDER BY t.id_npc
            """;

    private static final String SELECT_CAMPANHA_POR_NPC = """
            SELECT t.id_campanha, t.id_mestre, t.id_sistema, t.nome, t.descricao, t.historia,
                   t.status, t.created_at, t.finished_at
              FROM CAMPANHA_NPC r
              JOIN CAMPANHA t ON t.id_campanha = r.id_campanha
             WHERE r.id_npc = :idNpc
             ORDER BY t.id_campanha
            """;

    private static final String UPDATE = """
            UPDATE CAMPANHA_NPC
               SET status = :status
             WHERE id_campanha = :idCampanha AND id_npc = :idNpc
            """;

    private static final String DELETE = """
            DELETE FROM CAMPANHA_NPC
             WHERE id_campanha = :idCampanha AND id_npc = :idNpc
            """;

    @Autowired
    private DataSource dataSource;

    CampanhaNpc inserir(CampanhaNpc vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        CampanhaNpcPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT)) {

            ps.setParam("idCampanha", pk.getIdCampanha());
            ps.setParam("idNpc", pk.getIdNpc());
            ps.setParam("status", vinculo.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir CAMPANHA_NPC", e);
        }

        return buscarPorId(pk).orElseThrow(
                () -> new DaoException("CAMPANHA_NPC inserido nao foi encontrado: " + pk));
    }

    Optional<CampanhaNpc> buscarPorId(CampanhaNpcPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idCampanha", pk.getIdCampanha());
            ps.setParam("idNpc", pk.getIdNpc());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar CAMPANHA_NPC " + pk, e);
        }
    }

    List<NPC> listarNPCsPorCampanha(Long idCampanha) {
        Objects.requireNonNull(idCampanha, "idCampanha");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_NPC_POR_CAMPANHA)) {

            ps.setParam("idCampanha", idCampanha);
            try (ResultSet rs = ps.executeQuery()) {
                List<NPC> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(NPCDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar NPC de CAMPANHA id= " + idCampanha, e);
        }
    }

    List<Campanha> listarCampanhasPorNPC(Long idNpc) {
        Objects.requireNonNull(idNpc, "idNpc");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_CAMPANHA_POR_NPC)) {

            ps.setParam("idNpc", idNpc);
            try (ResultSet rs = ps.executeQuery()) {
                List<Campanha> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(CampanhaDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar CAMPANHA de NPC id= " + idNpc, e);
        }
    }

    boolean atualizar(CampanhaNpc vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        CampanhaNpcPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("status", vinculo.getStatus());
            ps.setParam("idCampanha", pk.getIdCampanha());
            ps.setParam("idNpc", pk.getIdNpc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar CAMPANHA_NPC " + pk, e);
        }
    }

    boolean deletar(CampanhaNpcPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idCampanha", pk.getIdCampanha());
            ps.setParam("idNpc", pk.getIdNpc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar CAMPANHA_NPC " + pk, e);
        }
    }

    private CampanhaNpc mapear(ResultSet rs) throws SQLException {
        CampanhaNpc vinculo = new CampanhaNpc();
        long idCampanha = rs.getLong("id_campanha");
        long idNpc = rs.getLong("id_npc");
        vinculo.setId(new CampanhaNpcPK(idCampanha, idNpc));
        vinculo.setCampanha(Campanha.ref(idCampanha));
        vinculo.setNpc(NPC.ref(idNpc));
        vinculo.setStatus(StatusCampanhaNPC.valueOf(rs.getString("status")));
        return vinculo;
    }
}
