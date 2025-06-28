package com.targosystem.varejo.estoque.application.output;

import com.targosystem.varejo.estoque.domain.model.ItemMovimentacaoEstoque;
// import java.time.LocalDate; // Nao e mais necessario aqui

public record ItemMovimentacaoOutput(
        String id,
        String produtoId,
        int quantidade
) {
    public static ItemMovimentacaoOutput fromDomain(ItemMovimentacaoEstoque item) {
        return new ItemMovimentacaoOutput(
                item.getId(),
                item.getProdutoId(),
                item.getQuantidade()
        );
    }
}