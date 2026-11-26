package com.projectBD.demo.dao;

import com.projectBD.demo.entity.NPC;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaNpc;
import com.projectBD.demo.entity.SistemaNpc.SistemaNpcPK;
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
class SistemaNpcDao {

    private static final String INSERT = """
            INSERT INTO SISTEMA_NPC (id_sistema, id_npc, ficha_arquivo)
            VALUES (:idSistema, :idNpc, :fichaArquivo)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_sistema, id_npc, ficha_arquivo
              FROM SISTEMA_NPC
             WHERE id_sistema = :idSistema AND id_npc = :idNpc
            """;

    private static final String SELECT_NPC_POR_SISTEMA = """
            SELECT t.id_npc, t.nome, t.descricao, t.historia, t.imagem
              FROM SISTEMA_NPC r
              JOIN NPC t ON t.id_npc = r.id_npc
             WHERE r.id_sistema = :idSistema
             ORDER BY t.id_npc
            """;

    private static final String SELECT_SISTEMA_POR_NPC = """
            SELECT t.id_sistema, t.nome, t.editora, t.edicao, t.livro_regras
              FROM SISTEMA_NPC r
              JOIN SISTEMA t ON t.id_sistema = r.id_sistema
             WHERE r.id_npc = :idNpc
             ORDER BY t.id_sistema
            """;

    private static final String UPDATE = """
            UPDATE SISTEMA_NPC
               SET ficha_arquivo = :fichaArquivo
             WHERE id_sistema = :idSistema AND id_npc = :idNpc
            """;

    private static final String DELETE = """
            DELETE FROM SISTEMA_NPC
             WHERE id_sistema = :idSistema AND id_npc = :idNpc
            """;

    @Autowired
    private DataSource dataSource;

    SistemaNpc inserir(SistemaNpc vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        SistemaNpcPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT)) {

            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idNpc", pk.getIdNpc());
            ps.setParam("fichaArquivo", vinculo.getFichaArquivo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir SISTEMA_NPC", e);
        }

        return buscarPorId(pk).orElseThrow(
                () -> new DaoException("SISTEMA_NPC inserido nao foi encontrado: " + pk));
    }

    Optional<SistemaNpc> buscarPorId(SistemaNpcPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idNpc", pk.getIdNpc());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar SISTEMA_NPC " + pk, e);
        }
    }

    List<NPC> listarNPCsPorSistema(Long idSistema) {
        Objects.requireNonNull(idSistema, "idSistema");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_NPC_POR_SISTEMA)) {

            ps.setParam("idSistema", idSistema);
            try (ResultSet rs = ps.executeQuery()) {
                List<NPC> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(NPCDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar NPC de SISTEMA id= " + idSistema, e);
        }
    }

    List<Sistema> listarSistemasPorNPC(Long idNpc) {
        Objects.requireNonNull(idNpc, "idNpc");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_SISTEMA_POR_NPC)) {

            ps.setParam("idNpc", idNpc);
            try (ResultSet rs = ps.executeQuery()) {
                List<Sistema> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(SistemaDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar SISTEMA de NPC id= " + idNpc, e);
        }
    }

    boolean atualizar(SistemaNpc vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        SistemaNpcPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("fichaArquivo", vinculo.getFichaArquivo());
            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idNpc", pk.getIdNpc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar SISTEMA_NPC " + pk, e);
        }
    }

    boolean deletar(SistemaNpcPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idNpc", pk.getIdNpc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar SISTEMA_NPC " + pk, e);
        }
    }

    private SistemaNpc mapear(ResultSet rs) throws SQLException {
        SistemaNpc vinculo = new SistemaNpc();
        long idSistema = rs.getLong("id_sistema");
        long idNpc = rs.getLong("id_npc");
        vinculo.setId(new SistemaNpcPK(idSistema, idNpc));
        vinculo.setSistema(Sistema.ref(idSistema));
        vinculo.setNpc(NPC.ref(idNpc));
        vinculo.setFichaArquivo(rs.getBytes("ficha_arquivo"));
        return vinculo;
    }
}
