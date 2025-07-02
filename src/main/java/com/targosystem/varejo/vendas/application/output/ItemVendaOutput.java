package com.targosystem.varejo.vendas.application.output;

import com.targosystem.varejo.vendas.domain.model.ItemVenda;

public record ItemVendaOutput(
        String id,
        String idProduto,
        String nomeProduto,
        int quantidade,
        String precoUnitario,
        String precoTotal
) {
    public static ItemVendaOutput from(ItemVenda itemVenda) {
        return new ItemVendaOutput(
                itemVenda.getId().value(),
                itemVenda.getIdProduto().value(),
                itemVenda.getNomeProduto(),
                itemVenda.getQuantidade(),
                itemVenda.getPrecoUnitario().toPlainString(),
                itemVenda.getPrecoTotal().toPlainString()
        );
    }
}