package com.targosystem.varejo.estoque.domain.model;

import java.util.Objects;

// Objeto de Valor: Imutável, sem ID próprio
public class LocalizacaoArmazenamento {
    private final String corredor;
    private final String prateleira;
    private final String nivel;

    public LocalizacaoArmazenamento(String corredor, String prateleira, String nivel) {
        this.corredor = Objects.requireNonNull(corredor, "Corredor não pode ser nulo.");
        this.prateleira = Objects.requireNonNull(prateleira, "Prateleira não pode ser nula.");
        this.nivel = Objects.requireNonNull(nivel, "Nível não pode ser nulo.");
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
        return Objects.equals(corredor, that.corredor) &&
                Objects.equals(prateleira, that.prateleira) &&
                Objects.equals(nivel, that.nivel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(corredor, prateleira, nivel);
    }

    // Método padrão (se necessário para testes ou situações que não exijam detalhes finos)
    public static LocalizacaoArmazenamento defaultLocation() {
        return new LocalizacaoArmazenamento("A1", "01", "01");
    }
}