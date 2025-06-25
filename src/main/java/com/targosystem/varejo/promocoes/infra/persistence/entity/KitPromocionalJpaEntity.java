package com.targosystem.varejo.promocoes.infra.persistence.entity;

import com.targosystem.varejo.promocoes.domain.model.KitPromocional;
import com.targosystem.varejo.promocoes.domain.model.ItemKit;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "kits_promocionais")
public class KitPromocionalJpaEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(length = 500) // Aumenta o tamanho da coluna se a descrição for longa
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoFixoKit;

    @ElementCollection // Mapeia uma coleção de objetos de valor
    @CollectionTable(name = "kit_itens", joinColumns = @JoinColumn(name = "kit_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "produtoId", column = @Column(name = "produto_id", nullable = false)),
            @AttributeOverride(name = "quantidade", column = @Column(name = "quantidade", nullable = false))
    })
    private List<ItemKitJpaEmbeddable> itens; // Usaremos uma classe "Embeddable" para ItemKit

    // Construtor padrão JPA
    protected KitPromocionalJpaEntity() {}

    public KitPromocionalJpaEntity(String id, String nome, String descricao, BigDecimal precoFixoKit, List<ItemKitJpaEmbeddable> itens) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.precoFixoKit = precoFixoKit;
        this.itens = itens;
    }

    public static KitPromocionalJpaEntity fromDomain(KitPromocional kit) {
        List<ItemKitJpaEmbeddable> jpaItens = kit.getItens().stream()
                .map(ItemKitJpaEmbeddable::fromDomain)
                .collect(Collectors.toList());
        return new KitPromocionalJpaEntity(
                kit.getId(),
                kit.getNome(),
                kit.getDescricao(),
                kit.getPrecoFixoKit(),
                jpaItens
        );
    }

    public KitPromocional toDomain() {
        List<ItemKit> domainItens = this.itens.stream()
                .map(ItemKitJpaEmbeddable::toDomain)
                .collect(Collectors.toList());
        return new KitPromocional(
                this.id,
                this.nome,
                this.descricao,
                this.precoFixoKit,
                domainItens
        );
    }

    // Getters e Setters para JPA
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPrecoFixoKit() { return precoFixoKit; }
    public void setPrecoFixoKit(BigDecimal precoFixoKit) { this.precoFixoKit = precoFixoKit; }
    public List<ItemKitJpaEmbeddable> getItens() { return itens; }
    public void setItens(List<ItemKitJpaEmbeddable> itens) { this.itens = itens; }
}