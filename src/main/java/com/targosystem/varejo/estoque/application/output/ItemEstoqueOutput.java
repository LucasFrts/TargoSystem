package com.targosystem.varejo.estoque.application.output;

import com.targosystem.varejo.estoque.domain.model.ItemEstoque;

record ItemEstoqueOutput(
        String id,
        String produtoId,
        int quantidade,
        LoteOutput lote,
        LocalizacaoArmazenamentoOutput localizacao
) {
    static ItemEstoqueOutput fromDomain(ItemEstoque item) {
        return new ItemEstoqueOutput(
                item.getId(),
                item.getProdutoId(),
                item.getQuantidade(),
                LoteOutput.fromDomain(item.getLote()),
                LocalizacaoArmazenamentoOutput.fromDomain(item.getLocalizacao())
        );
    }
}
