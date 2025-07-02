package com.targosystem.varejo.promocoes.infra.persistence.entity;

import com.targosystem.varejo.promocoes.domain.model.ItemKit;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class ItemKitJpaEmbeddable {

    private String produtoId;
    private int quantidade;

    protected ItemKitJpaEmbeddable() {}

    public ItemKitJpaEmbeddable(String produtoId, int quantidade) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public static ItemKitJpaEmbeddable fromDomain(ItemKit itemKit) {
        return new ItemKitJpaEmbeddable(itemKit.getProdutoId(), itemKit.getQuantidade());
    }

    public ItemKit toDomain() {
        return new ItemKit(this.produtoId, this.quantidade);
    }

    public String getProdutoId() { return produtoId; }
    public void setProdutoId(String produtoId) { this.produtoId = produtoId; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemKitJpaEmbeddable that = (ItemKitJpaEmbeddable) o;
        return quantidade == that.quantidade && Objects.equals(produtoId, that.produtoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produtoId, quantidade);
    }
}