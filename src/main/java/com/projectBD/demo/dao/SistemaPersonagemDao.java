package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaPersonagem;
import com.projectBD.demo.entity.SistemaPersonagem.SistemaPersonagemPK;
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
class SistemaPersonagemDao {

    private static final String INSERT = """
            INSERT INTO SISTEMA_PERSONAGEM (id_sistema, id_personagem, ficha_arquivo)
            VALUES (:idSistema, :idPersonagem, :fichaArquivo)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_sistema, id_personagem, ficha_arquivo
              FROM SISTEMA_PERSONAGEM
             WHERE id_sistema = :idSistema AND id_personagem = :idPersonagem
            """;

    private static final String SELECT_PERSONAGEM_POR_SISTEMA = """
            SELECT t.id_personagem, t.id_usuario, t.nome, t.status, t.imagem, t.created_at
              FROM SISTEMA_PERSONAGEM r
              JOIN PERSONAGEM t ON t.id_personagem = r.id_personagem
             WHERE r.id_sistema = :idSistema
             ORDER BY t.id_personagem
            """;

    private static final String SELECT_SISTEMA_POR_PERSONAGEM = """
            SELECT t.id_sistema, t.nome, t.editora, t.edicao, t.livro_regras
              FROM SISTEMA_PERSONAGEM r
              JOIN SISTEMA t ON t.id_sistema = r.id_sistema
             WHERE r.id_personagem = :idPersonagem
             ORDER BY t.id_sistema
            """;

    private static final String UPDATE = """
            UPDATE SISTEMA_PERSONAGEM
               SET ficha_arquivo = :fichaArquivo
             WHERE id_sistema = :idSistema AND id_personagem = :idPersonagem
            """;

    private static final String DELETE = """
            DELETE FROM SISTEMA_PERSONAGEM
             WHERE id_sistema = :idSistema AND id_personagem = :idPersonagem
            """;

    @Autowired
    private DataSource dataSource;

    SistemaPersonagem inserir(SistemaPersonagem vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        SistemaPersonagemPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT)) {

            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idPersonagem", pk.getIdPersonagem());
            ps.setParam("fichaArquivo", vinculo.getFichaArquivo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir SISTEMA_PERSONAGEM", e);
        }

        return buscarPorId(pk).orElseThrow(
                () -> new DaoException("SISTEMA_PERSONAGEM inserido nao foi encontrado: " + pk));
    }

    Optional<SistemaPersonagem> buscarPorId(SistemaPersonagemPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idPersonagem", pk.getIdPersonagem());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar SISTEMA_PERSONAGEM " + pk, e);
        }
    }

    List<Personagem> listarPersonagensPorSistema(Long idSistema) {
        Objects.requireNonNull(idSistema, "idSistema");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_PERSONAGEM_POR_SISTEMA)) {

            ps.setParam("idSistema", idSistema);
            try (ResultSet rs = ps.executeQuery()) {
                List<Personagem> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(PersonagemDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar PERSONAGEM de SISTEMA id= " + idSistema, e);
        }
    }

    List<Sistema> listarSistemasPorPersonagem(Long idPersonagem) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_SISTEMA_POR_PERSONAGEM)) {

            ps.setParam("idPersonagem", idPersonagem);
            try (ResultSet rs = ps.executeQuery()) {
                List<Sistema> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(SistemaDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar SISTEMA de PERSONAGEM id= " + idPersonagem, e);
        }
    }

    boolean atualizar(SistemaPersonagem vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        SistemaPersonagemPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("fichaArquivo", vinculo.getFichaArquivo());
            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idPersonagem", pk.getIdPersonagem());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar SISTEMA_PERSONAGEM " + pk, e);
        }
    }

    boolean deletar(SistemaPersonagemPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idPersonagem", pk.getIdPersonagem());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar SISTEMA_PERSONAGEM " + pk, e);
        }
    }

    private SistemaPersonagem mapear(ResultSet rs) throws SQLException {
        SistemaPersonagem vinculo = new SistemaPersonagem();
        long idSistema = rs.getLong("id_sistema");
        long idPersonagem = rs.getLong("id_personagem");
        vinculo.setId(new SistemaPersonagemPK(idSistema, idPersonagem));
        vinculo.setSistema(Sistema.ref(idSistema));
        vinculo.setPersonagem(Personagem.ref(idPersonagem));
        vinculo.setFichaArquivo(rs.getBytes("ficha_arquivo"));
        return vinculo;
    }
}
