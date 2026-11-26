package com.projectBD.demo.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.projectBD.demo.entity.Item;
import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.PersonagemItem;
import com.projectBD.demo.entity.PersonagemItem.PersonagemItemPK;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaItem;
import com.projectBD.demo.entity.SistemaItem.SistemaItemPK;

@Repository
public class ItemDao {

    private static final String INSERT = """
            INSERT INTO ITEM (nome, descricao, historia, imagem)
            VALUES (:nome, :descricao, :historia, :imagem)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_item, nome, descricao, historia, imagem
              FROM ITEM
             WHERE id_item = :idItem
            """;

    private static final String SELECT_ALL = """
            SELECT id_item, nome, descricao, historia, imagem
              FROM ITEM
             ORDER BY id_item
            """;

    private static final String UPDATE = """
            UPDATE ITEM
               SET nome = :nome, descricao = :descricao, historia = :historia,
                   imagem = :imagem
             WHERE id_item = :idItem
            """;

    private static final String DELETE = """
            DELETE FROM ITEM
             WHERE id_item = :idItem
            """;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PersonagemItemDao personagemItemDao;

    @Autowired
    private SistemaItemDao sistemaItemDao;

    public Item inserir(Item item) {
        Objects.requireNonNull(item, "item");

        long id;
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT, "id_item")) {

            ps.setParam("nome", item.getNome());
            ps.setParam("descricao", item.getDescricao());
            ps.setParam("historia", item.getHistoria());
            ps.setParam("imagem", item.getImagem());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new DaoException("O banco nao retornou o id gerado de ITEM");
                }
                id = chaves.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir ITEM", e);
        }

        return buscarPorId(id).orElseThrow(
                () -> new DaoException("ITEM inserido nao foi encontrado: id=" + id));
    }

    public Optional<Item> buscarPorId(Long idItem) {
        Objects.requireNonNull(idItem, "idItem");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idItem", idItem);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar ITEM id=" + idItem, e);
        }
    }

    public List<Item> listarTodos() {
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Item> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar ITEM", e);
        }
    }

    public boolean atualizar(Item item) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(item.getIdItem(), "item.idItem");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("nome", item.getNome());
            ps.setParam("descricao", item.getDescricao());
            ps.setParam("historia", item.getHistoria());
            ps.setParam("imagem", item.getImagem());
            ps.setParam("idItem", item.getIdItem());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar ITEM id=" + item.getIdItem(), e);
        }
    }

    public boolean deletar(Long idItem) {
        Objects.requireNonNull(idItem, "idItem");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idItem", idItem);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar ITEM id=" + idItem, e);
        }
    }

    public List<Personagem> buscarPersonagens(Long idItem) {
        Objects.requireNonNull(idItem, "idItem");
        return personagemItemDao.listarPersonagensPorItem(idItem);
    }

    public PersonagemItem associarPersonagem(Long idItem, Long idPersonagem, Long quantidade) {
        Objects.requireNonNull(idItem, "idItem");
        Objects.requireNonNull(idPersonagem, "idPersonagem");

        PersonagemItem vinculo = new PersonagemItem();
        vinculo.setId(new PersonagemItemPK(idPersonagem, idItem));
        if (quantidade != null) {
            vinculo.setQuantidade(quantidade);
        }
        return personagemItemDao.inserir(vinculo);
    }

    public boolean desassociarPersonagem(Long idItem, Long idPersonagem) {
        Objects.requireNonNull(idItem, "idItem");
        Objects.requireNonNull(idPersonagem, "idPersonagem");
        return personagemItemDao.deletar(new PersonagemItemPK(idPersonagem, idItem));
    }

    public List<Sistema> buscarSistemas(Long idItem) {
        Objects.requireNonNull(idItem, "idItem");
        return sistemaItemDao.listarSistemasPorItem(idItem);
    }

    public SistemaItem associarSistema(Long idItem, Long idSistema, byte[] fichaArquivo) {
        Objects.requireNonNull(idItem, "idItem");
        Objects.requireNonNull(idSistema, "idSistema");

        SistemaItem vinculo = new SistemaItem();
        vinculo.setId(new SistemaItemPK(idSistema, idItem));
        if (fichaArquivo != null) {
            vinculo.setFichaArquivo(fichaArquivo);
        }
        return sistemaItemDao.inserir(vinculo);
    }

    public boolean desassociarSistema(Long idItem, Long idSistema) {
        Objects.requireNonNull(idItem, "idItem");
        Objects.requireNonNull(idSistema, "idSistema");
        return sistemaItemDao.deletar(new SistemaItemPK(idSistema, idItem));
    }

    static Item mapear(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setIdItem(rs.getLong("id_item"));
        item.setNome(rs.getString("nome"));
        item.setDescricao(rs.getString("descricao"));
        item.setHistoria(rs.getString("historia"));
        item.setImagem(rs.getBytes("imagem"));
        return item;
    }
}
