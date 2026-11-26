package com.projectBD.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CENARIO")
public class Cenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cenario")
    private Long idCenario;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Lob
    @Column(name = "imagem", columnDefinition = "MEDIUMBLOB")
    private byte[] imagem;

    public static Cenario ref(Long idCenario) {
        Cenario cenario = new Cenario();
        cenario.setIdCenario(idCenario);
        return cenario;
    }
}
