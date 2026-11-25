package com.projectBD.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Monstro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_monstro;
    private String nome;
    private String descricao;
    private String historia;
    private String imagem;

}
