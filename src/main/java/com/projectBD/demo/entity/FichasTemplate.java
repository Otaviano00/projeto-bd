package com.projectBD.demo.entity;

import com.projectBD.demo.entity.enums.TipoFicha;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "FICHAS_TEMPLATE")
public class FichasTemplate {

    @EmbeddedId
    private FichasTemplatePK id;

    @MapsId("idSistema")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sistema")
    private Sistema sistema;

    @Lob
    @Column(name = "ficha_arquivo", nullable = false, columnDefinition = "BLOB")
    private byte[] fichaArquivo;

    // TODO: validar se precisa de um conversor
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoFicha tipo;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class FichasTemplatePK implements Serializable {

        @Column(name = "numero_ficha")
        private Long numeroFicha;

        @Column(name = "id_sistema")
        private Long idSistema;
    }
}
