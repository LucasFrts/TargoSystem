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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_estoque_id", nullable = false)
    private LocalEstoqueJpaEntity localEstoque;

    @OneToMany(mappedBy = "estoque", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemEstoqueJpaEntity> itensEstoque = new ArrayList<>();

    protected EstoqueJpaEntity() {}

    public EstoqueJpaEntity(String id, String produtoId, LocalEstoqueJpaEntity localEstoque, List<ItemEstoqueJpaEntity> itensEstoque) {
        this.id = id;
        this.produtoId = produtoId;
        this.localEstoque = localEstoque;
        this.itensEstoque = (itensEstoque != null) ? new ArrayList<>(itensEstoque) : new ArrayList<>();
    }

    public static EstoqueJpaEntity fromDomain(Estoque estoque) {
        EstoqueJpaEntity entity = new EstoqueJpaEntity(
                estoque.getId(),
                estoque.getProdutoId(),
                LocalEstoqueJpaEntity.fromDomain(estoque.getLocalEstoque()),
                null
        );

        List<ItemEstoqueJpaEntity> jpaItens = estoque.getItensEstoque().stream()
                .map(ItemEstoqueJpaEntity::fromDomain)
                .collect(Collectors.toList());
        jpaItens.forEach(item -> item.setEstoque(entity));
        entity.setItensEstoque(jpaItens);

        return entity;
    }

    // toDomain ATUALIZADO
    public Estoque toDomain() {
        List<ItemEstoque> domainItens = this.itensEstoque.stream()
                .map(ItemEstoqueJpaEntity::toDomain)
                .collect(Collectors.toList());

        return new Estoque(this.id, this.produtoId, this.localEstoque.toDomain(), domainItens);
    }

    // Getters e Setters (VERIFIQUE SE ESTÃO TODOS PRESENTES)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProdutoId() { return produtoId; }
    public void setProdutoId(String produtoId) { this.produtoId = produtoId; }

    public LocalEstoqueJpaEntity getLocalEstoque() { return localEstoque; }
    public void setLocalEstoque(LocalEstoqueJpaEntity localEstoque) { this.localEstoque = localEstoque; }

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