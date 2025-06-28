package com.targosystem.varejo.estoque.application.output;

import com.targosystem.varejo.estoque.domain.model.ItemEstoque;
import java.time.LocalDate;

public record ItemEstoqueOutput(
        String id,
        String produtoId,
        int quantidade,
        String numeroLote,
        LocalDate dataFabricacaoLote,
        LocalDate dataValidadeLote,
        String corredorLocalizacao,
        String prateleiraLocalizacao,
        String nivelLocalizacao,
        String localEstoqueId // NOVO: ID do LocalEstoque
) {
    public static ItemEstoqueOutput fromDomain(ItemEstoque itemEstoque) {
        return new ItemEstoqueOutput(
                itemEstoque.getId(),
                itemEstoque.getProdutoId(),
                itemEstoque.getQuantidade(),
                itemEstoque.getLote() != null ? itemEstoque.getLote().getNumeroLote() : null,
                itemEstoque.getLote() != null ? itemEstoque.getLote().getDataFabricacao() : null,
                itemEstoque.getLote() != null ? itemEstoque.getLote().getDataValidade() : null,
                itemEstoque.getLocalizacao() != null ? itemEstoque.getLocalizacao().getCorredor() : null,
                itemEstoque.getLocalizacao() != null ? itemEstoque.getLocalizacao().getPrateleira() : null,
                itemEstoque.getLocalizacao() != null ? itemEstoque.getLocalizacao().getNivel() : null,
                itemEstoque.getLocalEstoqueId() // NOVO
        );
    }
}