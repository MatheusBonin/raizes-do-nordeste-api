package com.raizes.nordeste_api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "UNIDADES")
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da unidade é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "A cidade/região é obrigatória")
    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(name = "cozinha_completa", nullable = false)
    private Boolean cozinhaCompleta = true;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public Boolean getCozinhaCompleta() { return cozinhaCompleta; }
    public void setCozinhaCompleta(Boolean cozinhaCompleta) { this.cozinhaCompleta = cozinhaCompleta; }
}