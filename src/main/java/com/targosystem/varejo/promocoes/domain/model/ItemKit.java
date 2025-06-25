package com.targosystem.varejo.promocoes.domain.model;

import java.util.Objects;

// Objeto de Valor: sem ID próprio, imutável após a criação
public class ItemKit {

    private final String produtoId; // Referência ao ID do Produto (do BC de Produtos)
    private final int quantidade;

    public ItemKit(String produtoId, int quantidade) {
        this.produtoId = Objects.requireNonNull(produtoId, "ID do produto não pode ser nulo.");
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade do item no kit deve ser maior que zero.");
        }
        this.quantidade = quantidade;
    }

    public String getProdutoId() { return produtoId; }
    public int getQuantidade() { return quantidade; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemKit itemKit = (ItemKit) o;
        return quantidade == itemKit.quantidade && produtoId.equals(itemKit.produtoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produtoId, quantidade);
    }
}