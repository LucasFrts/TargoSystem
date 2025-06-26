package com.targosystem.varejo.estoque.infra.persistence.entity;

import com.targosystem.varejo.estoque.domain.model.ItemEstoque;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "itens_estoque")
public class ItemEstoqueJpaEntity {

    @Id
    private String id;

    @Column(name = "produto_id", nullable = false)
    private String produtoId;

    @Column(nullable = false)
    private int quantidade;

    @Embedded // Incorpora o Objeto de Valor Lote
    private LoteJpaEmbeddable lote;

    @Embedded // Incorpora o Objeto de Valor LocalizacaoArmazenamento
    private LocalizacaoArmazenamentoJpaEmbeddable localizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estoque_id", nullable = false)
    private EstoqueJpaEntity estoque; // Referência à entidade de estoque pai

    protected ItemEstoqueJpaEntity() {}

    public ItemEstoqueJpaEntity(String id, String produtoId, int quantidade, LoteJpaEmbeddable lote, LocalizacaoArmazenamentoJpaEmbeddable localizacao, EstoqueJpaEntity estoque) {
        this.id = id;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.lote = lote;
        this.localizacao = localizacao;
        this.estoque = estoque;
    }

    public static ItemEstoqueJpaEntity fromDomain(ItemEstoque itemEstoque) {
        // A referência 'estoque' (EstoqueJpaEntity) será setada posteriormente no fromDomain de EstoqueJpaEntity
        return new ItemEstoqueJpaEntity(
                itemEstoque.getId(),
                itemEstoque.getProdutoId(),
                itemEstoque.getQuantidade(),
                LoteJpaEmbeddable.fromDomain(itemEstoque.getLote()),
                LocalizacaoArmazenamentoJpaEmbeddable.fromDomain(itemEstoque.getLocalizacao()),
                null // Será preenchido pela entidade pai
        );
    }

    public ItemEstoque toDomain() {
        return new ItemEstoque(
                this.id,
                this.produtoId,
                this.quantidade,
                this.lote.toDomain(),
                this.localizacao.toDomain(),
                this.estoque.getId() // Pega o ID do estoque pai
        );
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProdutoId() { return produtoId; }
    public void setProdutoId(String produtoId) { this.produtoId = produtoId; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public LoteJpaEmbeddable getLote() { return lote; }
    public void setLote(LoteJpaEmbeddable lote) { this.lote = lote; }
    public LocalizacaoArmazenamentoJpaEmbeddable getLocalizacao() { return localizacao; }
    public void setLocalizacao(LocalizacaoArmazenamentoJpaEmbeddable localizacao) { this.localizacao = localizacao; }
    public EstoqueJpaEntity getEstoque() { return estoque; }
    public void setEstoque(EstoqueJpaEntity estoque) { this.estoque = estoque; } // Setter para o ManyToOne
}