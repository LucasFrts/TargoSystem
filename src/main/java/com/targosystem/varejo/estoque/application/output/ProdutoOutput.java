package com.targosystem.varejo.estoque.application.output;

import java.math.BigDecimal; // Mantenha se ainda for usado em outros contextos ou para parsing

public record ProdutoOutput(
        String id,
        String nome,
        String descricao,
        String precoVenda,
        String codigoBarras,
        String categoriaNome,
        String marca,
        boolean ativo,
        long quantidadeDisponivel
) {
    public static ProdutoOutput fromProdutoServiceOutput(com.targosystem.varejo.produtos.application.output.ProdutoOutput produtoServiceOutput) {
        return new ProdutoOutput(
                produtoServiceOutput.id(),
                produtoServiceOutput.nome(),
                produtoServiceOutput.descricao(),
                produtoServiceOutput.precoSugerido(), // Usando precoSugerido do produtoServiceOutput
                produtoServiceOutput.codigoBarras(),
                produtoServiceOutput.categoriaNome(),
                produtoServiceOutput.marca(),
                produtoServiceOutput.ativo(),
                0 // Valor padrão, será preenchido pelo Controller
        );
    }
}