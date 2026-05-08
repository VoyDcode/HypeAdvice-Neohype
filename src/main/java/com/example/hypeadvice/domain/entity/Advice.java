package com.example.hypeadvice.domain.entity;

import com.example.hypeadvice.domain.enums.AdviceTypeEnum;
import com.google.gson.annotations.Expose;
import lombok.Data;

import javax.persistence.*;

/**
 * Entidade que representa um conselho persistido na base local.
 *
 * <p>Mapeada para a tabela {@code advice}. O campo {@code tipo} eh um enum
 * persistido como string (via {@link EnumType#STRING}) e nao pode ser nulo,
 * conforme requisito do exercicio 2.
 */
@Data
@javax.persistence.Entity
@Table(name = "advice")
public class Advice extends Entity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Expose
    @Column(name = "NOME", length = 100)
    private String nome;

    @Expose
    @Column(name = "DESCRICAO", columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @Expose
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false)
    private AdviceTypeEnum tipo;

    public Advice(String descricao, AdviceTypeEnum tipo) {
        this.descricao = descricao;
        this.tipo = tipo;
    }

    public Advice(String descricao) {
        this.descricao = descricao;
    }

    public Advice() {
    }
}
