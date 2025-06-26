package com.targosystem.varejo.shared.infra;

import com.targosystem.varejo.shared.domain.DomainEvent;

import java.time.LocalDateTime;
import java.util.List;

public record SolicitacaoReposicaoEstoqueEvent(
        String idVenda,
        List<ItemEstoque> itens,
        LocalDateTime ocorreuEm
) implements DomainEvent {
    public record ItemEstoque(String idProduto, int quantidade) {}

    @Override
    public LocalDateTime getOcorreuEm() {
        return ocorreuEm;
    }
}