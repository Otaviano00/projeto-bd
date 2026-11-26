package com.projectBD.demo.entity;

import java.io.Serializable;

import com.projectBD.demo.entity.enums.StatusCampanhaNPC;

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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CAMPANHA_NPC")
public class CampanhaNpc {

    @EmbeddedId
    private CampanhaNpcPK id;

    @MapsId("idCampanha")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_campanha")
    private Campanha campanha;

    @MapsId("idNpc")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_npc")
    private NPC npc;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusCampanhaNPC status = StatusCampanhaNPC.ATIVO;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class CampanhaNpcPK implements Serializable {

        @Column(name = "id_campanha")
        private Long idCampanha;

        @Column(name = "id_npc")
        private Long idNpc;
    }
}
