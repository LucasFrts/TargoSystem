package com.targosystem.varejo.estoque.infra.persistence.entity;

import com.targosystem.varejo.estoque.domain.model.LocalEstoque;
import com.targosystem.varejo.estoque.domain.model.TipoLocal;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "locais_estoque")
public class LocalEstoqueJpaEntity {

    @Id
    private String id; // Pode ser UUID gerado ou o ID de um Fornecedor/Cliente

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoLocal tipo; // INTERNO, FORNECEDOR, CLIENTE

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    protected LocalEstoqueJpaEntity() {}

    public LocalEstoqueJpaEntity(String id, String nome, TipoLocal tipo, boolean ativo, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public static LocalEstoqueJpaEntity fromDomain(LocalEstoque domain) {
        return new LocalEstoqueJpaEntity(
                domain.getId(),
                domain.getNome(),
                domain.getTipo(),
                domain.isAtivo(),
                domain.getDataCriacao(),
                domain.getDataAtualizacao()
        );
    }

    public LocalEstoque toDomain() {
        return new LocalEstoque(
                this.id,
                this.nome,
                this.tipo,
                this.ativo,
                this.dataCriacao,
                this.dataAtualizacao
        );
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TipoLocal getTipo() { return tipo; }
    public void setTipo(TipoLocal tipo) { this.tipo = tipo; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}