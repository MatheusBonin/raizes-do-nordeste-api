package com.raizes.nordeste_api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "USUARIOS")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome completo é obrigatório")
    @Column(name = "nome_completo", nullable = false, length = 100)
    private String nomeCompleto;

    @Email(message = "E-mail inválido")
    @NotBlank(message = "O e-mail é obrigatório")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String senha;

    @NotNull(message = "O aceite da LGPD é obrigatório")
    @Column(name = "aceite_lgpd", nullable = false)
    private Boolean aceiteLgpd = false;

    @NotNull(message = "O perfil do usuário é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PerfilUsuario perfil = PerfilUsuario.CLIENTE;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    
    public Boolean getAceiteLgpd() { return aceiteLgpd; }
    public void setAceiteLgpd(Boolean aceiteLgpd) { this.aceiteLgpd = aceiteLgpd; }

    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }

    @Column(name = "pontos_fidelidade", nullable = false, columnDefinition = "integer default 0")
    private Integer pontosFidelidade = 0;

    public Integer getPontosFidelidade() { return pontosFidelidade; }
    public void setPontosFidelidade(Integer pontosFidelidade) { this.pontosFidelidade = pontosFidelidade; }

}