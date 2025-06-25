package com.targosystem.varejo.produtos.application.input;

import com.targosystem.varejo.shared.domain.Price;

public record CadastrarProdutoInput(
        String nome,
        String descricao,
        String codigoBarras,
        String nomeCategoria, // Nome da categoria para facilitar a entrada
        String descricaoCategoria, // Descrição da categoria, se precisar criar
        String marca,
        Price precoSugerido
) {}