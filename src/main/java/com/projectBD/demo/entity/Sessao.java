package com.projectBD.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
@Table(name = "SESSAO")
public class Sessao {

    @EmbeddedId
    private SessaoPK id;

    @MapsId("idCampanha")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_campanha")
    private Campanha campanha;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "resumo_narrativo", columnDefinition = "MEDIUMTEXT")
    private String resumoNarrativo;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    public static Sessao ref(Long idCampanha, Long numeroSessao) {
        Sessao sessao = new Sessao();
        sessao.setId(new SessaoPK(idCampanha, numeroSessao));
        return sessao;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class SessaoPK implements Serializable {

        @Column(name = "id_campanha")
        private Long idCampanha;

        @Column(name = "numero_sessao")
        private Long numeroSessao;
    }
}
