package com.targosystem.varejo.vendas.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.domain.ValueObject;

import java.util.UUID;

public record ItemVendaId(String value) implements ValueObject {
    public ItemVendaId {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("ID do item de venda não pode ser nulo ou vazio.");
        }
        try {
            UUID.fromString(value); 
        } catch (IllegalArgumentException e) {
            throw new DomainException("Formato de ID de item de venda inválido: " + value, e);
        }
    }

    public static ItemVendaId generate() {
        return new ItemVendaId(UUID.randomUUID().toString());
    }

    public static ItemVendaId from(String value) {
        return new ItemVendaId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}