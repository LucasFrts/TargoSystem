package com.targosystem.varejo.estoque.infra.persistence.entity;

import com.targosystem.varejo.estoque.domain.model.Estoque;
import com.targosystem.varejo.estoque.domain.model.ItemEstoque;
import com.targosystem.varejo.estoque.domain.model.MovimentacaoEstoque;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "estoques")
public class EstoqueJpaEntity {

    @Id
    private String id;

    @Column(name = "produto_id", nullable = false, unique = true)
    private String produtoId;

    @OneToMany(mappedBy = "estoque", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemEstoqueJpaEntity> itensEstoque = new ArrayList<>();

    @OneToMany(mappedBy = "estoque", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MovimentacaoEstoqueJpaEntity> movimentacoes = new ArrayList<>();

    protected EstoqueJpaEntity() {}

    public EstoqueJpaEntity(String id, String produtoId, List<ItemEstoqueJpaEntity> itensEstoque, List<MovimentacaoEstoqueJpaEntity> movimentacoes) {
        this.id = id;
        this.produtoId = produtoId;
        // Certifique-se de inicializar as listas para evitar NullPointerExceptions
        this.itensEstoque = (itensEstoque != null) ? new ArrayList<>(itensEstoque) : new ArrayList<>();
        this.movimentacoes = (movimentacoes != null) ? new ArrayList<>(movimentacoes) : new ArrayList<>();
    }

    public static EstoqueJpaEntity fromDomain(Estoque estoque) {
        EstoqueJpaEntity entity = new EstoqueJpaEntity(
                estoque.getId(),
                estoque.getProdutoId(),
                null, // Não passar listas aqui, elas serão preenchidas abaixo para setar a referência ao pai
                null
        );

        List<ItemEstoqueJpaEntity> jpaItens = estoque.getItensEstoque().stream()
                .map(ItemEstoqueJpaEntity::fromDomain)
                .collect(Collectors.toList());
        jpaItens.forEach(item -> item.setEstoque(entity)); // Garante a ligação com o pai
        entity.setItensEstoque(jpaItens); // Usa o setter

        List<MovimentacaoEstoqueJpaEntity> jpaMovs = estoque.getMovimentacoes().stream()
                .map(MovimentacaoEstoqueJpaEntity::fromDomain)
                .collect(Collectors.toList());
        jpaMovs.forEach(mov -> mov.setEstoque(entity)); // Garante a ligação com o pai
        entity.setMovimentacoes(jpaMovs); // Usa o setter

        return entity;
    }

    public Estoque toDomain() {
        List<ItemEstoque> domainItens = this.itensEstoque.stream()
                .map(ItemEstoqueJpaEntity::toDomain)
                .collect(Collectors.toList());

        List<MovimentacaoEstoque> domainMovs = this.movimentacoes.stream()
                .map(MovimentacaoEstoqueJpaEntity::toDomain)
                .collect(Collectors.toList());

        return new Estoque(this.id, this.produtoId, domainItens, domainMovs);
    }

    // Getters e Setters (VERIFIQUE SE ESTÃO TODOS PRESENTES)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProdutoId() { return produtoId; }
    public void setProdutoId(String produtoId) { this.produtoId = produtoId; }

    // GETTERS E SETTERS PARA AS COLEÇÕES - ESTES SÃO CRUCIAIS!
    public List<ItemEstoqueJpaEntity> getItensEstoque() {
        return itensEstoque;
    }
    public void setItensEstoque(List<ItemEstoqueJpaEntity> itensEstoque) {
        // Limpa e adiciona para garantir que o JPA entenda as mudanças
        this.itensEstoque.clear();
        if (itensEstoque != null) {
            this.itensEstoque.addAll(itensEstoque);
        }
    }

    public List<MovimentacaoEstoqueJpaEntity> getMovimentacoes() {
        return movimentacoes;
    }
    public void setMovimentacoes(List<MovimentacaoEstoqueJpaEntity> movimentacoes) {
        // Limpa e adiciona para garantir que o JPA entenda as mudanças
        this.movimentacoes.clear();
        if (movimentacoes != null) {
            this.movimentacoes.addAll(movimentacoes);
        }
    }
}