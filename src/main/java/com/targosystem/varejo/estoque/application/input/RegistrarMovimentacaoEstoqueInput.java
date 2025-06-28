package com.targosystem.varejo.estoque.application.input;

import com.targosystem.varejo.estoque.domain.model.TipoMovimentacao;

import java.time.LocalDate; // Keep for other potential date usage, though not directly used for lote here
import java.util.List;
import java.util.Objects; // Added for Objects.requireNonNull

public record RegistrarMovimentacaoEstoqueInput(
        TipoMovimentacao tipoMovimentacao,
        String localOrigemId,
        String localDestinoId,
        List<ItemMovimentacaoInput> itens,
        String motivo
) {
    public RegistrarMovimentacaoEstoqueInput {
        Objects.requireNonNull(tipoMovimentacao, "Tipo de movimentação não pode ser nulo.");
        Objects.requireNonNull(localOrigemId, "Local de origem não pode ser nulo.");
        Objects.requireNonNull(localDestinoId, "Local de destino não pode ser nulo.");
        Objects.requireNonNull(itens, "A lista de itens não pode ser nula.");
    }
}