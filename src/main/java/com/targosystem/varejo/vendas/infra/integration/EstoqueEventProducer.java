package com.targosystem.varejo.vendas.infra.integration;

import com.targosystem.varejo.shared.infra.EventPublisher;
import com.targosystem.varejo.shared.infra.SolicitacaoBaixaEstoqueEvent;
import com.targosystem.varejo.shared.infra.SolicitacaoReposicaoEstoqueEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EstoqueEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(EstoqueEventProducer.class);

    private final EventPublisher eventPublisher;

    public EstoqueEventProducer(EventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public void sendEstoqueBaixaEvent(String idVenda, List<ProdutoParaEstoqueInfo> produtosParaBaixa) {

        List<SolicitacaoBaixaEstoqueEvent.ItemEstoque> itensBaixa = produtosParaBaixa.stream()
                .map(info -> new SolicitacaoBaixaEstoqueEvent.ItemEstoque(info.idProduto(), info.quantidade()))
                .collect(Collectors.toList());

        SolicitacaoBaixaEstoqueEvent event = new SolicitacaoBaixaEstoqueEvent(idVenda, itensBaixa, LocalDateTime.now());
        eventPublisher.publish(event);
        logger.info("Evento SolicitacaoBaixaEstoqueEvent publicado para venda ID: {}", idVenda);
    }

    public void sendEstoqueReposicaoEvent(String idVenda, List<ProdutoParaEstoqueInfo> produtosParaReposicao) {

        List<SolicitacaoReposicaoEstoqueEvent.ItemEstoque> itensReposicao = produtosParaReposicao.stream()
                .map(info -> new SolicitacaoReposicaoEstoqueEvent.ItemEstoque(info.idProduto(), info.quantidade()))
                .collect(Collectors.toList());

        SolicitacaoReposicaoEstoqueEvent event = new SolicitacaoReposicaoEstoqueEvent(idVenda, itensReposicao, LocalDateTime.now());
        eventPublisher.publish(event);
        logger.info("Evento SolicitacaoReposicaoEstoqueEvent publicado para venda ID: {}", idVenda);
    }
}