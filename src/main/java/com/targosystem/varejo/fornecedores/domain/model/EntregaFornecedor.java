package com.targosystem.varejo.fornecedores.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class EntregaFornecedor {

    private String id;
    private FornecedorId fornecedorId;
    private String numeroPedidoCompra;
    private LocalDate dataPrevistaEntrega;
    private LocalDate dataRealizacaoEntrega;
    private String status;
    private int quantidadeItens;
    private String observacoes;
    private Integer avaliacaoNota;
    private String avaliacaoComentario;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public EntregaFornecedor(FornecedorId fornecedorId, String numeroPedidoCompra, LocalDate dataPrevistaEntrega, int quantidadeItens, String observacoes) {
        if (fornecedorId == null) {
            throw new DomainException("FornecedorId é obrigatório para registrar uma entrega.");
        }
        if (numeroPedidoCompra == null || numeroPedidoCompra.trim().isEmpty()) {
            throw new DomainException("Número do pedido de compra é obrigatório.");
        }
        if (dataPrevistaEntrega == null) {
            throw new DomainException("Data prevista de entrega é obrigatória.");
        }
        if (quantidadeItens <= 0) {
            throw new DomainException("Quantidade de itens deve ser positiva.");
        }

        this.id = UUID.randomUUID().toString();
        this.fornecedorId = fornecedorId;
        this.numeroPedidoCompra = numeroPedidoCompra;
        this.dataPrevistaEntrega = dataPrevistaEntrega;
        this.quantidadeItens = quantidadeItens;
        this.observacoes = observacoes;
        this.status = "PENDENTE"; // Status inicial
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor para reconstruir da persistência
    public EntregaFornecedor(String id, FornecedorId fornecedorId, String numeroPedidoCompra, LocalDate dataPrevistaEntrega, LocalDate dataRealizacaoEntrega, String status, int quantidadeItens, String observacoes, Integer avaliacaoNota, String avaliacaoComentario, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = Objects.requireNonNull(id, "ID da entrega não pode ser nulo.");
        this.fornecedorId = Objects.requireNonNull(fornecedorId, "FornecedorId da entrega não pode ser nulo.");
        this.numeroPedidoCompra = Objects.requireNonNull(numeroPedidoCompra, "Número do pedido de compra não pode ser nulo.");
        this.dataPrevistaEntrega = Objects.requireNonNull(dataPrevistaEntrega, "Data prevista de entrega não pode ser nula.");
        this.dataRealizacaoEntrega = dataRealizacaoEntrega; // Pode ser nulo
        this.status = Objects.requireNonNull(status, "Status da entrega não pode ser nulo.");
        this.quantidadeItens = quantidadeItens;
        this.observacoes = observacoes;
        this.avaliacaoNota = avaliacaoNota;
        this.avaliacaoComentario = avaliacaoComentario;
        this.dataCriacao = Objects.requireNonNull(dataCriacao, "Data de criação da entrega não pode ser nula.");
        this.dataAtualizacao = Objects.requireNonNull(dataAtualizacao, "Data de atualização da entrega não pode ser nula.");
    }

    // Comportamentos
    public void registrarRecebimento(LocalDate dataRealizacao) {
        if (this.status.equals("ENTREGUE") || this.status.equals("CANCELADA")) {
            throw new DomainException("Não é possível registrar recebimento para uma entrega com status " + this.status);
        }
        if (dataRealizacao == null) {
            throw new DomainException("Data de realização da entrega não pode ser nula.");
        }
        this.dataRealizacaoEntrega = dataRealizacao;
        this.status = "ENTREGUE";
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void avaliar(int nota, String comentario) {
        if (!this.status.equals("ENTREGUE")) {
            throw new DomainException("Somente entregas com status 'ENTREGUE' podem ser avaliadas.");
        }
        if (nota < 1 || nota > 5) {
            throw new DomainException("A nota da avaliação deve ser entre 1 e 5.");
        }
        this.avaliacaoNota = nota;
        this.avaliacaoComentario = comentario;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void cancelar(String motivo) {
        if (this.status.equals("ENTREGUE")) {
            throw new DomainException("Não é possível cancelar uma entrega que já foi realizada.");
        }
        this.status = "CANCELADA";
        this.observacoes = "CANCELADA: " + motivo + (this.observacoes != null ? " | Original: " + this.observacoes : "");
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public FornecedorId getFornecedorId() { return fornecedorId; }
    public String getNumeroPedidoCompra() { return numeroPedidoCompra; }
    public LocalDate getDataPrevistaEntrega() { return dataPrevistaEntrega; }
    public LocalDate getDataRealizacaoEntrega() { return dataRealizacaoEntrega; }
    public String getStatus() { return status; }
    public int getQuantidadeItens() { return quantidadeItens; }
    public String getObservacoes() { return observacoes; }
    public Integer getAvaliacaoNota() { return avaliacaoNota; }
    public String getAvaliacaoComentario() { return avaliacaoComentario; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntregaFornecedor that = (EntregaFornecedor) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}