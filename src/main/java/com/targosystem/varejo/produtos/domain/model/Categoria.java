package com.targosystem.varejo.produtos.domain.model;

import com.targosystem.varejo.shared.domain.AggregateRoot;
import com.targosystem.varejo.shared.domain.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;

public class Categoria implements AggregateRoot {
    private CategoriaId id;
    private String nome;
    private String descricao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Categoria(String nome, String descricao) {
        this.id = CategoriaId.generate();
        setNome(nome);
        setDescricao(descricao);
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Categoria(CategoriaId id, String nome, String descricao, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = Objects.requireNonNull(id, "ID da categoria não pode ser nulo.");
        setNome(nome);
        setDescricao(descricao);
        this.dataCriacao = Objects.requireNonNull(dataCriacao, "Data de criação da categoria não pode ser nula.");
        this.dataAtualizacao = Objects.requireNonNull(dataAtualizacao, "Data de atualização da categoria não pode ser nula.");
    }

    // Getters
    public CategoriaId getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    // Setters (com validação se necessário)
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DomainException("Nome da categoria não pode ser nulo ou vazio.");
        }
        this.nome = nome;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void atualizarDescricao(String novaDescricao) {
        setDescricao(novaDescricao);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(id, categoria.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}