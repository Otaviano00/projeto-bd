package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Item;
import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.PersonagemItem;
import com.projectBD.demo.entity.PersonagemItem.PersonagemItemPK;
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
class PersonagemItemDao {

    private static final String INSERT = """
            INSERT INTO PERSONAGEM_ITEM (id_personagem, id_item, quantidade)
            VALUES (:idPersonagem, :idItem, :quantidade)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_personagem, id_item, quantidade
              FROM PERSONAGEM_ITEM
             WHERE id_personagem = :idPersonagem AND id_item = :idItem
            """;

    private static final String SELECT_ITEM_POR_PERSONAGEM = """
            SELECT t.id_item, t.nome, t.descricao, t.historia, t.imagem
              FROM PERSONAGEM_ITEM r
              JOIN ITEM t ON t.id_item = r.id_item
             WHERE r.id_personagem = :idPersonagem
             ORDER BY t.id_item
            """;

    private static final String SELECT_PERSONAGEM_POR_ITEM = """
            SELECT t.id_personagem, t.id_usuario, t.nome, t.status, t.imagem, t.created_at
              FROM PERSONAGEM_ITEM r
              JOIN PERSONAGEM t ON t.id_personagem = r.id_personagem
             WHERE r.id_item = :idItem
             ORDER BY t.id_personagem
            """;

    private static final String UPDATE = """
            UPDATE PERSONAGEM_ITEM
               SET quantidade = :quantidade
             WHERE id_personagem = :idPersonagem AND id_item = :idItem
            """;

    private static final String DELETE = """
            DELETE FROM PERSONAGEM_ITEM
             WHERE id_personagem = :idPersonagem AND id_item = :idItem
            """;

    @Autowired
    private DataSource dataSource;

    PersonagemItem inserir(PersonagemItem vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        PersonagemItemPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT)) {

            ps.setParam("idPersonagem", pk.getIdPersonagem());
            ps.setParam("idItem", pk.getIdItem());
            ps.setParam("quantidade", vinculo.getQuantidade());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir PERSONAGEM_ITEM", e);
        }

        return buscarPorId(pk).orElseThrow(
                () -> new DaoException("PERSONAGEM_ITEM inserido nao foi encontrado: " + pk));
    }

    Optional<PersonagemItem> buscarPorId(PersonagemItemPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idPersonagem", pk.getIdPersonagem());
            ps.setParam("idItem", pk.getIdItem());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar PERSONAGEM_ITEM " + pk, e);
        }
    }

    List<Item> listarItensPorPersonagem(Long idPersonagem) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_ITEM_POR_PERSONAGEM)) {

            ps.setParam("idPersonagem", idPersonagem);
            try (ResultSet rs = ps.executeQuery()) {
                List<Item> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(ItemDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar ITEM de PERSONAGEM id= " + idPersonagem, e);
        }
    }

    List<Personagem> listarPersonagensPorItem(Long idItem) {
        Objects.requireNonNull(idItem, "idItem");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_PERSONAGEM_POR_ITEM)) {

            ps.setParam("idItem", idItem);
            try (ResultSet rs = ps.executeQuery()) {
                List<Personagem> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(PersonagemDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar PERSONAGEM de ITEM id= " + idItem, e);
        }
    }

    boolean atualizar(PersonagemItem vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        PersonagemItemPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("quantidade", vinculo.getQuantidade());
            ps.setParam("idPersonagem", pk.getIdPersonagem());
            ps.setParam("idItem", pk.getIdItem());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar PERSONAGEM_ITEM " + pk, e);
        }
    }

    boolean deletar(PersonagemItemPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idPersonagem", pk.getIdPersonagem());
            ps.setParam("idItem", pk.getIdItem());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar PERSONAGEM_ITEM " + pk, e);
        }
    }

    private PersonagemItem mapear(ResultSet rs) throws SQLException {
        PersonagemItem vinculo = new PersonagemItem();
        long idPersonagem = rs.getLong("id_personagem");
        long idItem = rs.getLong("id_item");
        vinculo.setId(new PersonagemItemPK(idPersonagem, idItem));
        vinculo.setPersonagem(Personagem.ref(idPersonagem));
        vinculo.setItem(Item.ref(idItem));
        vinculo.setQuantidade(rs.getLong("quantidade"));
        return vinculo;
    }
}
