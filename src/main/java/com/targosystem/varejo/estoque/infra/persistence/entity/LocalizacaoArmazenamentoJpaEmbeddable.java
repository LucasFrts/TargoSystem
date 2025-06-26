package com.targosystem.varejo.estoque.infra.persistence.entity;

import com.targosystem.varejo.estoque.domain.model.LocalizacaoArmazenamento;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class LocalizacaoArmazenamentoJpaEmbeddable {

    @Column(name = "loc_corredor")
    private String corredor;

    @Column(name = "loc_prateleira")
    private String prateleira;

    @Column(name = "loc_nivel")
    private String nivel;

    protected LocalizacaoArmazenamentoJpaEmbeddable() {}

    public LocalizacaoArmazenamentoJpaEmbeddable(String corredor, String prateleira, String nivel) {
        this.corredor = corredor;
        this.prateleira = prateleira;
        this.nivel = nivel;
    }

    public static LocalizacaoArmazenamentoJpaEmbeddable fromDomain(LocalizacaoArmazenamento localizacao) {
        if (localizacao == null) return null; // Para casos opcionais
        return new LocalizacaoArmazenamentoJpaEmbeddable(localizacao.getCorredor(), localizacao.getPrateleira(), localizacao.getNivel());
    }

    public LocalizacaoArmazenamento toDomain() {
        return new LocalizacaoArmazenamento(this.corredor, this.prateleira, this.nivel);
    }

    // Getters e Setters
    public String getCorredor() { return corredor; }
    public void setCorredor(String corredor) { this.corredor = corredor; }
    public String getPrateleira() { return prateleira; }
    public void setPrateleira(String prateleira) { this.prateleira = prateleira; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalizacaoArmazenamentoJpaEmbeddable that = (LocalizacaoArmazenamentoJpaEmbeddable) o;
        return Objects.equals(corredor, that.corredor) && Objects.equals(prateleira, that.prateleira) && Objects.equals(nivel, that.nivel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(corredor, prateleira, nivel);
    }
}