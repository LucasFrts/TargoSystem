package com.targosystem.varejo.vendas.domain.model;

import java.util.Objects;

public class VendaId {

    private final String value;

    public VendaId(String value) {
        this.value = Objects.requireNonNull(value, "VendaId não pode ser nulo.");
    }

    public String value() {
        return value;
    }

    public static VendaId generate() {
        return new VendaId(java.util.UUID.randomUUID().toString());
    }

    // ✅ ADICIONE ESTE MÉTODO
    public static VendaId from(String id) {
        return new VendaId(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof VendaId)) return false;
        VendaId other = (VendaId) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
