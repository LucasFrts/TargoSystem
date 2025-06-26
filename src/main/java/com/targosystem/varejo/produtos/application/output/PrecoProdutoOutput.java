package com.targosystem.varejo.produtos.application.output;

import java.math.BigDecimal;
import java.util.Objects;

public record PrecoProdutoOutput(
        String idProduto,
        BigDecimal preco
) {
    public PrecoProdutoOutput {
        Objects.requireNonNull(idProduto, "ID do produto não pode ser nulo.");
        Objects.requireNonNull(preco, "Preço do produto não pode ser nulo.");
        if (preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço do produto não pode ser negativo.");
        }
    }
}