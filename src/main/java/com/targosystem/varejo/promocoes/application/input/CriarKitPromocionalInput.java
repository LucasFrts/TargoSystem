package com.targosystem.varejo.promocoes.application.input;

import java.math.BigDecimal;
import java.util.List;

public record CriarKitPromocionalInput(
        String nome,
        String descricao,
        BigDecimal precoFixoKit,
        List<KitItemInput> itens
) {}