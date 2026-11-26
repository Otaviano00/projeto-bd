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
@Table(name = "SISTEMA_NPC")
public class SistemaNpc {

    @EmbeddedId
    private SistemaNpcPK id;

    @MapsId("idSistema")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sistema")
    private Sistema sistema;

    @MapsId("idNpc")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_npc")
    private NPC npc;

    @Lob
    @Column(name = "ficha_arquivo", columnDefinition = "BLOB")
    private byte[] fichaArquivo;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class SistemaNpcPK implements Serializable {

        @Column(name = "id_sistema")
        private Long idSistema;

        @Column(name = "id_npc")
        private Long idNpc;
    }
}
