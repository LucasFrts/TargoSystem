package com.targosystem.varejo.produtos.application.input;

import com.targosystem.varejo.produtos.domain.model.ProdutoId;
import com.targosystem.varejo.shared.domain.Price;

public record AtualizarProdutoInput(
        ProdutoId id,
        String nome,
        String descricao,
        String codigoBarras,
        String nomeCategoria,
        String descricaoCategoria,
        String marca,
        Price precoSugerido
) {}