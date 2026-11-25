package com.projectBD.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Anotacao {

    @Id
    private Long numero_anotacao;
    private String titulo;
    private String conteudo;
    private String visibilidade;
    private Date created_at;
    private Date finished_at;
}
