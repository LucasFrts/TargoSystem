package com.targosystem.varejo.estoque.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;
import java.util.UUID;

public class ItemEstoque {
    private String id;
    private String produtoId;
    private int quantidade;
    private Lote lote;
    private LocalizacaoArmazenamento localizacao;
    private String estoqueId; // ID do estoque ao qual este item pertence
    private String localEstoqueId; // NOVO: ID do LocalEstoque ao qual este item pertence (denormalizado para consultas)

    // Construtor para criar um novo ItemEstoque
    public ItemEstoque(String produtoId, int quantidade, Lote lote, LocalizacaoArmazenamento localizacao, String estoqueId, String localEstoqueId) {
        this.id = UUID.randomUUID().toString();
        this.produtoId = Objects.requireNonNull(produtoId);
        this.quantidade = quantidade;
        this.lote = Objects.requireNonNull(lote);
        this.localizacao = Objects.requireNonNull(localizacao);
        this.estoqueId = Objects.requireNonNull(estoqueId);
        this.localEstoqueId = Objects.requireNonNull(localEstoqueId); // NOVO
    }

    // Construtor para reconstrução da persistência
    public ItemEstoque(String id, String produtoId, int quantidade, Lote lote, LocalizacaoArmazenamento localizacao, String estoqueId, String localEstoqueId) {
        this.id = Objects.requireNonNull(id);
        this.produtoId = Objects.requireNonNull(produtoId);
        this.quantidade = quantidade;
        this.lote = Objects.requireNonNull(lote);
        this.localizacao = Objects.requireNonNull(localizacao);
        this.estoqueId = Objects.requireNonNull(estoqueId);
        this.localEstoqueId = Objects.requireNonNull(localEstoqueId); // NOVO
    }

    public void adicionarQuantidade(int qtd) {
        if (qtd <= 0) {
            throw new DomainException("A quantidade a ser adicionada deve ser positiva.");
        }
        this.quantidade += qtd;
    }

    public void removerQuantidade(int qtd) {
        if (qtd <= 0) {
            throw new DomainException("A quantidade a ser removida deve ser positiva.");
        }
        if (this.quantidade < qtd) {
            throw new DomainException("Quantidade insuficiente em ItemEstoque. Disponível: " + this.quantidade + ", Tentou remover: " + qtd);
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
    public String getLocalEstoqueId() { return localEstoqueId; } // NOVO GETTER

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEstoque that = (ItemEstoque) o;
        // Um item de estoque é único pelo ID ou pela combinação de produto, lote, localização e o estoque principal
        return Objects.equals(id, that.id) ||
                (Objects.equals(produtoId, that.produtoId) &&
                        Objects.equals(lote, that.lote) &&
                        Objects.equals(localizacao, that.localizacao) &&
                        Objects.equals(estoqueId, that.estoqueId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, produtoId, lote, localizacao, estoqueId);
    }
}