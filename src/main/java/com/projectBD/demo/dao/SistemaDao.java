package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Item;
import com.projectBD.demo.entity.Monstro;
import com.projectBD.demo.entity.NPC;
import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaItem;
import com.projectBD.demo.entity.SistemaItem.SistemaItemPK;
import com.projectBD.demo.entity.SistemaMonstro;
import com.projectBD.demo.entity.SistemaMonstro.SistemaMonstroPK;
import com.projectBD.demo.entity.SistemaNpc;
import com.projectBD.demo.entity.SistemaNpc.SistemaNpcPK;
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
public class SistemaDao {

    private static final String INSERT = """
            INSERT INTO SISTEMA (nome, editora, edicao, livro_regras)
            VALUES (:nome, :editora, :edicao, :livroRegras)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_sistema, nome, editora, edicao, livro_regras
              FROM SISTEMA
             WHERE id_sistema = :idSistema
            """;

    private static final String SELECT_ALL = """
            SELECT id_sistema, nome, editora, edicao, livro_regras
              FROM SISTEMA
             ORDER BY id_sistema
            """;

    private static final String UPDATE = """
            UPDATE SISTEMA
               SET nome = :nome, editora = :editora, edicao = :edicao,
                   livro_regras = :livroRegras
             WHERE id_sistema = :idSistema
            """;

    private static final String DELETE = """
            DELETE FROM SISTEMA
             WHERE id_sistema = :idSistema
            """;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SistemaMonstroDao sistemaMonstroDao;

    @Autowired
    private SistemaItemDao sistemaItemDao;

    @Autowired
    private SistemaPersonagemDao sistemaPersonagemDao;

    @Autowired
    private SistemaNpcDao sistemaNpcDao;

