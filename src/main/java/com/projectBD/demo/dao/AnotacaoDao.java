package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Anotacao;
import com.projectBD.demo.entity.Anotacao.AnotacaoPK;
import com.projectBD.demo.entity.Usuario;
import com.projectBD.demo.entity.enums.Visibilidade;
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
public class AnotacaoDao {

    private static final String INSERT = """
            INSERT INTO ANOTACAO (id_usuario, numero_anotacao, titulo, conteudo, visibilidade)
            VALUES (:idUsuario, :numeroAnotacao, :titulo, :conteudo, :visibilidade)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_usuario, numero_anotacao, titulo, conteudo, visibilidade, created_at,
                   updated_at
              FROM ANOTACAO
             WHERE id_usuario = :idUsuario AND numero_anotacao = :numeroAnotacao
            """;

    private static final String SELECT_ALL = """
            SELECT id_usuario, numero_anotacao, titulo, conteudo, visibilidade, created_at,
                   updated_at
              FROM ANOTACAO
             ORDER BY id_usuario, numero_anotacao
            """;

    private static final String SELECT_POR_USUARIO = """
            SELECT id_usuario, numero_anotacao, titulo, conteudo, visibilidade, created_at,
                   updated_at
              FROM ANOTACAO
             WHERE id_usuario = :idUsuario
             ORDER BY numero_anotacao
            """;

    private static final String NEXT_NUMERO = """
            SELECT COALESCE(MAX(numero_anotacao), 0) + 1
              FROM ANOTACAO
             WHERE id_usuario = :idUsuario
            """;

    private static final String UPDATE = """
            UPDATE ANOTACAO
               SET titulo = :titulo, conteudo = :conteudo, visibilidade = :visibilidade,
                   updated_at = CURRENT_TIMESTAMP
             WHERE id_usuario = :idUsuario AND numero_anotacao = :numeroAnotacao
            """;

    private static final String DELETE = """
            DELETE FROM ANOTACAO
             WHERE id_usuario = :idUsuario AND numero_anotacao = :numeroAnotacao
            """;

    @Autowired
    private DataSource dataSource;

    public Anotacao inserir(Anotacao anotacao) {
        Objects.requireNonNull(anotacao, "anotacao");
        AnotacaoPK pk = anotacao.getId();
        Objects.requireNonNull(pk, "anotacao.id");

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (pk.getNumeroAnotacao() == null || pk.getNumeroAnotacao() == 0L) {
                    pk.setNumeroAnotacao(proximoNumero(conn, pk.getIdUsuario()));
                }
                try (NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT)) {
                    ps.setParam("idUsuario", pk.getIdUsuario());
                    ps.setParam("numeroAnotacao", pk.getNumeroAnotacao());
                    ps.setParam("titulo", anotacao.getTitulo());
                    ps.setParam("conteudo", anotacao.getConteudo());
                    ps.setParam("visibilidade", anotacao.getVisibilidade());
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir ANOTACAO", e);
        }

        return buscarPorId(pk).orElseThrow(
                () -> new DaoException("ANOTACAO inserido nao foi encontrado: " + pk));
    }

    public Optional<Anotacao> buscarPorId(AnotacaoPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idUsuario", pk.getIdUsuario());
            ps.setParam("numeroAnotacao", pk.getNumeroAnotacao());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar ANOTACAO " + pk, e);
        }
    }

    public List<Anotacao> listarPorUsuario(Long idUsuario) {
        Objects.requireNonNull(idUsuario, "idUsuario");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_POR_USUARIO)) {

            ps.setParam("idUsuario", idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                List<Anotacao> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar ANOTACAO de USUARIO " + idUsuario, e);
        }
    }

    public List<Anotacao> listarTodos() {
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Anotacao> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar ANOTACAO", e);
        }
    }

    public boolean atualizar(Anotacao anotacao) {
        Objects.requireNonNull(anotacao, "anotacao");
        Objects.requireNonNull(anotacao.getId(), "anotacao.id");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("titulo", anotacao.getTitulo());
            ps.setParam("conteudo", anotacao.getConteudo());
            ps.setParam("visibilidade", anotacao.getVisibilidade());
            ps.setParam("idUsuario", anotacao.getId().getIdUsuario());
            ps.setParam("numeroAnotacao", anotacao.getId().getNumeroAnotacao());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar ANOTACAO " + anotacao.getId(), e);
        }
    }

    public boolean deletar(AnotacaoPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idUsuario", pk.getIdUsuario());
            ps.setParam("numeroAnotacao", pk.getNumeroAnotacao());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar ANOTACAO " + pk, e);
        }
    }

    private long proximoNumero(Connection conn, Long idUsuario) throws SQLException {
        try (NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, NEXT_NUMERO)) {
            ps.setParam("idUsuario", idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    static Anotacao mapear(ResultSet rs) throws SQLException {
        Anotacao anotacao = new Anotacao();
        long idUsuario = rs.getLong("id_usuario");
        anotacao.setId(new AnotacaoPK(idUsuario, rs.getLong("numero_anotacao")));
        anotacao.setUsuario(Usuario.ref(idUsuario));
        anotacao.setTitulo(rs.getString("titulo"));
        anotacao.setConteudo(rs.getString("conteudo"));
        anotacao.setVisibilidade(Visibilidade.valueOf(rs.getString("visibilidade")));
        anotacao.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        anotacao.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        return anotacao;
    }
}
