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
public class CenarioDao {

    private static final String INSERT = """
            INSERT INTO CENARIO (nome, descricao, imagem)
            VALUES (:nome, :descricao, :imagem)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_cenario, nome, descricao, imagem
              FROM CENARIO
             WHERE id_cenario = :idCenario
            """;

    private static final String SELECT_ALL = """
            SELECT id_cenario, nome, descricao, imagem
              FROM CENARIO
             ORDER BY id_cenario
            """;

    private static final String UPDATE = """
            UPDATE CENARIO
               SET nome = :nome, descricao = :descricao, imagem = :imagem
             WHERE id_cenario = :idCenario
            """;

    private static final String DELETE = """
            DELETE FROM CENARIO
             WHERE id_cenario = :idCenario
            """;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CampanhaCenarioDao campanhaCenarioDao;

    public Cenario inserir(Cenario cenario) {
        Objects.requireNonNull(cenario, "cenario");

        long id;
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT, "id_cenario")) {

            ps.setParam("nome", cenario.getNome());
            ps.setParam("descricao", cenario.getDescricao());
            ps.setParam("imagem", cenario.getImagem());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new DaoException("O banco nao retornou o id gerado de CENARIO");
                }
                id = chaves.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir CENARIO", e);
        }

        return buscarPorId(id).orElseThrow(
                () -> new DaoException("CENARIO inserido nao foi encontrado: id=" + id));
    }

    public Optional<Cenario> buscarPorId(Long idCenario) {
        Objects.requireNonNull(idCenario, "idCenario");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idCenario", idCenario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar CENARIO id=" + idCenario, e);
        }
    }

    public List<Cenario> listarTodos() {
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Cenario> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar CENARIO", e);
        }
    }

    public boolean atualizar(Cenario cenario) {
        Objects.requireNonNull(cenario, "cenario");
        Objects.requireNonNull(cenario.getIdCenario(), "cenario.idCenario");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("nome", cenario.getNome());
            ps.setParam("descricao", cenario.getDescricao());
            ps.setParam("imagem", cenario.getImagem());
            ps.setParam("idCenario", cenario.getIdCenario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar CENARIO id=" + cenario.getIdCenario(), e);
        }
    }

    public boolean deletar(Long idCenario) {
        Objects.requireNonNull(idCenario, "idCenario");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idCenario", idCenario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar CENARIO id=" + idCenario, e);
        }
    }

    public List<Campanha> buscarCampanhas(Long idCenario) {
        Objects.requireNonNull(idCenario, "idCenario");
        return campanhaCenarioDao.listarCampanhasPorCenario(idCenario);
    }

    public CampanhaCenario associarCampanha(Long idCenario, Long idCampanha) {
        Objects.requireNonNull(idCenario, "idCenario");
        Objects.requireNonNull(idCampanha, "idCampanha");

        CampanhaCenario vinculo = new CampanhaCenario();
        vinculo.setId(new CampanhaCenarioPK(idCampanha, idCenario));
        return campanhaCenarioDao.inserir(vinculo);
    }

    public boolean desassociarCampanha(Long idCenario, Long idCampanha) {
        Objects.requireNonNull(idCenario, "idCenario");
        Objects.requireNonNull(idCampanha, "idCampanha");
        return campanhaCenarioDao.deletar(new CampanhaCenarioPK(idCampanha, idCenario));
    }

    static Cenario mapear(ResultSet rs) throws SQLException {
        Cenario cenario = new Cenario();
        cenario.setIdCenario(rs.getLong("id_cenario"));
        cenario.setNome(rs.getString("nome"));
        cenario.setDescricao(rs.getString("descricao"));
        cenario.setImagem(rs.getBytes("imagem"));
        return cenario;
    }
}
