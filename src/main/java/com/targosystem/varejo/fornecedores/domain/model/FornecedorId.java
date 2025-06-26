package com.targosystem.varejo.fornecedores.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.domain.ValueObject;

import java.util.Objects;
import java.util.UUID;

public record FornecedorId(String value) implements ValueObject {

    public FornecedorId {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("ID do fornecedor não pode ser nulo ou vazio.");
        }
        // Opcional: Adicionar validação de formato UUID se todos os IDs forem UUIDs
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new DomainException("Formato de ID de fornecedor inválido: " + value, e);
        }
    }

    public static FornecedorId generate() {
        return new FornecedorId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}