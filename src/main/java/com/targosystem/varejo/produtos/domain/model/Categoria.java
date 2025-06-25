package com.targosystem.varejo.produtos.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;

public class Categoria {
    private final Integer id; // Pode ser um ID numérico gerado pelo DB
    private final String nome;
    private final String descricao;

    // Construtor para categorias existentes (com ID)
    public Categoria(Integer id, String nome, String descricao) {
        Objects.requireNonNull(id, "Category ID cannot be null");
        Objects.requireNonNull(nome, "Category name cannot be null");
        if (nome.isBlank()) {
            throw new DomainException("Category name cannot be empty");
        }
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    // Construtor para novas categorias (sem ID, a ser gerado na persistência)
    public Categoria(String nome, String descricao) {
        Objects.requireNonNull(nome, "Category name cannot be null");
        if (nome.isBlank()) {
            throw new DomainException("Category name cannot be empty");
        }
        this.id = null; // ID será gerado pelo banco de dados
        this.nome = nome;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        // Se o ID é null (nova categoria ainda não persistida), compara pelo nome
        // Caso contrário, compara pelo ID
        if (id == null || categoria.id == null) {
            return nome.equals(categoria.nome);
        }
        return id.equals(categoria.id);
    }

    @Override
    public int hashCode() {
        // Se o ID é null, usa o nome para o hash, caso contrário, usa o ID
        return id != null ? Objects.hash(id) : Objects.hash(nome);
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}