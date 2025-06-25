package com.targosystem.varejo.produtos.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;
import java.util.UUID;

public class ProdutoId {
    private final String value;

    public ProdutoId(String value) {
        Objects.requireNonNull(value, "Product ID cannot be null");
        if (value.isBlank()) {
            throw new DomainException("Product ID cannot be empty");
        }
        // Opcional: Adicionar validação de formato UUID se for sempre UUID
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new DomainException("Invalid Product ID format", e);
        }
        this.value = value;
    }

    public static ProdutoId generate() {
        return new ProdutoId(UUID.randomUUID().toString());
    }

    public static ProdutoId from(String value) {
        return new ProdutoId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProdutoId produtoId = (ProdutoId) o;
        return value.equals(produtoId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}