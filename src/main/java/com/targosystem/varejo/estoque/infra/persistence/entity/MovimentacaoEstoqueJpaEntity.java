// src/main/java/com/targosystem/varejo/estoque/infra/persistence/entity/MovimentacaoEstoqueJpaEntity.java
package com.targosystem.varejo.estoque.infra.persistence.entity;

import com.targosystem.varejo.estoque.domain.model.MovimentacaoEstoque;
import com.targosystem.varejo.estoque.domain.model.TipoMovimentacao;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes_estoque")
public class MovimentacaoEstoqueJpaEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estoque_id", nullable = false)
    private EstoqueJpaEntity estoque; // Referência à entidade de estoque pai

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private int quantidade;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(length = 255)
    private String motivo;

    @Embedded
    @AttributeOverrides({ // Para evitar conflito de nomes de colunas com outros @Embedded
            @AttributeOverride(name = "numeroLote", column = @Column(name = "mov_numero_lote")),
            @AttributeOverride(name = "dataFabricacao", column = @Column(name = "mov_data_fabricacao")),
            @AttributeOverride(name = "dataValidade", column = @Column(name = "mov_data_validade"))
    })
    private LoteJpaEmbeddable loteAfetado; // NOVO

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "corredor", column = @Column(name = "mov_loc_corredor")),
            @AttributeOverride(name = "prateleira", column = @Column(name = "mov_loc_prateleira")),
            @AttributeOverride(name = "nivel", column = @Column(name = "mov_loc_nivel"))
    })
    private LocalizacaoArmazenamentoJpaEmbeddable localizacaoAfetada; // NOVO

    protected MovimentacaoEstoqueJpaEntity() {}

    public MovimentacaoEstoqueJpaEntity(String id, EstoqueJpaEntity estoque, TipoMovimentacao tipo, int quantidade, LocalDateTime dataHora, String motivo, LoteJpaEmbeddable loteAfetado, LocalizacaoArmazenamentoJpaEmbeddable localizacaoAfetada) {
        this.id = id;
        this.estoque = estoque;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.dataHora = dataHora;
        this.motivo = motivo;
        this.loteAfetado = loteAfetado;
        this.localizacaoAfetada = localizacaoAfetada;
    }

    public static MovimentacaoEstoqueJpaEntity fromDomain(MovimentacaoEstoque movimentacao) {
        return new MovimentacaoEstoqueJpaEntity(
                movimentacao.getId(),
                null, // Será setado posteriormente pela EstoqueJpaEntity.fromDomain
                movimentacao.getTipo(),
                movimentacao.getQuantidade(),
                movimentacao.getDataHora(),
                movimentacao.getMotivo(),
                LoteJpaEmbeddable.fromDomain(movimentacao.getLoteAfetado()),
                LocalizacaoArmazenamentoJpaEmbeddable.fromDomain(movimentacao.getLocalizacaoAfetada())
        );
    }

    public MovimentacaoEstoque toDomain() {
        return new MovimentacaoEstoque(
                this.id,
                this.estoque.getId(),
                this.tipo,
                this.quantidade,
                this.dataHora,
                this.motivo,
                this.loteAfetado != null ? this.loteAfetado.toDomain() : null,
                this.localizacaoAfetada != null ? this.localizacaoAfetada.toDomain() : null
        );
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public EstoqueJpaEntity getEstoque() { return estoque; }
    public void setEstoque(EstoqueJpaEntity estoque) { this.estoque = estoque; }
    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public LoteJpaEmbeddable getLoteAfetado() { return loteAfetado; } // NOVO
    public void setLoteAfetado(LoteJpaEmbeddable loteAfetado) { this.loteAfetado = loteAfetado; } // NOVO
    public LocalizacaoArmazenamentoJpaEmbeddable getLocalizacaoAfetada() { return localizacaoAfetada; } // NOVO
    public void setLocalizacaoAfetada(LocalizacaoArmazenamentoJpaEmbeddable localizacaoAfetada) { this.localizacaoAfetada = localizacaoAfetada; } // NOVO
}