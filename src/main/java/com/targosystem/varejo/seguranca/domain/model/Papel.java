package com.targosystem.varejo.seguranca.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;

public class Papel {
    private final Integer id;
    private String nome;
    private String descricao;

    public Papel(Integer id, String nome, String descricao) {
        Objects.requireNonNull(id, "Role ID cannot be null");
        setNome(nome);
        setDescricao(descricao);
        this.id = id;
    }

    public Papel(String nome, String descricao) {
        setNome(nome);
        setDescricao(descricao);
        this.id = null;
    }

    public void atualizar(String novoNome, String novaDescricao) {
        setNome(novoNome);
        setDescricao(novaDescricao);
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    private void setNome(String nome) {
        Objects.requireNonNull(nome, "Role name cannot be null");
        if (nome.isBlank()) {
            throw new DomainException("Role name cannot be empty");
        }
        this.nome = nome;
    }

    private void setDescricao(String descricao) {
        this.descricao = descricao; // Descrição pode ser nula
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Papel papel = (Papel) o;
        // Se o ID é null (novo papel não persistido), compara pelo nome
        // Caso contrário, compara pelo ID
        if (id == null || papel.id == null) {
            return nome.equals(papel.nome);
        }
        return id.equals(papel.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(nome);
    }

    @Override
    public String toString() {
        return "Papel{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}