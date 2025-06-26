package com.targosystem.varejo.vendas.infra.persistence.entity;

import com.targosystem.varejo.produtos.domain.model.ProdutoId;
import com.targosystem.varejo.vendas.domain.model.ItemVenda; // Keep your domain import

import com.targosystem.varejo.vendas.domain.model.ItemVendaId;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "itens_venda")
public class ItemVendaJpaEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private VendaJpaEntity venda;

    @Column(name = "id_produto", nullable = false)
    private String idProduto;

    @Column(name = "nome_produto", nullable = false)
    private String nomeProduto;

    @Column(nullable = false)
    private int quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "preco_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoTotal;

    protected ItemVendaJpaEntity() {}

    public ItemVendaJpaEntity(String id, VendaJpaEntity venda, String idProduto, String nomeProduto, int quantidade, BigDecimal precoUnitario, BigDecimal precoTotal) {
        this.id = id;
        this.venda = Objects.requireNonNull(venda, "VendaJpaEntity cannot be null for ItemVendaJpaEntity.");
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.precoTotal = precoTotal;
    }

    public static ItemVendaJpaEntity fromDomain(ItemVenda itemVenda, VendaJpaEntity vendaEntity) {
        return new ItemVendaJpaEntity(
                itemVenda.getId().value(),
                vendaEntity,
                itemVenda.getIdProduto().value(),
                itemVenda.getNomeProduto(),
                itemVenda.getQuantidade(),
                itemVenda.getPrecoUnitario(),
                itemVenda.getPrecoTotal()
        );
    }

    public ItemVenda toDomain() {
        return new ItemVenda(
                new ItemVendaId(this.id), // Reconstruir ItemVendaId
                new ProdutoId(this.idProduto), // Reconstruir ProdutoId
                this.nomeProduto,
                this.quantidade,
                this.precoUnitario,
                this.precoTotal
        );
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public VendaJpaEntity getVenda() { return venda; }
    public void setVenda(VendaJpaEntity venda) { this.venda = venda; }
    public String getIdProduto() { return idProduto; }
    public void setIdProduto(String idProduto) { this.idProduto = idProduto; }
    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
    public BigDecimal getPrecoTotal() { return precoTotal; }
    public void setPrecoTotal(BigDecimal precoTotal) { this.precoTotal = precoTotal; }
}