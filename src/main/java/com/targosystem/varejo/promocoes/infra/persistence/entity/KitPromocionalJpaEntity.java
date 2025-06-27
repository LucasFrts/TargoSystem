package com.targosystem.varejo.promocoes.infra.persistence.entity;

import com.targosystem.varejo.promocoes.domain.model.KitPromocional;
import com.targosystem.varejo.promocoes.domain.model.ItemKit; // Certifique-se que ItemKit está correto
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoFixoKit;

    @ElementCollection
    @CollectionTable(name = "kit_itens", joinColumns = @JoinColumn(name = "kit_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "produtoId", column = @Column(name = "produto_id", nullable = false)),
            @AttributeOverride(name = "quantidade", column = @Column(name = "quantidade", nullable = false))
    })
    private List<ItemKitJpaEmbeddable> itens; // Usaremos uma classe "Embeddable" para ItemKit

    // NOVOS CAMPOS PARA DATAS
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    // Construtor padrão JPA
    protected KitPromocionalJpaEntity() {}

    // Construtor completo ajustado para incluir as datas
    public KitPromocionalJpaEntity(String id, String nome, String descricao, BigDecimal precoFixoKit,
                                   List<ItemKitJpaEmbeddable> itens,
                                   LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.precoFixoKit = precoFixoKit;
        this.itens = itens;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    // Método fromDomain ajustado para usar as datas do objeto de domínio
    public static KitPromocionalJpaEntity fromDomain(KitPromocional kit) {
        List<ItemKitJpaEmbeddable> jpaItens = kit.getItens().stream()
                .map(ItemKitJpaEmbeddable::fromDomain)
                .collect(Collectors.toList());
        return new KitPromocionalJpaEntity(
                kit.getId(),
                kit.getNome(),
                kit.getDescricao(),
                kit.getPrecoFixoKit(),
                jpaItens,
                kit.getDataCriacao(),
                kit.getDataAtualizacao()
        );
    }

    // Método toDomain ajustado para chamar o construtor completo de KitPromocional
    public KitPromocional toDomain() {
        List<ItemKit> domainItens = this.itens.stream()
                .map(ItemKitJpaEmbeddable::toDomain)
                .collect(Collectors.toList());
        return new KitPromocional(
                this.id,
                this.nome,
                this.descricao,
                this.precoFixoKit,
                domainItens,
                this.dataCriacao,
                this.dataAtualizacao
        );
    }

    // Getters e Setters para JPA (incluir os novos campos)
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

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}