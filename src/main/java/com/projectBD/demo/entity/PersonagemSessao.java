package com.projectBD.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
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
@Table(name = "PERSONAGEM_SESSAO")
public class PersonagemSessao {

    @EmbeddedId
    private PersonagemSessaoPK id;

    @MapsId("idPersonagem")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_personagem")
    private Personagem personagem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns(value = {
            @JoinColumn(name = "id_campanha", referencedColumnName = "id_campanha",
                    insertable = false, updatable = false),
            @JoinColumn(name = "numero_sessao", referencedColumnName = "numero_sessao",
                    insertable = false, updatable = false)
    })
    private Sessao sessao;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class PersonagemSessaoPK implements Serializable {

        @Column(name = "id_personagem")
        private Long idPersonagem;

        @Column(name = "id_campanha")
        private Long idCampanha;

        @Column(name = "numero_sessao")
        private Long numeroSessao;
    }
}
