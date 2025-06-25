package com.targosystem.varejo.seguranca.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nome_completo", length = 255)
    private String nomeCompleto;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @ManyToMany(fetch = FetchType.EAGER) // Carrega papéis junto com o usuário
    @JoinTable(
            name = "usuario_papeis",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "papel_id")
    )
    private Set<PapelJpaEntity> papeis = new HashSet<>();

    public UsuarioJpaEntity() {}

    public UsuarioJpaEntity(String id, String username, String passwordHash, String nomeCompleto, String email, boolean ativo, LocalDateTime dataCadastro, LocalDateTime ultimoLogin, Set<PapelJpaEntity> papeis) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.ativo = ativo;
        this.dataCadastro = dataCadastro;
        this.ultimoLogin = ultimoLogin;
        this.papeis = papeis;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public LocalDateTime getUltimoLogin() { return ultimoLogin; }
    public void setUltimoLogin(LocalDateTime ultimoLogin) { this.ultimoLogin = ultimoLogin; }
    public Set<PapelJpaEntity> getPapeis() { return papeis; }
    public void setPapeis(Set<PapelJpaEntity> papeis) { this.papeis = papeis; }
}