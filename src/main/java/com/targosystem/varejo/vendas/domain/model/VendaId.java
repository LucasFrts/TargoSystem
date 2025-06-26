package com.targosystem.varejo.vendas.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.domain.ValueObject;

import java.util.Objects;
import java.util.UUID;

public record VendaId(String value) implements ValueObject {

    public VendaId {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("ID da venda não pode ser nulo ou vazio.");
        }
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new DomainException("Formato de ID de venda inválido: " + value, e);
        }
    }

    public static VendaId generate() {
        return new VendaId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}