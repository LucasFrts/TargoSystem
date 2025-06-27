package com.targosystem.varejo.promocoes.application.output;

import com.targosystem.varejo.produtos.application.output.ProdutoOutput;
import com.targosystem.varejo.promocoes.domain.model.ItemKit;

import java.util.Objects;

public record KitItemOutput(
        String produtoId,
        String nomeProduto, // Adicionado nome do produto para exibição (pode ser null se não buscado)
        int quantidade
) {
    // Construtor para quando nao se tem o ProdutoOutput (usado nos UseCases de escrita)
    public KitItemOutput(String produtoId, int quantidade) {
        this(produtoId, null, quantidade); // Nome do produto é null por padrao
    }

    // Construtor principal para criar ItemKitOutput com nome do produto
    // Usado pelas Queries que buscam detalhes do produto
    public static KitItemOutput from(ItemKit itemKit, ProdutoOutput produtoOutput) {
        Objects.requireNonNull(itemKit, "ItemKit domain object cannot be null.");
        // produtoOutput pode ser nulo se o ProdutoService retornar null, entao checamos
        return new KitItemOutput(
                itemKit.getProdutoId(),
                (produtoOutput != null) ? produtoOutput.nome() : "Produto Desconhecido", // Usa o nome do produto do ProdutoOutput ou um padrão
                itemKit.getQuantidade()
        );
    }

    // NOVO: from sem ProdutoOutput para uso em UseCases de escrita (quando o nome do produto nao e' necessario)
    public static KitItemOutput from(ItemKit itemKit) {
        Objects.requireNonNull(itemKit, "ItemKit domain object cannot be null.");
        return new KitItemOutput(itemKit.getProdutoId(), null, itemKit.getQuantidade()); // Nome do produto eh null
    }
}
