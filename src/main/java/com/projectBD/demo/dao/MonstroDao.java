package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Monstro;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaMonstro;
import com.projectBD.demo.entity.SistemaMonstro.SistemaMonstroPK;
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
public class MonstroDao {

    private static final String INSERT = """
            INSERT INTO MONSTRO (nome, descricao, historia, imagem)
            VALUES (:nome, :descricao, :historia, :imagem)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_monstro, nome, descricao, historia, imagem
              FROM MONSTRO
             WHERE id_monstro = :idMonstro
            """;

    private static final String SELECT_ALL = """
            SELECT id_monstro, nome, descricao, historia, imagem
              FROM MONSTRO
             ORDER BY id_monstro
            """;

    private static final String UPDATE = """
            UPDATE MONSTRO
               SET nome = :nome, descricao = :descricao, historia = :historia,
                   imagem = :imagem
             WHERE id_monstro = :idMonstro
            """;

    private static final String DELETE = """
            DELETE FROM MONSTRO
             WHERE id_monstro = :idMonstro
            """;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SistemaMonstroDao sistemaMonstroDao;

    public Monstro inserir(Monstro monstro) {
        Objects.requireNonNull(monstro, "monstro");

        long id;
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT, "id_monstro")) {

            ps.setParam("nome", monstro.getNome());
            ps.setParam("descricao", monstro.getDescricao());
            ps.setParam("historia", monstro.getHistoria());
            ps.setParam("imagem", monstro.getImagem());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new DaoException("O banco nao retornou o id gerado de MONSTRO");
                }
                id = chaves.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir MONSTRO", e);
        }

        return buscarPorId(id).orElseThrow(
                () -> new DaoException("MONSTRO inserido nao foi encontrado: id=" + id));
    }

    public Optional<Monstro> buscarPorId(Long idMonstro) {
        Objects.requireNonNull(idMonstro, "idMonstro");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idMonstro", idMonstro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar MONSTRO id=" + idMonstro, e);
        }
    }

    public List<Monstro> listarTodos() {
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Monstro> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar MONSTRO", e);
        }
    }

    public boolean atualizar(Monstro monstro) {
        Objects.requireNonNull(monstro, "monstro");
        Objects.requireNonNull(monstro.getIdMonstro(), "monstro.idMonstro");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("nome", monstro.getNome());
            ps.setParam("descricao", monstro.getDescricao());
            ps.setParam("historia", monstro.getHistoria());
            ps.setParam("imagem", monstro.getImagem());
            ps.setParam("idMonstro", monstro.getIdMonstro());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar MONSTRO id=" + monstro.getIdMonstro(), e);
        }
    }

    public boolean deletar(Long idMonstro) {
        Objects.requireNonNull(idMonstro, "idMonstro");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idMonstro", idMonstro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar MONSTRO id=" + idMonstro, e);
        }
    }

    public List<Sistema> buscarSistemas(Long idMonstro) {
        Objects.requireNonNull(idMonstro, "idMonstro");
        return sistemaMonstroDao.listarSistemasPorMonstro(idMonstro);
    }

    public SistemaMonstro associarSistema(Long idMonstro, Long idSistema, byte[] fichaArquivo) {
        Objects.requireNonNull(idMonstro, "idMonstro");
        Objects.requireNonNull(idSistema, "idSistema");

        SistemaMonstro vinculo = new SistemaMonstro();
        vinculo.setId(new SistemaMonstroPK(idSistema, idMonstro));
        if (fichaArquivo != null) {
            vinculo.setFichaArquivo(fichaArquivo);
        }
        return sistemaMonstroDao.inserir(vinculo);
    }

    public boolean desassociarSistema(Long idMonstro, Long idSistema) {
        Objects.requireNonNull(idMonstro, "idMonstro");
        Objects.requireNonNull(idSistema, "idSistema");
        return sistemaMonstroDao.deletar(new SistemaMonstroPK(idSistema, idMonstro));
    }

    static Monstro mapear(ResultSet rs) throws SQLException {
        Monstro monstro = new Monstro();
        monstro.setIdMonstro(rs.getLong("id_monstro"));
        monstro.setNome(rs.getString("nome"));
        monstro.setDescricao(rs.getString("descricao"));
        monstro.setHistoria(rs.getString("historia"));
        monstro.setImagem(rs.getBytes("imagem"));
        return monstro;
    }
}
