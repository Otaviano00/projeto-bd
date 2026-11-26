package com.projectBD.demo.dao;

import com.projectBD.demo.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;

@Repository
public class UsuarioDao {

    private static final String INSERT = """
            INSERT INTO USUARIO (nome, email, hash_senha, data_nascimento)
            VALUES (:nome, :email, :hashSenha, :dataNascimento)
            """;

    private static final String SELECT_BY_ID = """
            SELECT id_usuario, nome, email, hash_senha, data_nascimento, created_at
              FROM USUARIO
             WHERE id_usuario = :idUsuario
            """;

    private static final String SELECT_ALL = """
            SELECT id_usuario, nome, email, hash_senha, data_nascimento, created_at
              FROM USUARIO
             ORDER BY id_usuario
            """;

    private static final String UPDATE = """
            UPDATE USUARIO
               SET nome = :nome, email = :email, hash_senha = :hashSenha,
                   data_nascimento = :dataNascimento
             WHERE id_usuario = :idUsuario
            """;

    private static final String DELETE = """
            DELETE FROM USUARIO
             WHERE id_usuario = :idUsuario
            """;

    @Autowired
    private DataSource dataSource;

    public Usuario inserir(Usuario usuario) {
        Objects.requireNonNull(usuario, "usuario");

        long id;
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, INSERT, "id_usuario")) {

            ps.setParam("nome", usuario.getNome());
            ps.setParam("email", usuario.getEmail());
            ps.setParam("hashSenha", usuario.getHashSenha());
            ps.setParam("dataNascimento", usuario.getDataNascimento());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new DaoException("O banco nao retornou o id gerado de USUARIO");
                }
                id = chaves.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir USUARIO", e);
        }

        return buscarPorId(id).orElseThrow(
                () -> new DaoException("USUARIO inserido nao foi encontrado: id=" + id));
    }

    public Optional<Usuario> buscarPorId(Long idUsuario) {
        Objects.requireNonNull(idUsuario, "idUsuario");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_BY_ID)) {

            ps.setParam("idUsuario", idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar USUARIO id=" + idUsuario, e);
        }
    }

    public List<Usuario> listarTodos() {
        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Usuario> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar USUARIO", e);
        }
    }

    public boolean atualizar(Usuario usuario) {
        Objects.requireNonNull(usuario, "usuario");
        Objects.requireNonNull(usuario.getIdUsuario(), "usuario.idUsuario");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, UPDATE)) {

            ps.setParam("nome", usuario.getNome());
            ps.setParam("email", usuario.getEmail());
            ps.setParam("hashSenha", usuario.getHashSenha());
            ps.setParam("dataNascimento", usuario.getDataNascimento());
            ps.setParam("idUsuario", usuario.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar USUARIO id=" + usuario.getIdUsuario(), e);
        }
    }

    public boolean deletar(Long idUsuario) {
        Objects.requireNonNull(idUsuario, "idUsuario");

        try (Connection conn = dataSource.getConnection();
             NamedPreparedStatement ps = NamedPreparedStatement.prepare(conn, DELETE)) {

            ps.setParam("idUsuario", idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar USUARIO id=" + idUsuario, e);
        }
    }

    static Usuario mapear(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getLong("id_usuario"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setHashSenha(rs.getString("hash_senha"));
        usuario.setDataNascimento(rs.getObject("data_nascimento", LocalDate.class));
        usuario.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        return usuario;
    }
}
