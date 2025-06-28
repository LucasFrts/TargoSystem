package com.targosystem.varejo.estoque.domain.model;

import java.util.Objects;
import java.util.UUID;

public class ItemMovimentacaoEstoque {
    private String id;
    private String movimentacaoId; // ID da movimentação a que este item pertence
    private String produtoId;
    private int quantidade;

    // Construtor para criar um novo item de movimentação (sem detalhes de lote/localização aqui)
    public ItemMovimentacaoEstoque(String produtoId, int quantidade) {
        this.id = UUID.randomUUID().toString();
        this.produtoId = Objects.requireNonNull(produtoId, "ID do produto não pode ser nulo.");
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade do item de movimentação deve ser positiva.");
        }
        this.quantidade = quantidade;
    }

    // Construtor para reconstrução da persistência (sem detalhes de lote/localização aqui)
    public ItemMovimentacaoEstoque(String id, String movimentacaoId, String produtoId, int quantidade) {
        this.id = Objects.requireNonNull(id);
        this.movimentacaoId = movimentacaoId; // Pode ser nulo inicialmente até ser setado pela MovimentacaoEstoque
        this.produtoId = Objects.requireNonNull(produtoId);
        this.quantidade = quantidade;
    }

    // Getters
    public String getId() { return id; }
    public String getMovimentacaoId() { return movimentacaoId; }
    public String getProdutoId() { return produtoId; }
    public int getQuantidade() { return quantidade; }

    // Setter para movimentacaoId (usado na criação da MovimentacaoEstoque)
    public void setMovimentacaoId(String movimentacaoId) {
        this.movimentacaoId = movimentacaoId;
    }

    // Setter para ID (usado na criação da MovimentacaoEstoque)
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemMovimentacaoEstoque that = (ItemMovimentacaoEstoque) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}