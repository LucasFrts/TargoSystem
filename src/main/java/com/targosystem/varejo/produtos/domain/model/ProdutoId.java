package com.targosystem.varejo.produtos.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.domain.ValueObject;

import java.util.Objects;
import java.util.UUID;

public record ProdutoId(String value) implements ValueObject {
    public ProdutoId {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("ID do produto não pode ser nulo ou vazio.");
        }
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new DomainException("Formato de ID de produto inválido: " + value, e);
        }
    }

    /**
     * Gera um novo ProdutoId com um UUID aleatório.
     * @return Um novo ProdutoId.
     */
    public static ProdutoId generate() {
        return new ProdutoId(UUID.randomUUID().toString());
    }

    /**
     * Cria um ProdutoId a partir de uma String existente.
     * @param value A string que representa o ID do produto.
     * @return Um ProdutoId.
     */
    public static ProdutoId from(String value) { // ADDED THIS METHOD
        return new ProdutoId(value);
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }
}