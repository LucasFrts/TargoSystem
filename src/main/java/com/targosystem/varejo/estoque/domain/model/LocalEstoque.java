package com.targosystem.varejo.estoque.domain.model;

import com.targosystem.varejo.shared.domain.AggregateRoot;
import java.time.LocalDateTime;
import java.util.Objects;

public class LocalEstoque implements AggregateRoot {
    private String id; // UUID
    private String nome;
    private TipoLocal tipo;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // Construtor para Locais INTERNOS (gerados automaticamente ou predefinidos)
    public LocalEstoque(String nome, TipoLocal tipo) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do local de estoque não pode ser nulo ou vazio.");
        }
        if (tipo == null || tipo == TipoLocal.FORNECEDOR || tipo == TipoLocal.CLIENTE) {
            throw new IllegalArgumentException("Tipo de local inválido para este construtor. Use INTERNO.");
        }
        this.id = java.util.UUID.randomUUID().toString();
        this.nome = nome;
        this.tipo = tipo;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor para Locais EXTERNOS (Fornecedor, Cliente) onde o ID é ditado pela entidade externa
    public LocalEstoque(String idExterno, String nome, TipoLocal tipo) {
        if (idExterno == null || idExterno.trim().isEmpty()) {
            throw new IllegalArgumentException("ID externo do local de estoque não pode ser nulo ou vazio para tipos FORNECEDOR/CLIENTE.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do local de estoque não pode ser nulo ou vazio.");
        }
        if (tipo == null || (tipo != TipoLocal.FORNECEDOR && tipo != TipoLocal.CLIENTE)) {
            throw new IllegalArgumentException("Tipo de local inválido para este construtor. Use FORNECEDOR ou CLIENTE.");
        }
        this.id = idExterno;
        this.nome = nome;
        this.tipo = tipo;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor para reconstrução da persistência
    public LocalEstoque(String id, String nome, TipoLocal tipo, boolean ativo, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = Objects.requireNonNull(id);
        this.nome = Objects.requireNonNull(nome);
        this.tipo = Objects.requireNonNull(tipo);
        this.ativo = ativo;
        this.dataCriacao = Objects.requireNonNull(dataCriacao);
        this.dataAtualizacao = Objects.requireNonNull(dataAtualizacao);
    }

    public void atualizarInformacoes(String novoNome, boolean novoStatus) {
        if (novoNome != null && !novoNome.trim().isEmpty()) {
            this.nome = novoNome;
        }
        this.ativo = novoStatus;
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public TipoLocal getTipo() { return tipo; }
    public boolean isAtivo() { return ativo; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalEstoque that = (LocalEstoque) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}