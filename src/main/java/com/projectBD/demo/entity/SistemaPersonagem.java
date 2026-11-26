package com.projectBD.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SISTEMA_PERSONAGEM")
public class SistemaPersonagem {

    @EmbeddedId
    private SistemaPersonagemPK id;

    @MapsId("idSistema")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sistema")
    private Sistema sistema;

    @MapsId("idPersonagem")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_personagem")
    private Personagem personagem;

    @Lob
    @Column(name = "ficha_arquivo", columnDefinition = "BLOB")
    private byte[] fichaArquivo;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class SistemaPersonagemPK implements Serializable {

        @Column(name = "id_sistema")
        private Long idSistema;

        @Column(name = "id_personagem")
        private Long idPersonagem;
    }
}