    public Sistema inserir(Sistema sistema) {
        Objects.requireNonNull(sistema, "sistema");

        long id;
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT, "id_sistema")) {

            ps.setParam("nome", sistema.getNome());
            ps.setParam("editora", sistema.getEditora());
            ps.setParam("edicao", sistema.getEdicao());
            ps.setParam("livroRegras", sistema.getLivroRegras());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new DaoException("O banco nao retornou o id gerado de SISTEMA");
                }
                id = chaves.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir SISTEMA", e);
        }

        return buscarPorId(id).orElseThrow(
                () -> new DaoException("SISTEMA inserido nao foi encontrado: id=" + id));
    }

    public Optional<Sistema> buscarPorId(Long idSistema) {
        Objects.requireNonNull(idSistema, "idSistema");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idSistema", idSistema);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar SISTEMA id=" + idSistema, e);
        }
    }

    public List<Sistema> listarTodos() {
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Sistema> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar SISTEMA", e);
        }
    }

    public boolean atualizar(Sistema sistema) {
        Objects.requireNonNull(sistema, "sistema");
        Objects.requireNonNull(sistema.getIdSistema(), "sistema.idSistema");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("nome", sistema.getNome());
            ps.setParam("editora", sistema.getEditora());
            ps.setParam("edicao", sistema.getEdicao());
            ps.setParam("livroRegras", sistema.getLivroRegras());
            ps.setParam("idSistema", sistema.getIdSistema());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar SISTEMA id=" + sistema.getIdSistema(), e);
        }
    }

    public boolean deletar(Long idSistema) {
        Objects.requireNonNull(idSistema, "idSistema");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idSistema", idSistema);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar SISTEMA id=" + idSistema, e);
        }
    }

    public List<Monstro> buscarMonstros(Long idSistema) {
        Objects.requireNonNull(idSistema, "idSistema");
        return sistemaMonstroDao.listarMonstrosPorSistema(idSistema);
    }

    public SistemaMonstro associarMonstro(Long idSistema, Long idMonstro, byte[] fichaArquivo) {
        Objects.requireNonNull(idSistema, "idSistema");
        Objects.requireNonNull(idMonstro, "idMonstro");

        SistemaMonstro vinculo = new SistemaMonstro();
        vinculo.setId(new SistemaMonstroPK(idSistema, idMonstro));
        if (fichaArquivo != null) {
            vinculo.setFichaArquivo(fichaArquivo);
        }
        return sistemaMonstroDao.inserir(vinculo);
    }

    public boolean desassociarMonstro(Long idSistema, Long idMonstro) {
        Objects.requireNonNull(idSistema, "idSistema");
        Objects.requireNonNull(idMonstro, "idMonstro");
        return sistemaMonstroDao.deletar(new SistemaMonstroPK(idSistema, idMonstro));
    }

    public List<Item> buscarItens(Long idSistema) {
        Objects.requireNonNull(idSistema, "idSistema");
        return sistemaItemDao.listarItensPorSistema(idSistema);
    }

    public SistemaItem associarItem(Long idSistema, Long idItem, byte[] fichaArquivo) {
        Objects.requireNonNull(idSistema, "idSistema");
        Objects.requireNonNull(idItem, "idItem");

        SistemaItem vinculo = new SistemaItem();
        vinculo.setId(new SistemaItemPK(idSistema, idItem));
        if (fichaArquivo != null) {
            vinculo.setFichaArquivo(fichaArquivo);
        }
        return sistemaItemDao.inserir(vinculo);
    }

    public boolean desassociarItem(Long idSistema, Long idItem) {
        Objects.requireNonNull(idSistema, "idSistema");
        Objects.requireNonNull(idItem, "idItem");
        return sistemaItemDao.deletar(new SistemaItemPK(idSistema, idItem));
    }

    public List<Personagem> buscarPersonagens(Long idSistema) {
        Objects.requireNonNull(idSistema, "idSistema");
        return sistemaPersonagemDao.listarPersonagensPorSistema(idSistema);
    }

    public SistemaPersonagem associarPersonagem(Long idSistema, Long idPersonagem, byte[] fichaArquivo) {
        Objects.requireNonNull(idSistema, "idSistema");
        Objects.requireNonNull(idPersonagem, "idPersonagem");

        SistemaPersonagem vinculo = new SistemaPersonagem();
        vinculo.setId(new SistemaPersonagemPK(idSistema, idPersonagem));
        if (fichaArquivo != null) {
            vinculo.setFichaArquivo(fichaArquivo);
        }
        return sistemaPersonagemDao.inserir(vinculo);
    }

    public boolean desassociarPersonagem(Long idSistema, Long idPersonagem) {
        Objects.requireNonNull(idSistema, "idSistema");
        Objects.requireNonNull(idPersonagem, "idPersonagem");
        return sistemaPersonagemDao.deletar(new SistemaPersonagemPK(idSistema, idPersonagem));
    }

    public List<NPC> buscarNPCs(Long idSistema) {
        Objects.requireNonNull(idSistema, "idSistema");
        return sistemaNpcDao.listarNPCsPorSistema(idSistema);
    }

    public SistemaNpc associarNPC(Long idSistema, Long idNpc, byte[] fichaArquivo) {
        Objects.requireNonNull(idSistema, "idSistema");
        Objects.requireNonNull(idNpc, "idNpc");

        SistemaNpc vinculo = new SistemaNpc();
        vinculo.setId(new SistemaNpcPK(idSistema, idNpc));
        if (fichaArquivo != null) {
            vinculo.setFichaArquivo(fichaArquivo);
        }
        return sistemaNpcDao.inserir(vinculo);
    }

    public boolean desassociarNPC(Long idSistema, Long idNpc) {
        Objects.requireNonNull(idSistema, "idSistema");
        Objects.requireNonNull(idNpc, "idNpc");
        return sistemaNpcDao.deletar(new SistemaNpcPK(idSistema, idNpc));
    }

    static Sistema mapear(ResultSet rs) throws SQLException {
        Sistema sistema = new Sistema();
        sistema.setIdSistema(rs.getLong("id_sistema"));
        sistema.setNome(rs.getString("nome"));
        sistema.setEditora(rs.getString("editora"));
        sistema.setEdicao(rs.getString("edicao"));
        sistema.setLivroRegras(rs.getString("livro_regras"));
        return sistema;
    }
}
