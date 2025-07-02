package com.targosystem.varejo.promocoes.application.output;

import com.targosystem.varejo.produtos.application.output.ProdutoOutput;
import com.targosystem.varejo.promocoes.domain.model.ItemKit;

import java.util.Objects;

public record KitItemOutput(
        String produtoId,
        String nomeProduto,
        int quantidade
) {
    public KitItemOutput(String produtoId, int quantidade) {
        this(produtoId, null, quantidade); // Nome do produto é null por padrao
    }

    public static KitItemOutput from(ItemKit itemKit, ProdutoOutput produtoOutput) {
        Objects.requireNonNull(itemKit, "ItemKit domain object cannot be null.");
        return new KitItemOutput(
                itemKit.getProdutoId(),
                (produtoOutput != null) ? produtoOutput.nome() : "Produto Desconhecido",
                itemKit.getQuantidade()
        );
    }

    public static KitItemOutput from(ItemKit itemKit) {
        Objects.requireNonNull(itemKit, "ItemKit domain object cannot be null.");
        return new KitItemOutput(itemKit.getProdutoId(), null, itemKit.getQuantidade()); // Nome do produto eh null
    }
}
