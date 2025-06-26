// src/main/java/com/targosystem/varejo/estoque/domain/model/LocalizacaoArmazenamento.java
package com.targosystem.varejo.estoque.domain.model;

import java.util.Objects;

public class LocalizacaoArmazenamento {

    private final String corredor;
    private final String prateleira;
    private final String nivel; // Opcional (ex: "superior", "inferior", "A", "B", etc.)

    public LocalizacaoArmazenamento(String corredor, String prateleira, String nivel) {
        this.corredor = Objects.requireNonNull(corredor, "Corredor não pode ser nulo.");
        this.prateleira = Objects.requireNonNull(prateleira, "Prateleira não pode ser nula.");
        this.nivel = nivel; // Pode ser nulo
    }

    // Getters
    public String getCorredor() { return corredor; }
    public String getPrateleira() { return prateleira; }
    public String getNivel() { return nivel; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalizacaoArmazenamento that = (LocalizacaoArmazenamento) o;
        return corredor.equals(that.corredor) &&
                prateleira.equals(that.prateleira) &&
                Objects.equals(nivel, that.nivel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(corredor, prateleira, nivel);
    }
}