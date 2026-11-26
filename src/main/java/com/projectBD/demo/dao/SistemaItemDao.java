package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Item;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaItem;
import com.projectBD.demo.entity.SistemaItem.SistemaItemPK;
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
class SistemaItemDao {

    private static final String INSERT = """
            INSERT INTO SISTEMA_ITEM (id_sistema, id_item, ficha_arquivo)
            VALUES (:idSistema, :idItem, :fichaArquivo)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_sistema, id_item, ficha_arquivo
              FROM SISTEMA_ITEM
             WHERE id_sistema = :idSistema AND id_item = :idItem
            """;

    private static final String SELECT_ITEM_POR_SISTEMA = """
            SELECT t.id_item, t.nome, t.descricao, t.historia, t.imagem
              FROM SISTEMA_ITEM r
              JOIN ITEM t ON t.id_item = r.id_item
             WHERE r.id_sistema = :idSistema
             ORDER BY t.id_item
            """;

    private static final String SELECT_SISTEMA_POR_ITEM = """
            SELECT t.id_sistema, t.nome, t.editora, t.edicao, t.livro_regras
              FROM SISTEMA_ITEM r
              JOIN SISTEMA t ON t.id_sistema = r.id_sistema
             WHERE r.id_item = :idItem
             ORDER BY t.id_sistema
            """;

    private static final String UPDATE = """
            UPDATE SISTEMA_ITEM
               SET ficha_arquivo = :fichaArquivo
             WHERE id_sistema = :idSistema AND id_item = :idItem
            """;

    private static final String DELETE = """
            DELETE FROM SISTEMA_ITEM
             WHERE id_sistema = :idSistema AND id_item = :idItem
            """;

    @Autowired
    private DataSource dataSource;

    SistemaItem inserir(SistemaItem vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        SistemaItemPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT)) {

            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idItem", pk.getIdItem());
            ps.setParam("fichaArquivo", vinculo.getFichaArquivo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir SISTEMA_ITEM", e);
        }

        return buscarPorId(pk).orElseThrow(
                () -> new DaoException("SISTEMA_ITEM inserido nao foi encontrado: " + pk));
    }

    Optional<SistemaItem> buscarPorId(SistemaItemPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idItem", pk.getIdItem());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar SISTEMA_ITEM " + pk, e);
        }
    }

    List<Item> listarItensPorSistema(Long idSistema) {
        Objects.requireNonNull(idSistema, "idSistema");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_ITEM_POR_SISTEMA)) {

            ps.setParam("idSistema", idSistema);
            try (ResultSet rs = ps.executeQuery()) {
                List<Item> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(ItemDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar ITEM de SISTEMA id= " + idSistema, e);
        }
    }

    List<Sistema> listarSistemasPorItem(Long idItem) {
        Objects.requireNonNull(idItem, "idItem");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_SISTEMA_POR_ITEM)) {

            ps.setParam("idItem", idItem);
            try (ResultSet rs = ps.executeQuery()) {
                List<Sistema> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(SistemaDao.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar SISTEMA de ITEM id= " + idItem, e);
        }
    }

    boolean atualizar(SistemaItem vinculo) {
        Objects.requireNonNull(vinculo, "vinculo");
        SistemaItemPK pk = vinculo.getId();

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("fichaArquivo", vinculo.getFichaArquivo());
            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idItem", pk.getIdItem());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar SISTEMA_ITEM " + pk, e);
        }
    }

    boolean deletar(SistemaItemPK pk) {
        Objects.requireNonNull(pk, "pk");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idSistema", pk.getIdSistema());
            ps.setParam("idItem", pk.getIdItem());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar SISTEMA_ITEM " + pk, e);
        }
    }

    private SistemaItem mapear(ResultSet rs) throws SQLException {
        SistemaItem vinculo = new SistemaItem();
        long idSistema = rs.getLong("id_sistema");
        long idItem = rs.getLong("id_item");
        vinculo.setId(new SistemaItemPK(idSistema, idItem));
        vinculo.setSistema(Sistema.ref(idSistema));
        vinculo.setItem(Item.ref(idItem));
        vinculo.setFichaArquivo(rs.getBytes("ficha_arquivo"));
        return vinculo;
    }
}
