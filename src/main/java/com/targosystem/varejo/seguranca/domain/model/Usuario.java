package com.targosystem.varejo.seguranca.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Usuario {
    private final UsuarioId id;
    private String username;
    private String passwordHash; // O hash da senha, não a senha em texto plano
    private String nomeCompleto;
    private String email;
    private boolean ativo;
    private final LocalDateTime dataCadastro;
    private LocalDateTime ultimoLogin;
    private Set<Papel> papeis; // Set de papéis do usuário

    public Usuario(String username, String passwordHash, String nomeCompleto, String email) {
        this.id = UsuarioId.generate();
        setUsername(username);
        setPasswordHash(passwordHash);
        setNomeCompleto(nomeCompleto);
        setEmail(email);
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
        this.papeis = new HashSet<>();
    }

    public Usuario(UsuarioId id, String username, String passwordHash, String nomeCompleto, String email, boolean ativo, LocalDateTime dataCadastro, LocalDateTime ultimoLogin, Set<Papel> papeis) {
        Objects.requireNonNull(id, "User ID cannot be null for existing user");
        this.id = id;
        setUsername(username);
        setPasswordHash(passwordHash);
        setNomeCompleto(nomeCompleto);
        setEmail(email);
        this.ativo = ativo;
        Objects.requireNonNull(dataCadastro, "Data de cadastro cannot be null");
        this.dataCadastro = dataCadastro;
        this.ultimoLogin = ultimoLogin;
        this.papeis = new HashSet<>(papeis);
    }

    public void atualizarInformacoes(String novoNomeCompleto, String novoEmail) {
        setNomeCompleto(novoNomeCompleto);
        setEmail(novoEmail);
    }

    public void alterarSenha(String novoPasswordHash) {
        setPasswordHash(novoPasswordHash);
    }

    public void adicionarPapel(Papel papel) {
        Objects.requireNonNull(papel, "Role cannot be null");
        this.papeis.add(papel);
    }

    public void removerPapel(Papel papel) {
        Objects.requireNonNull(papel, "Role cannot be null");
        this.papeis.remove(papel);
    }

    public void ativar() {
        this.ativo = true;
    }

    public void inativar() {
        this.ativo = false;
    }

    public void registrarUltimoLogin() {
        this.ultimoLogin = LocalDateTime.now();
    }

    // Getters
    public UsuarioId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public Set<Papel> getPapeis() {
        return Collections.unmodifiableSet(papeis);
    }

    private void setUsername(String username) {
        Objects.requireNonNull(username, "Username cannot be null");
        if (username.isBlank()) {
            throw new DomainException("Username cannot be empty");
        }
        this.username = username;
    }

    private void setPasswordHash(String passwordHash) {
        Objects.requireNonNull(passwordHash, "Password hash cannot be null");
        if (passwordHash.isBlank()) {
            throw new DomainException("Password hash cannot be empty");
        }
        this.passwordHash = passwordHash;
    }

    private void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    private void setEmail(String email) {
        // Poderia adicionar validação de formato de e-mail aqui
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}