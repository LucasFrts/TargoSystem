package com.targosystem.varejo.promocoes.application.input;

import java.math.BigDecimal;
import java.util.List;

// Representa um item dentro do kit para o input
public record CriarKitPromocionalInput(
        String nome,
        String descricao,
        BigDecimal precoFixoKit,
        List<ItemKitInput> itens
) {}