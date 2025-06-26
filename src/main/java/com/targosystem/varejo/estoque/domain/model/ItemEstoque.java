package com.targosystem.varejo.estoque.domain.model;

import java.util.Objects;
import java.util.UUID;

public class ItemEstoque {

    private String id;
    private String produtoId;
    private int quantidade;
    private Lote lote;
    private LocalizacaoArmazenamento localizacao;
    private String estoqueId;

    // Construtor para criar um novo ItemEstoque
    public ItemEstoque(String produtoId, int quantidade, Lote lote, LocalizacaoArmazenamento localizacao, String estoqueId) {
        this.id = UUID.randomUUID().toString();
        this.produtoId = Objects.requireNonNull(produtoId, "ID do produto não pode ser nulo.");
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade do item de estoque deve ser positiva.");
        }
        this.quantidade = quantidade;
        this.lote = Objects.requireNonNull(lote, "Lote não pode ser nulo.");
        this.localizacao = Objects.requireNonNull(localizacao, "Localização de armazenamento não pode ser nula.");
        this.estoqueId = Objects.requireNonNull(estoqueId, "ID do estoque pai não pode ser nulo.");
    }

    // Construtor para reconstruir ItemEstoque do banco de dados
    public ItemEstoque(String id, String produtoId, int quantidade, Lote lote, LocalizacaoArmazenamento localizacao, String estoqueId) {
        this.id = Objects.requireNonNull(id, "ID do item de estoque não pode ser nulo.");
        this.produtoId = Objects.requireNonNull(produtoId, "ID do produto não pode ser nulo.");
        if (quantidade < 0) { // Pode ser 0 se o item for esgotado, mas ainda precisa ser rastreado
            throw new IllegalArgumentException("Quantidade do item de estoque não pode ser negativa.");
        }
        this.quantidade = quantidade;
        this.lote = Objects.requireNonNull(lote, "Lote não pode ser nulo.");
        this.localizacao = Objects.requireNonNull(localizacao, "Localização de armazenamento não pode ser nula.");
        this.estoqueId = Objects.requireNonNull(estoqueId, "ID do estoque pai não pode ser nulo.");
    }

    // Métodos de negócio do ItemEstoque
    public void adicionarQuantidade(int qtd) {
        if (qtd <= 0) {
            throw new IllegalArgumentException("Quantidade a adicionar deve ser positiva.");
        }
        this.quantidade += qtd;
    }

    public void removerQuantidade(int qtd) {
        if (qtd <= 0) {
            throw new IllegalArgumentException("Quantidade a remover deve ser positiva.");
        }
        if (this.quantidade < qtd) {
            throw new IllegalArgumentException("Tentativa de remover mais itens do que o disponível neste item de estoque.");
        }
        this.quantidade -= qtd;
    }

    // Getters
    public String getId() { return id; }
    public String getProdutoId() { return produtoId; }
    public int getQuantidade() { return quantidade; }
    public Lote getLote() { return lote; }
    public LocalizacaoArmazenamento getLocalizacao() { return localizacao; }
    public String getEstoqueId() { return estoqueId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEstoque that = (ItemEstoque) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}