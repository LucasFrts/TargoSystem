package com.targosystem.varejo.produtos.infra.persistence.entity;

import com.targosystem.varejo.produtos.domain.model.Produto;
import com.targosystem.varejo.produtos.domain.model.ProdutoId;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "produtos")
public class ProdutoJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "codigo_barras", unique = true, nullable = false, length = 50)
    private String codigoBarras;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false) // Coluna de FK
    private CategoriaJpaEntity categoria; // Mapeamento para a entidade JPA da Categoria

    @Column(length = 100) // Assumindo que marca não é uma entidade complexa
    private String marca; // Novo campo mapeado

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    protected ProdutoJpaEntity() {}

    public ProdutoJpaEntity(String id, String nome, String descricao, BigDecimal precoVenda, String codigoBarras, CategoriaJpaEntity categoria, String marca, String status, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.precoVenda = precoVenda;
        this.codigoBarras = codigoBarras;
        this.categoria = categoria; // Adicionado ao construtor
        this.marca = marca; // Adicionado ao construtor
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public static ProdutoJpaEntity fromDomain(Produto produto) {
        return new ProdutoJpaEntity(
                produto.getId().value(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPrecoVenda(),
                produto.getCodigoBarras(),
                // Converta Categoria do domínio para CategoriaJpaEntity
                CategoriaJpaEntity.fromDomain(produto.getCategoria()),
                produto.getMarca(),
                produto.getStatus(),
                produto.getDataCriacao(),
                produto.getDataAtualizacao()
        );
    }

    public Produto toDomain() {
        return new Produto(
                new ProdutoId(this.id),
                this.nome,
                this.descricao,
                this.precoVenda,
                this.codigoBarras,
                // Converta CategoriaJpaEntity para Categoria do domínio
                this.categoria.toDomain(),
                this.marca,
                this.status,
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
    public BigDecimal getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(BigDecimal precoVenda) { this.precoVenda = precoVenda; }
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    public CategoriaJpaEntity getCategoria() { return categoria; } // Getter para CategoriaJpaEntity
    public void setCategoria(CategoriaJpaEntity categoria) { this.categoria = categoria; } // Setter para CategoriaJpaEntity
    public String getMarca() { return marca; } // Getter para marca
    public void setMarca(String marca) { this.marca = marca; } // Setter para marca
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}