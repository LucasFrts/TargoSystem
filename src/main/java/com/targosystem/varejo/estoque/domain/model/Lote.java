package com.targosystem.varejo.estoque.domain.model;

import java.time.LocalDate;
import java.util.Objects;

public class Lote {

    private final String numeroLote;
    private final LocalDate dataFabricacao;
    private final LocalDate dataValidade; // Opcional, pode ser null

    public Lote(String numeroLote, LocalDate dataFabricacao, LocalDate dataValidade) {
        this.numeroLote = Objects.requireNonNull(numeroLote, "Número do lote não pode ser nulo.");
        this.dataFabricacao = Objects.requireNonNull(dataFabricacao, "Data de fabricação não pode ser nula.");

        if (dataValidade != null && dataValidade.isBefore(dataFabricacao)) {
            throw new IllegalArgumentException("Data de validade não pode ser anterior à data de fabricação.");
        }
        this.dataValidade = dataValidade;
    }

    // Getters
    public String getNumeroLote() { return numeroLote; }
    public LocalDate getDataFabricacao() { return dataFabricacao; }
    public LocalDate getDataValidade() { return dataValidade; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lote lote = (Lote) o;
        return numeroLote.equals(lote.numeroLote) &&
                dataFabricacao.equals(lote.dataFabricacao) &&
                Objects.equals(dataValidade, lote.dataValidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroLote, dataFabricacao, dataValidade);
    }

}