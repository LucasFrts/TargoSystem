package com.targosystem.varejo.vendas.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;

import java.util.Objects;

public class Cliente { // É uma entidade dentro do contexto de Venda, pode ter ID próprio ou referenciar um ID de outro BC

    private String id; // ID do cliente, provavelmente de outro BC
    private String nome;
    private String cpf; // Pode ser útil para identificação

    public Cliente(String id, String nome, String cpf) {
        if (id == null || id.trim().isEmpty()) {
            throw new DomainException("ID do cliente não pode ser nulo ou vazio.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new DomainException("Nome do cliente não pode ser nulo ou vazio.");
        }
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new DomainException("CPF inválido. Deve conter 11 dígitos numéricos.");
        }
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}