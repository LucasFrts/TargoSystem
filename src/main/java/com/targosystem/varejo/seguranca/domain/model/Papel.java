package com.targosystem.varejo.seguranca.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;

public class Papel {
    private final Integer id; // ID gerado pelo DB
    private String nome;
    private String descricao;

    // Construtor para papéis existentes (com ID)
    public Papel(Integer id, String nome, String descricao) {
        Objects.requireNonNull(id, "Role ID cannot be null");
        setNome(nome);
        setDescricao(descricao);
        this.id = id;
    }

    // Construtor para novos papéis (sem ID, a ser gerado na persistência)
    public Papel(String nome, String descricao) {
        setNome(nome);
        setDescricao(descricao);
        this.id = null; // ID será gerado pelo banco de dados
    }

    // Métodos de comportamento (se houver regras de negócio específicas para Papel)
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

    // Setters com validações
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