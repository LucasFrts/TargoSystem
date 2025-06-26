// src/main/java/com/targosystem/varejo/estoque/application/output/EstoqueOutput.java
package com.targosystem.varejo.estoque.application.output;

import com.targosystem.varejo.estoque.domain.model.Estoque;
import java.util.List;
import java.util.stream.Collectors;

public record EstoqueOutput(
        String id,
        String produtoId,
        int quantidadeTotalDisponivel,
        List<ItemEstoqueOutput> itensEstoque,
        List<MovimentacaoEstoqueOutput> movimentacoes
) {
    public static EstoqueOutput fromDomain(Estoque estoque) {
        List<ItemEstoqueOutput> itemOutputs = estoque.getItensEstoque().stream()
                .map(ItemEstoqueOutput::fromDomain)
                .collect(Collectors.toList());

        List<MovimentacaoEstoqueOutput> movOutputs = estoque.getMovimentacoes().stream()
                .map(MovimentacaoEstoqueOutput::fromDomain)
                .collect(Collectors.toList());

        return new EstoqueOutput(
                estoque.getId(),
                estoque.getProdutoId(),
                estoque.getQuantidadeTotalDisponivel(),
                itemOutputs,
                movOutputs
        );
    }
}