package com.projectBD.demo.entity;

import com.projectBD.demo.entity.enums.Visibilidade;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ANOTACAO")
public class Anotacao {

    @EmbeddedId
    private AnotacaoPK id;

    @MapsId("idUsuario")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "conteudo", columnDefinition = "MEDIUMTEXT")
    private String conteudo;

    // TODO: validar se precisa de um conversor
    @Enumerated(EnumType.STRING)
    @Column(name = "visibilidade", nullable = false)
    private Visibilidade visibilidade = Visibilidade.PRIVADA;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class AnotacaoPK implements Serializable {

        @Column(name = "id_usuario")
        private Long idUsuario;

        @Column(name = "numero_anotacao")
        private Long numeroAnotacao;
    }
}
