package com.targosystem.varejo.estoque.infra.persistence.entity;

import com.targosystem.varejo.estoque.domain.model.Lote;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class LoteJpaEmbeddable {

    @Column(name = "numero_lote")
    private String numeroLote;

    @Column(name = "data_fabricacao")
    private LocalDate dataFabricacao;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    protected LoteJpaEmbeddable() {}

    public LoteJpaEmbeddable(String numeroLote, LocalDate dataFabricacao, LocalDate dataValidade) {
        this.numeroLote = numeroLote;
        this.dataFabricacao = dataFabricacao;
        this.dataValidade = dataValidade;
    }

    public static LoteJpaEmbeddable fromDomain(Lote lote) {
        if (lote == null) return null; // Para casos opcionais
        return new LoteJpaEmbeddable(lote.getNumeroLote(), lote.getDataFabricacao(), lote.getDataValidade());
    }

    public Lote toDomain() {
        return new Lote(this.numeroLote, this.dataFabricacao, this.dataValidade);
    }

    // Getters e Setters
    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
    public LocalDate getDataFabricacao() { return dataFabricacao; }
    public void setDataFabricacao(LocalDate dataFabricacao) { this.dataFabricacao = dataFabricacao; }
    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoteJpaEmbeddable that = (LoteJpaEmbeddable) o;
        return Objects.equals(numeroLote, that.numeroLote) && Objects.equals(dataFabricacao, that.dataFabricacao) && Objects.equals(dataValidade, that.dataValidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroLote, dataFabricacao, dataValidade);
    }
}