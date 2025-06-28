package com.targosystem.varejo.estoque.infra.persistence.entity;

import com.targosystem.varejo.estoque.domain.model.ItemMovimentacaoEstoque;
import jakarta.persistence.*;
// import java.time.LocalDate; // Nao e mais necessario aqui
import java.util.Objects;
// import java.math.BigDecimal; // Se o item tiver preço, para futuras expansões

@Entity
@Table(name = "itens_movimentacao_estoque")
public class ItemMovimentacaoEstoqueJpaEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimentacao_id", nullable = false)
    private MovimentacaoEstoqueJpaEntity movimentacao; // Referência ao cabeçalho da movimentação

    @Column(name = "produto_id", nullable = false)
    private String produtoId; // ID do produto movimentado

    @Column(nullable = false)
    private int quantidade; // Quantidade deste produto na movimentação

    // Removidos campos de lote e localização

    protected ItemMovimentacaoEstoqueJpaEntity() {}

    // Construtor original para a entidade JPA
    public ItemMovimentacaoEstoqueJpaEntity(String id, MovimentacaoEstoqueJpaEntity movimentacao, String produtoId, int quantidade) {
        this.id = id;
        this.movimentacao = movimentacao;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public static ItemMovimentacaoEstoqueJpaEntity fromDomain(ItemMovimentacaoEstoque domain) {
        return new ItemMovimentacaoEstoqueJpaEntity(
                domain.getId(),
                null, // Será setado pela entidade pai (MovimentacaoEstoqueJpaEntity.fromDomain)
                domain.getProdutoId(),
                domain.getQuantidade()
        );
    }

    public ItemMovimentacaoEstoque toDomain() {
        return new ItemMovimentacaoEstoque(
                this.id,
                this.movimentacao != null ? this.movimentacao.getId() : null, // Cuidado com Lazy Loading aqui se for acessado fora de uma transação
                this.produtoId,
                this.quantidade
        );
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public MovimentacaoEstoqueJpaEntity getMovimentacao() { return movimentacao; }
    public void setMovimentacao(MovimentacaoEstoqueJpaEntity movimentacao) { this.movimentacao = movimentacao; }
    public String getProdutoId() { return produtoId; }
    public void setProdutoId(String produtoId) { this.produtoId = produtoId; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    // Removidos Getters e Setters para lote e localização
}