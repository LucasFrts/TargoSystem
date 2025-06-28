package com.targosystem.varejo.estoque.application.output;

import com.targosystem.varejo.estoque.domain.model.Estoque;
import com.targosystem.varejo.estoque.domain.model.ItemEstoque;
import java.util.List;
import java.util.stream.Collectors;

public record EstoqueOutput(
        String id,
        String produtoId,
        String localEstoqueId, // NOVO: ID do LocalEstoque
        String nomeLocalEstoque, // NOVO: Nome do LocalEstoque
        long quantidadeTotalDisponivel,
        List<ItemEstoqueOutput> itensEstoque
) {
    public static EstoqueOutput fromDomain(Estoque estoque) {
        return new EstoqueOutput(
                estoque.getId(),
                estoque.getProdutoId(),
                estoque.getLocalEstoque().getId(), // Pega o ID do LocalEstoque
                estoque.getLocalEstoque().getNome(), // Pega o Nome do LocalEstoque
                estoque.getQuantidadeTotalDisponivel(),
                estoque.getItensEstoque().stream()
                        .map(ItemEstoqueOutput::fromDomain)
                        .collect(Collectors.toList())
                // REMOVIDA: estoque.getMovimentacoes().stream().map(MovimentacaoEstoqueOutput::fromDomain).collect(Collectors.toList())
        );
    }
}