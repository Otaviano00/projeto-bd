package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Campanha;
import com.projectBD.demo.entity.CampanhaNpc;
import com.projectBD.demo.entity.CampanhaNpc.CampanhaNpcPK;
import com.projectBD.demo.entity.NPC;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaNpc;
import com.projectBD.demo.entity.SistemaNpc.SistemaNpcPK;
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
public class NPCDao {

    private static final String INSERT = """
            INSERT INTO NPC (nome, descricao, historia, imagem)
            VALUES (:nome, :descricao, :historia, :imagem)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_npc, nome, descricao, historia, imagem
              FROM NPC
             WHERE id_npc = :idNpc
            """;

    private static final String SELECT_ALL = """
            SELECT id_npc, nome, descricao, historia, imagem
              FROM NPC
             ORDER BY id_npc
            """;

    private static final String UPDATE = """
            UPDATE NPC
               SET nome = :nome, descricao = :descricao, historia = :historia,
                   imagem = :imagem
             WHERE id_npc = :idNpc
            """;

    private static final String DELETE = """
            DELETE FROM NPC
             WHERE id_npc = :idNpc
            """;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CampanhaNpcDao campanhaNpcDao;

    @Autowired
    private SistemaNpcDao sistemaNpcDao;

    public NPC inserir(NPC npc) {
        Objects.requireNonNull(npc, "npc");

        long id;
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT, "id_npc")) {

            ps.setParam("nome", npc.getNome());
            ps.setParam("descricao", npc.getDescricao());
            ps.setParam("historia", npc.getHistoria());
            ps.setParam("imagem", npc.getImagem());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new DaoException("O banco nao retornou o id gerado de NPC");
                }
                id = chaves.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir NPC", e);
        }

        return buscarPorId(id).orElseThrow(
                () -> new DaoException("NPC inserido nao foi encontrado: id=" + id));
    }

    public Optional<NPC> buscarPorId(Long idNpc) {
        Objects.requireNonNull(idNpc, "idNpc");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idNpc", idNpc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar NPC id=" + idNpc, e);
        }
    }

    public List<NPC> listarTodos() {
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<NPC> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar NPC", e);
        }
    }

    public boolean atualizar(NPC npc) {
        Objects.requireNonNull(npc, "npc");
        Objects.requireNonNull(npc.getIdNpc(), "npc.idNpc");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("nome", npc.getNome());
            ps.setParam("descricao", npc.getDescricao());
            ps.setParam("historia", npc.getHistoria());
            ps.setParam("imagem", npc.getImagem());
            ps.setParam("idNpc", npc.getIdNpc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar NPC id=" + npc.getIdNpc(), e);
        }
    }

    public boolean deletar(Long idNpc) {
        Objects.requireNonNull(idNpc, "idNpc");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idNpc", idNpc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar NPC id=" + idNpc, e);
        }
    }

    public List<Campanha> buscarCampanhas(Long idNpc) {
        Objects.requireNonNull(idNpc, "idNpc");
        return campanhaNpcDao.listarCampanhasPorNPC(idNpc);
    }

    public CampanhaNpc associarCampanha(Long idNpc, Long idCampanha, StatusCampanhaNPC status) {
        Objects.requireNonNull(idNpc, "idNpc");
        Objects.requireNonNull(idCampanha, "idCampanha");

        CampanhaNpc vinculo = new CampanhaNpc();
        vinculo.setId(new CampanhaNpcPK(idCampanha, idNpc));
        if (status != null) {
            vinculo.setStatus(status);
        }
        return campanhaNpcDao.inserir(vinculo);
    }

    public boolean desassociarCampanha(Long idNpc, Long idCampanha) {
        Objects.requireNonNull(idNpc, "idNpc");
        Objects.requireNonNull(idCampanha, "idCampanha");
        return campanhaNpcDao.deletar(new CampanhaNpcPK(idCampanha, idNpc));
    }

    public List<Sistema> buscarSistemas(Long idNpc) {
        Objects.requireNonNull(idNpc, "idNpc");
        return sistemaNpcDao.listarSistemasPorNPC(idNpc);
    }

    public SistemaNpc associarSistema(Long idNpc, Long idSistema, byte[] fichaArquivo) {
        Objects.requireNonNull(idNpc, "idNpc");
        Objects.requireNonNull(idSistema, "idSistema");

        SistemaNpc vinculo = new SistemaNpc();
        vinculo.setId(new SistemaNpcPK(idSistema, idNpc));
        if (fichaArquivo != null) {
            vinculo.setFichaArquivo(fichaArquivo);
        }
        return sistemaNpcDao.inserir(vinculo);
    }

    public boolean desassociarSistema(Long idNpc, Long idSistema) {
        Objects.requireNonNull(idNpc, "idNpc");
        Objects.requireNonNull(idSistema, "idSistema");
        return sistemaNpcDao.deletar(new SistemaNpcPK(idSistema, idNpc));
    }

    static NPC mapear(ResultSet rs) throws SQLException {
        NPC npc = new NPC();
        npc.setIdNpc(rs.getLong("id_npc"));
        npc.setNome(rs.getString("nome"));
        npc.setDescricao(rs.getString("descricao"));
        npc.setHistoria(rs.getString("historia"));
        npc.setImagem(rs.getBytes("imagem"));
        return npc;
    }
}
