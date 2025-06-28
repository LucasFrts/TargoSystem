package com.targosystem.varejo.estoque.infra.persistence.entity;

import com.targosystem.varejo.estoque.domain.model.Estoque;
import com.targosystem.varejo.estoque.domain.model.ItemEstoque;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "estoques")
public class EstoqueJpaEntity {

    @Id
    private String id;

    @Column(name = "produto_id", nullable = false)
    private String produtoId;

    // NEW: Referência ao LocalEstoque a que este estoque pertence
    // Um Estoque (do Produto X) existe em um LocalEstoque (Loja A ou Depósito B)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_estoque_id", nullable = false)
    private LocalEstoqueJpaEntity localEstoque;

    @OneToMany(mappedBy = "estoque", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemEstoqueJpaEntity> itensEstoque = new ArrayList<>();

    protected EstoqueJpaEntity() {}

    // CONSTRUTOR ATUALIZADO
    public EstoqueJpaEntity(String id, String produtoId, LocalEstoqueJpaEntity localEstoque, List<ItemEstoqueJpaEntity> itensEstoque) {
        this.id = id;
        this.produtoId = produtoId;
        this.localEstoque = localEstoque;
        this.itensEstoque = (itensEstoque != null) ? new ArrayList<>(itensEstoque) : new ArrayList<>();
    }

    // fromDomain ATUALIZADO
    public static EstoqueJpaEntity fromDomain(Estoque estoque) {
        EstoqueJpaEntity entity = new EstoqueJpaEntity(
                estoque.getId(),
                estoque.getProdutoId(),
                LocalEstoqueJpaEntity.fromDomain(estoque.getLocalEstoque()), // Converte e seta o local
                null // Não passar listas aqui, elas serão preenchidas abaixo para setar a referência ao pai
        );

        List<ItemEstoqueJpaEntity> jpaItens = estoque.getItensEstoque().stream()
                .map(ItemEstoqueJpaEntity::fromDomain)
                .collect(Collectors.toList());
        jpaItens.forEach(item -> item.setEstoque(entity)); // Garante a ligação com o pai
        entity.setItensEstoque(jpaItens); // Usa o setter

        // REMOVIDO: Movimentações não mais aqui
        // List<MovimentacaoEstoqueJpaEntity> jpaMovs = estoque.getMovimentacoes().stream()
        //         .map(MovimentacaoEstoqueJpaEntity::fromDomain)
        //         .collect(Collectors.toList());
        // jpaMovs.forEach(mov -> mov.setEstoque(entity)); // Garante a ligação com o pai
        // entity.setMovimentacoes(jpaMovs); // Usa o setter

        return entity;
    }

    // toDomain ATUALIZADO
    public Estoque toDomain() {
        List<ItemEstoque> domainItens = this.itensEstoque.stream()
                .map(ItemEstoqueJpaEntity::toDomain)
                .collect(Collectors.toList());

        // REMOVIDO: Movimentações não mais aqui
        // List<MovimentacaoEstoque> domainMovs = this.movimentacoes.stream()
        //         .map(MovimentacaoEstoqueJpaEntity::toDomain)
        //         .collect(Collectors.toList());

        return new Estoque(this.id, this.produtoId, this.localEstoque.toDomain(), domainItens); // ATUALIZADO: Inclui LocalEstoque
    }

    // Getters e Setters (VERIFIQUE SE ESTÃO TODOS PRESENTES)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProdutoId() { return produtoId; }
    public void setProdutoId(String produtoId) { this.produtoId = produtoId; }

    public LocalEstoqueJpaEntity getLocalEstoque() { return localEstoque; } // NOVO GETTER
    public void setLocalEstoque(LocalEstoqueJpaEntity localEstoque) { this.localEstoque = localEstoque; } // NOVO SETTER

    // GETTERS E SETTERS PARA AS COLEÇÕES - ESTES SÃO CRUCIAIS!
    public List<ItemEstoqueJpaEntity> getItensEstoque() {
        return itensEstoque;
    }
    public void setItensEstoque(List<ItemEstoqueJpaEntity> itensEstoque) {
        this.itensEstoque.clear();
        if (itensEstoque != null) {
            this.itensEstoque.addAll(itensEstoque);
        }
    }
}