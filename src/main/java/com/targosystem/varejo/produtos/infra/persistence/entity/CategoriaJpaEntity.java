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
    private String id;

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

    /**
     * Converte um objeto de domínio Categoria para uma CategoriaJpaEntity.
     * O ID da entidade JPA será o ID da Categoria de domínio.
     * Assumimos que o ID da categoria de domínio nunca é nulo.
     * @param categoria A entidade de domínio Categoria.
     * @return A CategoriaJpaEntity correspondente.
     */
    public static CategoriaJpaEntity fromDomain(Categoria categoria) {
        return new CategoriaJpaEntity(
                categoria.getId().value(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.getDataCriacao(),
                categoria.getDataAtualizacao()
        );
    }

    public Categoria toDomain() {
        return new Categoria(
                new CategoriaId(this.id),
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoriaJpaEntity that = (CategoriaJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CategoriaJpaEntity{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataAtualizacao=" + dataAtualizacao +
                '}';
    }
}