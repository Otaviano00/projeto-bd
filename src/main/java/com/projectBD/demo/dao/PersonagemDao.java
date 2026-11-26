package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Item;
import com.projectBD.demo.entity.Personagem;
import com.projectBD.demo.entity.PersonagemItem;
import com.projectBD.demo.entity.PersonagemItem.PersonagemItemPK;
import com.projectBD.demo.entity.PersonagemSessao;
import com.projectBD.demo.entity.PersonagemSessao.PersonagemSessaoPK;
import com.projectBD.demo.entity.Sessao;
import com.projectBD.demo.entity.Sessao.SessaoPK;
import com.projectBD.demo.entity.Sistema;
import com.projectBD.demo.entity.SistemaPersonagem;
import com.projectBD.demo.entity.SistemaPersonagem.SistemaPersonagemPK;
import com.projectBD.demo.entity.Usuario;
import com.projectBD.demo.entity.enums.StatusPersonagem;
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
public class PersonagemDao {

    private static final String INSERT = """
            INSERT INTO PERSONAGEM (id_usuario, nome, status, imagem)
            VALUES (:idUsuario, :nome, :status, :imagem)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_personagem, id_usuario, nome, status, imagem, created_at
              FROM PERSONAGEM
             WHERE id_personagem = :idPersonagem
            """;

    private static final String SELECT_ALL = """
            SELECT id_personagem, id_usuario, nome, status, imagem, created_at
              FROM PERSONAGEM
             ORDER BY id_personagem
            """;

    private static final String UPDATE = """
            UPDATE PERSONAGEM
               SET id_usuario = :idUsuario, nome = :nome, status = :status,
                   imagem = :imagem
             WHERE id_personagem = :idPersonagem
            """;

    private static final String DELETE = """
            DELETE FROM PERSONAGEM
             WHERE id_personagem = :idPersonagem
            """;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PersonagemItemDao personagemItemDao;

    @Autowired
    private SistemaPersonagemDao sistemaPersonagemDao;

    @Autowired
    private PersonagemSessaoDao personagemSessaoDao;

    public Personagem inserir(Personagem personagem) {
        Objects.requireNonNull(personagem, "personagem");

        long id;
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT, "id_personagem")) {

            ps.setParam("idUsuario", personagem.getUsuario().getIdUsuario());
            ps.setParam("nome", personagem.getNome());
            ps.setParam("status", personagem.getStatus());
            ps.setParam("imagem", personagem.getImagem());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new DaoException("O banco nao retornou o id gerado de PERSONAGEM");
                }
                id = chaves.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir PERSONAGEM", e);
        }

        return buscarPorId(id).orElseThrow(
                () -> new DaoException("PERSONAGEM inserido nao foi encontrado: id=" + id));
    }

    public Optional<Personagem> buscarPorId(Long idPersonagem) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idPersonagem", idPersonagem);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar PERSONAGEM id=" + idPersonagem, e);
        }
    }

    public List<Personagem> listarTodos() {
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Personagem> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar PERSONAGEM", e);
        }
    }

    public boolean atualizar(Personagem personagem) {
        Objects.requireNonNull(personagem, "personagem");
        Objects.requireNonNull(personagem.getIdPersonagem(), "personagem.idPersonagem");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("idUsuario", personagem.getUsuario().getIdUsuario());
            ps.setParam("nome", personagem.getNome());
            ps.setParam("status", personagem.getStatus());
            ps.setParam("imagem", personagem.getImagem());
            ps.setParam("idPersonagem", personagem.getIdPersonagem());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar PERSONAGEM id=" + personagem.getIdPersonagem(), e);
        }
    }

    public boolean deletar(Long idPersonagem) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idPersonagem", idPersonagem);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar PERSONAGEM id=" + idPersonagem, e);
        }
    }

    public List<Item> buscarItens(Long idPersonagem) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");
        return personagemItemDao.listarItensPorPersonagem(idPersonagem);
    }

    public PersonagemItem associarItem(Long idPersonagem, Long idItem, Long quantidade) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");
        Objects.requireNonNull(idItem, "idItem");

        PersonagemItem vinculo = new PersonagemItem();
        vinculo.setId(new PersonagemItemPK(idPersonagem, idItem));
        if (quantidade != null) {
            vinculo.setQuantidade(quantidade);
        }
        return personagemItemDao.inserir(vinculo);
    }

    public boolean desassociarItem(Long idPersonagem, Long idItem) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");
        Objects.requireNonNull(idItem, "idItem");
        return personagemItemDao.deletar(new PersonagemItemPK(idPersonagem, idItem));
    }

    public List<Sistema> buscarSistemas(Long idPersonagem) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");
        return sistemaPersonagemDao.listarSistemasPorPersonagem(idPersonagem);
    }

    public SistemaPersonagem associarSistema(Long idPersonagem, Long idSistema, byte[] fichaArquivo) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");
        Objects.requireNonNull(idSistema, "idSistema");

        SistemaPersonagem vinculo = new SistemaPersonagem();
        vinculo.setId(new SistemaPersonagemPK(idSistema, idPersonagem));
        if (fichaArquivo != null) {
            vinculo.setFichaArquivo(fichaArquivo);
        }
        return sistemaPersonagemDao.inserir(vinculo);
    }

    public boolean desassociarSistema(Long idPersonagem, Long idSistema) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");
        Objects.requireNonNull(idSistema, "idSistema");
        return sistemaPersonagemDao.deletar(new SistemaPersonagemPK(idSistema, idPersonagem));
    }

    public List<Sessao> buscarSessoes(Long idPersonagem) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");
        return personagemSessaoDao.listarSessoesPorPersonagem(idPersonagem);
    }

    public PersonagemSessao associarSessao(Long idPersonagem, SessaoPK pk) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");
        Objects.requireNonNull(pk, "pk");

        PersonagemSessao vinculo = new PersonagemSessao();
        vinculo.setId(new PersonagemSessaoPK(idPersonagem, pk.getIdCampanha(), pk.getNumeroSessao()));
        return personagemSessaoDao.inserir(vinculo);
    }

    public boolean desassociarSessao(Long idPersonagem, SessaoPK pk) {
        Objects.requireNonNull(idPersonagem, "idPersonagem");
        Objects.requireNonNull(pk, "pk");
        return personagemSessaoDao.deletar(
                new PersonagemSessaoPK(idPersonagem, pk.getIdCampanha(), pk.getNumeroSessao()));
    }

    static Personagem mapear(ResultSet rs) throws SQLException {
        Personagem personagem = new Personagem();
        personagem.setIdPersonagem(rs.getLong("id_personagem"));
        personagem.setUsuario(Usuario.ref(rs.getLong("id_usuario")));
        personagem.setNome(rs.getString("nome"));
        personagem.setStatus(StatusPersonagem.valueOf(rs.getString("status")));
        personagem.setImagem(rs.getBytes("imagem"));
        personagem.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        return personagem;
    }
}
