package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.PersonagemSessao;
import com.projectBD.demo.entity.PersonagemSessao.PersonagemSessaoPK;
import com.projectBD.demo.entity.Sessao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
class PersonagemSessaoDao {

    private static final String INSERT = """
            INSERT INTO PERSONAGEM_SESSAO (id_personagem, id_campanha, numero_sessao)
            VALUES (:idPersonagem, :idCampanha, :numeroSessao)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_personagem, id_campanha, numero_sessao
              FROM PERSONAGEM_SESSAO
             WHERE id_personagem = :idPersonagem AND id_campanha = :idCampanha
               AND numero_sessao = :numeroSessao
            """;

    private static final String SELECT_PERSONAGENS_POR_SESSAO = """
            SELECT p.id_personagem, p.id_usuario, p.nome, p.status, p.imagem, p.created_at
              FROM PERSONAGEM_SESSAO r
              JOIN PERSONAGEM p ON p.id_personagem = r.id_personagem
             WHERE r.id_campanha = :idCampanha AND r.numero_sessao = :numeroSessao
             ORDER BY p.id_personagem
            """;

    private static final String SELECT_SESSOES_POR_PERSONAGEM = """
            SELECT s.id_campanha, s.numero_sessao, s.data_hora, s.resumo_narrativo, s.created_at,
                   s.finished_at
              FROM PERSONAGEM_SESSAO r
              JOIN SESSAO s ON s.id_campanha = r.id_campanha AND s.numero_sessao = r.numero_sessao
             WHERE r.id_personagem = :idPersonagem
             ORDER BY s.id_campanha, s.numero_sessao
            """;

    private static final String DELETE = """
            DELETE FROM PERSONAGEM_SESSAO
             WHERE id_personagem = :idPersonagem AND id_campanha = :idCampanha
               AND numero_sessao = :numeroSessao
            """;

    @Autowired
    private DataSource dataSource;

    PersonagemSessao inserir(PersonagemSessao vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        PersonagemSessaoPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT)) {

            ps.setParam("idPersonagem", pk.getIdPersonagem());
            ps.setParam("idCampanha", pk.getIdCampanha());
            ps.setParam("numeroSessao", pk.getNumeroSessao());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir PERSONAGEM_SESSAO", e);
        }

        return buscarPorId(pk).orElseThrow(
                () -> new DaoException("PERSONAGEM_SESSAO inserido nao foi encontrado: " + pk));
    }

    Optional<PersonagemSessao> buscarPorId(PersonagemSessaoPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idPersonagem", pk.getIdPersonagem());
            ps.setParam("idCampanha", pk.getIdCampanha());
            ps.setParam("numeroSessao", pk.getNumeroSessao());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar PERSONAGEM_SESSAO " + pk, e);
        }
    }

    List<Personagem> listarPersonagensPorSessao(Long idCampanha, Long numeroSessao) {
        Objects.requireNonNull(idCampanha, "idCampanha");
        Objects.requireNonNull(numeroSessao, "numeroSessao");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_PERSONAGENS_POR_SESSAO)) {

            ps.setParam("idCampanha", idCampanha);
            ps.setParam("numeroSessao", numeroSessao);
            try (ResultSet rs = ps.executeQuery()) {
                List<Personagem> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(PersonagemDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar PERSONAGEM da SESSAO " + idCampanha + "/" + numeroSessao, e);
        }
    }

    List<Sessao> listarSessoesPorPersonagem(Long idPersonagem) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_SESSOES_POR_PERSONAGEM)) {

            ps.setParam("idPersonagem", idPersonagem);
            try (ResultSet rs = ps.executeQuery()) {
                List<Sessao> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(SessaoDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar SESSAO do PERSONAGEM id=" + idPersonagem, e);
        }
    }

    boolean deletar(PersonagemSessaoPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idPersonagem", pk.getIdPersonagem());
            ps.setParam("idCampanha", pk.getIdCampanha());
            ps.setParam("numeroSessao", pk.getNumeroSessao());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar PERSONAGEM_SESSAO " + pk, e);
        }
    }

    private PersonagemSessao mapear(ResultSet rs) throws SQLException {
        PersonagemSessao vinculo = new PersonagemSessao();
        long idPersonagem = rs.getLong("id_personagem");
        long idCampanha = rs.getLong("id_campanha");
        long numeroSessao = rs.getLong("numero_sessao");
        vinculo.setId(new PersonagemSessaoPK(idPersonagem, idCampanha, numeroSessao));
        vinculo.setPersonagem(Personagem.ref(idPersonagem));
        vinculo.setSessao(Sessao.ref(idCampanha, numeroSessao));
        return vinculo;
    }
}
