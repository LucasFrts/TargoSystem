package com.targosystem.varejo.fornecedores.infra.persistence.entity;

import com.targosystem.varejo.fornecedores.domain.model.EntregaFornecedor;
import com.targosystem.varejo.fornecedores.domain.model.FornecedorId;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "entregas_fornecedores")
public class EntregaFornecedorJpaEntity {

    @Id
    private String id;

    @Column(name = "fornecedor_id", nullable = false)
    private String fornecedorId; // Mapeia o valor do FornecedorId

    @Column(name = "numero_pedido_compra", nullable = false)
    private String numeroPedidoCompra;

    @Column(name = "data_prevista_entrega", nullable = false)
    private LocalDate dataPrevistaEntrega;

    @Column(name = "data_realizacao_entrega")
    private LocalDate dataRealizacaoEntrega;

    @Column(nullable = false)
    private String status;

    @Column(name = "quantidade_itens", nullable = false)
    private int quantidadeItens;

    @Column
    private String observacoes;

    @Column(name = "avaliacao_nota")
    private Integer avaliacaoNota;

    @Column(name = "avaliacao_comentario")
    private String avaliacaoComentario;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    protected EntregaFornecedorJpaEntity() {}

    public EntregaFornecedorJpaEntity(String id, String fornecedorId, String numeroPedidoCompra, LocalDate dataPrevistaEntrega, LocalDate dataRealizacaoEntrega, String status, int quantidadeItens, String observacoes, Integer avaliacaoNota, String avaliacaoComentario, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.fornecedorId = fornecedorId;
        this.numeroPedidoCompra = numeroPedidoCompra;
        this.dataPrevistaEntrega = dataPrevistaEntrega;
        this.dataRealizacaoEntrega = dataRealizacaoEntrega;
        this.status = status;
        this.quantidadeItens = quantidadeItens;
        this.observacoes = observacoes;
        this.avaliacaoNota = avaliacaoNota;
        this.avaliacaoComentario = avaliacaoComentario;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public static EntregaFornecedorJpaEntity fromDomain(EntregaFornecedor entrega) {
        return new EntregaFornecedorJpaEntity(
                entrega.getId(),
                entrega.getFornecedorId().value(), // Mapeia o valor do FornecedorId
                entrega.getNumeroPedidoCompra(),
                entrega.getDataPrevistaEntrega(),
                entrega.getDataRealizacaoEntrega(),
                entrega.getStatus(),
                entrega.getQuantidadeItens(),
                entrega.getObservacoes(),
                entrega.getAvaliacaoNota(),
                entrega.getAvaliacaoComentario(),
                entrega.getDataCriacao(),
                entrega.getDataAtualizacao()
        );
    }

    public EntregaFornecedor toDomain() {
        return new EntregaFornecedor(
                this.id,
                new FornecedorId(this.fornecedorId), // Converte de volta para FornecedorId
                this.numeroPedidoCompra,
                this.dataPrevistaEntrega,
                this.dataRealizacaoEntrega,
                this.status,
                this.quantidadeItens,
                this.observacoes,
                this.avaliacaoNota,
                this.avaliacaoComentario,
                this.dataCriacao,
                this.dataAtualizacao
        );
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(String fornecedorId) { this.fornecedorId = fornecedorId; }
    public String getNumeroPedidoCompra() { return numeroPedidoCompra; }
    public void setNumeroPedidoCompra(String numeroPedidoCompra) { this.numeroPedidoCompra = numeroPedidoCompra; }
    public LocalDate getDataPrevistaEntrega() { return dataPrevistaEntrega; }
    public void setDataPrevistaEntrega(LocalDate dataPrevistaEntrega) { this.dataPrevistaEntrega = dataPrevistaEntrega; }
    public LocalDate getDataRealizacaoEntrega() { return dataRealizacaoEntrega; }
    public void setDataRealizacaoEntrega(LocalDate dataRealizacaoEntrega) { this.dataRealizacaoEntrega = dataRealizacaoEntrega; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getQuantidadeItens() { return quantidadeItens; }
    public void setQuantidadeItens(int quantidadeItens) { this.quantidadeItens = quantidadeItens; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Integer getAvaliacaoNota() { return avaliacaoNota; }
    public void setAvaliacaoNota(Integer avaliacaoNota) { this.avaliacaoNota = avaliacaoNota; }
    public String getAvaliacaoComentario() { return avaliacaoComentario; }
    public void setAvaliacaoComentario(String avaliacaoComentario) { this.avaliacaoComentario = avaliacaoComentario; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}