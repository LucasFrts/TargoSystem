package com.targosystem.varejo.estoque.application.input;

import java.time.LocalDate;
import java.util.Objects;

public record ItemMovimentacaoInput(
        String produtoId,
        int quantidade,
        String numeroLote,
        LocalDate dataFabricacaoLote,
        LocalDate dataValidadeLote,
        String corredor,
        String prateleira,
        String nivel
) {
    public ItemMovimentacaoInput {
        Objects.requireNonNull(produtoId, "Produto ID do item de movimentação não pode ser nulo.");
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade do item de movimentação deve ser positiva.");
        }
    }
}