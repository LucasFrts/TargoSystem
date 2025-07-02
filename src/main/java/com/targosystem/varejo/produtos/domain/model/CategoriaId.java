package com.targosystem.varejo.produtos.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.domain.ValueObject;

import java.util.UUID;

public record CategoriaId(String value) implements ValueObject {
    public CategoriaId {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("ID da categoria não pode ser nulo ou vazio.");
        }
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new DomainException("Formato de ID de categoria inválido: " + value, e);
        }
    }

    public static CategoriaId generate() {
        return new CategoriaId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}