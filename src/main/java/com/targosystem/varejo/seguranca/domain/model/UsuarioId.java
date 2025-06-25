package com.targosystem.varejo.seguranca.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;
import java.util.UUID;

public class UsuarioId {
    private final String value;

    public UsuarioId(String value) {
        Objects.requireNonNull(value, "User ID cannot be null");
        if (value.isBlank()) {
            throw new DomainException("User ID cannot be empty");
        }
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new DomainException("Invalid User ID format", e);
        }
        this.value = value;
    }

    public static UsuarioId generate() {
        return new UsuarioId(UUID.randomUUID().toString());
    }

    public static UsuarioId from(String value) {
        return new UsuarioId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioId usuarioId = (UsuarioId) o;
        return value.equals(usuarioId.value);
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