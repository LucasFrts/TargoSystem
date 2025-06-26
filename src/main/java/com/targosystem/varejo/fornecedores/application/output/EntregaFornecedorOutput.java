package com.targosystem.varejo.fornecedores.application.output;

import com.targosystem.varejo.fornecedores.domain.model.EntregaFornecedor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EntregaFornecedorOutput(
        String id,
        String fornecedorId,
        String numeroPedidoCompra,
        LocalDate dataPrevistaEntrega,
        LocalDate dataRealizacaoEntrega,
        String status,
        int quantidadeItens,
        String observacoes,
        Integer avaliacaoNota,
        String avaliacaoComentario,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
    public static EntregaFornecedorOutput fromDomain(EntregaFornecedor entrega) {
        return new EntregaFornecedorOutput(
                entrega.getId(),
                entrega.getFornecedorId().value(),
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
}