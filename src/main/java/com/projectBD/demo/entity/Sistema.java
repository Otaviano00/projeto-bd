package com.projectBD.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "SISTEMA")
public class Sistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sistema")
    private Long idSistema;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "editora", length = 120)
    private String editora;

    @Column(name = "edicao", length = 80)
    private String edicao;

    @Column(name = "livro_regras", length = 255)
    private String livroRegras;

    public static Sistema ref(Long idSistema) {
        Sistema sistema = new Sistema();
        sistema.setIdSistema(idSistema);
        return sistema;
    }
}
