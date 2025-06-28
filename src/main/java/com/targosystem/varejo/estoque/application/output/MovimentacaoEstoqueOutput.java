package com.targosystem.varejo.estoque.application.output;

import com.targosystem.varejo.estoque.domain.model.MovimentacaoEstoque;
import com.targosystem.varejo.estoque.domain.model.TipoMovimentacao;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record MovimentacaoEstoqueOutput(
        String id,
        TipoMovimentacao tipo,
        LocalDateTime dataHora,
        String motivo,
        String localOrigemId,
        String localDestinoId,
        List<ItemMovimentacaoOutput> itens
) {
    public static MovimentacaoEstoqueOutput fromDomain(MovimentacaoEstoque movimentacao) {
        return new MovimentacaoEstoqueOutput(
                movimentacao.getId(),
                movimentacao.getTipo(),
                movimentacao.getDataHora(),
                movimentacao.getMotivo(),
                movimentacao.getLocalOrigemId(),
                movimentacao.getLocalDestinoId(),
                movimentacao.getItens().stream()
                        .map(ItemMovimentacaoOutput::fromDomain)
                        .collect(Collectors.toList())
        );
    }
}