package com.targosystem.varejo.vendas.domain.model;

import com.targosystem.varejo.produtos.domain.model.ProdutoId; // Importe ProdutoId
import com.targosystem.varejo.shared.domain.DomainException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID; // Para gerar ItemVendaId

public class ItemVenda {
    private ItemVendaId id; // Value Object para o ID do item da venda
    private ProdutoId idProduto; // Value Object para o ID do produto
    private String nomeProduto; // Nome do produto para "snapshot" no momento da venda
    private int quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal precoTotal;

    // Construtor para nova criação
    public ItemVenda(ProdutoId idProduto, String nomeProduto, int quantidade, BigDecimal precoUnitario) {
        this.id = new ItemVendaId(UUID.randomUUID().toString()); // Gerar ID único para o item
        setIdProduto(idProduto);
        setNomeProduto(nomeProduto);
        setQuantidade(quantidade);
        setPrecoUnitario(precoUnitario);
        calcularPrecoTotal();
    }

    // Construtor completo para reconstrução da persistência
    public ItemVenda(ItemVendaId id, ProdutoId idProduto, String nomeProduto, int quantidade, BigDecimal precoUnitario, BigDecimal precoTotal) {
        this.id = Objects.requireNonNull(id, "ID do item de venda não pode ser nulo.");
        this.idProduto = Objects.requireNonNull(idProduto, "ID do produto no item de venda não pode ser nulo.");
        this.nomeProduto = Objects.requireNonNull(nomeProduto, "Nome do produto no item de venda não pode ser nulo.");
        this.quantidade = quantidade;
        this.precoUnitario = Objects.requireNonNull(precoUnitario, "Preço unitário no item de venda não pode ser nulo.");
        this.precoTotal = Objects.requireNonNull(precoTotal, "Preço total no item de venda não pode ser nulo.");
    }

    // Getters
    public ItemVendaId getId() { return id; }
    public ProdutoId getIdProduto() { return idProduto; }
    public String getNomeProduto() { return nomeProduto; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public BigDecimal getPrecoTotal() { return precoTotal; }

    // Setters com validação
    private void setIdProduto(ProdutoId idProduto) {
        Objects.requireNonNull(idProduto, "ID do produto não pode ser nulo.");
        this.idProduto = idProduto;
    }

    private void setNomeProduto(String nomeProduto) {
        if (nomeProduto == null || nomeProduto.trim().isEmpty()) {
            throw new DomainException("Nome do produto no item de venda não pode ser nulo ou vazio.");
        }
        this.nomeProduto = nomeProduto;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new DomainException("Quantidade do item de venda deve ser maior que zero.");
        }
        this.quantidade = quantidade;
        calcularPrecoTotal();
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        if (precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Preço unitário do item de venda não pode ser nulo ou negativo.");
        }
        this.precoUnitario = precoUnitario;
        calcularPrecoTotal();
    }

    private void calcularPrecoTotal() {
        if (this.precoUnitario != null) {
            this.precoTotal = this.precoUnitario.multiply(new BigDecimal(this.quantidade));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemVenda itemVenda = (ItemVenda) o;
        return Objects.equals(id, itemVenda.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}