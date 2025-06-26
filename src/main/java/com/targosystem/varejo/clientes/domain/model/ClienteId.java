package com.targosystem.varejo.clientes.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.domain.ValueObject;

import java.util.Objects;
import java.util.UUID;

public record ClienteId(String value) implements ValueObject {
    public ClienteId {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("ID do cliente não pode ser nulo ou vazio.");
        }
        try {
            UUID.fromString(value); // Assumindo que o ID do cliente é um UUID
        } catch (IllegalArgumentException e) {
            throw new DomainException("Formato de ID de cliente inválido: " + value, e);
        }
    }

    public static ClienteId generate() {
        return new ClienteId(UUID.randomUUID().toString());
    }

    public static ClienteId from(String value) {
        return new ClienteId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}