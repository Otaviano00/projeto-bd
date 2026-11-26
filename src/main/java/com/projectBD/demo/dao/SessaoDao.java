package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Campanha;
import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.PersonagemSessao;
import com.projectBD.demo.entity.PersonagemSessao.PersonagemSessaoPK;
import com.projectBD.demo.entity.Sessao;
import com.projectBD.demo.entity.Sessao.SessaoPK;
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
public class SessaoDao {

    private static final String INSERT = """
            INSERT INTO SESSAO (id_campanha, numero_sessao, data_hora, resumo_narrativo, finished_at)
            VALUES (:idCampanha, :numeroSessao, :dataHora, :resumoNarrativo, :finishedAt)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_campanha, numero_sessao, data_hora, resumo_narrativo, created_at, finished_at
              FROM SESSAO
             WHERE id_campanha = :idCampanha AND numero_sessao = :numeroSessao
            """;

    private static final String SELECT_ALL = """
            SELECT id_campanha, numero_sessao, data_hora, resumo_narrativo, created_at, finished_at
              FROM SESSAO
             ORDER BY id_campanha, numero_sessao
            """;

    private static final String SELECT_POR_CAMPANHA = """
            SELECT id_campanha, numero_sessao, data_hora, resumo_narrativo, created_at, finished_at
              FROM SESSAO
             WHERE id_campanha = :idCampanha
             ORDER BY numero_sessao
            """;

    private static final String NEXT_NUMERO = """
            SELECT COALESCE(MAX(numero_sessao), 0) + 1
              FROM SESSAO
             WHERE id_campanha = :idCampanha
            """;

    private static final String UPDATE = """
            UPDATE SESSAO
               SET data_hora = :dataHora, resumo_narrativo = :resumoNarrativo,
                   finished_at = :finishedAt
             WHERE id_campanha = :idCampanha AND numero_sessao = :numeroSessao
            """;

    private static final String DELETE = """
            DELETE FROM SESSAO
             WHERE id_campanha = :idCampanha AND numero_sessao = :numeroSessao
            """;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PersonagemSessaoDao personagemSessaoDao;

    public Sessao inserir(Sessao sessao) {
        Objects.requireNonNull(sessao, "sessao");
        SessaoPK pk = sessao.getId();
        Objects.requireNonNull(pk, "sessao.id");

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (pk.getNumeroSessao() == null || pk.getNumeroSessao() == 0L) {
                    pk.setNumeroSessao(proximoNumero(conn, pk.getIdCampanha()));
                }
                try (NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT)) {
                    ps.setParam("idCampanha", pk.getIdCampanha());
                    ps.setParam("numeroSessao", pk.getNumeroSessao());
                    ps.setParam("dataHora", sessao.getDataHora());
                    ps.setParam("resumoNarrativo", sessao.getResumoNarrativo());
                    ps.setParam("finishedAt", sessao.getFinishedAt());
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir SESSAO", e);
        }

        return buscarPorId(pk).orElseThrow(
                () -> new DaoException("SESSAO inserido nao foi encontrado: " + pk));
    }

    public Optional<Sessao> buscarPorId(SessaoPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idCampanha", pk.getIdCampanha());
            ps.setParam("numeroSessao", pk.getNumeroSessao());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar SESSAO " + pk, e);
        }
    }

    public List<Sessao> listarPorCampanha(Long idCampanha) {
        Objects.requireNonNull(idCampanha, "idCampanha");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_POR_CAMPANHA)) {

            ps.setParam("idCampanha", idCampanha);
            try (ResultSet rs = ps.executeQuery()) {
                List<Sessao> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar SESSAO de CAMPANHA " + idCampanha, e);
        }
    }

    public List<Sessao> listarTodos() {
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Sessao> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar SESSAO", e);
        }
    }

    public boolean atualizar(Sessao sessao) {
        Objects.requireNonNull(sessao, "sessao");
        Objects.requireNonNull(sessao.getId(), "sessao.id");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("dataHora", sessao.getDataHora());
            ps.setParam("resumoNarrativo", sessao.getResumoNarrativo());
            ps.setParam("finishedAt", sessao.getFinishedAt());
            ps.setParam("idCampanha", sessao.getId().getIdCampanha());
            ps.setParam("numeroSessao", sessao.getId().getNumeroSessao());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar SESSAO " + sessao.getId(), e);
        }
    }

    public boolean deletar(SessaoPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idCampanha", pk.getIdCampanha());
            ps.setParam("numeroSessao", pk.getNumeroSessao());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar SESSAO " + pk, e);
        }
    }

    private long proximoNumero(Connection conn, Long idCampanha) throws SQLException {
        try (NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, NEXT_NUMERO)) {
            ps.setParam("idCampanha", idCampanha);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    public List<Personagem> buscarPersonagens(SessaoPK pk) {
        Objects.requireNonNull(pk, "pk");
        return personagemSessaoDao.listarPersonagensPorSessao(pk.getIdCampanha(), pk.getNumeroSessao());
    }

    public PersonagemSessao associarPersonagem(SessaoPK pk, Long idPersonagem) {
        Objects.requireNonNull(pk, "pk");
        Objects.requireNonNull(idPersonagem, "idPersonagem");

        PersonagemSessao vinculo = new PersonagemSessao();
        vinculo.setId(new PersonagemSessaoPK(idPersonagem, pk.getIdCampanha(), pk.getNumeroSessao()));
        return personagemSessaoDao.inserir(vinculo);
    }

    public boolean desassociarPersonagem(SessaoPK pk, Long idPersonagem) {
        Objects.requireNonNull(pk, "pk");
        Objects.requireNonNull(idPersonagem, "idPersonagem");
        return personagemSessaoDao.deletar(
                new PersonagemSessaoPK(idPersonagem, pk.getIdCampanha(), pk.getNumeroSessao()));
    }

    static Sessao mapear(ResultSet rs) throws SQLException {
        Sessao sessao = new Sessao();
        long idCampanha = rs.getLong("id_campanha");
        sessao.setId(new SessaoPK(idCampanha, rs.getLong("numero_sessao")));
        sessao.setCampanha(Campanha.ref(idCampanha));
        sessao.setDataHora(rs.getObject("data_hora", LocalDateTime.class));
        sessao.setResumoNarrativo(rs.getString("resumo_narrativo"));
        sessao.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        sessao.setFinishedAt(rs.getObject("finished_at", LocalDateTime.class));
        return sessao;
    }
}
