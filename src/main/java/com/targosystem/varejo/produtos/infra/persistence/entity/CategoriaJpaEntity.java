package com.targosystem.varejo.produtos.infra.persistence.entity;

import com.targosystem.varejo.produtos.domain.model.Categoria;
import com.targosystem.varejo.produtos.domain.model.CategoriaId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "categorias")
public class CategoriaJpaEntity {

    @Id
    private String id; // O ID deve ser String para corresponder a CategoriaId

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    protected CategoriaJpaEntity() {}

    public CategoriaJpaEntity(String id, String nome, String descricao, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public static CategoriaJpaEntity fromDomain(Categoria categoria) {
        // Cuidado: Se o ID da categoria for nulo (nova categoria), o ID da entidade será nulo também.
        // O JPA gerará o ID no persist.
        return new CategoriaJpaEntity(
                categoria.getId() != null ? categoria.getId().value() : null, // Mapeia para String do ProdutoId
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.getDataCriacao(),
                categoria.getDataAtualizacao()
        );
    }

    public Categoria toDomain() {
        return new Categoria(
                new CategoriaId(this.id), // Constrói CategoriaId a partir da String
                this.nome,
                this.descricao,
                this.dataCriacao,
                this.dataAtualizacao
        );
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}