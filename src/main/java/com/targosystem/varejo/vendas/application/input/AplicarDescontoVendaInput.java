package com.targosystem.varejo.vendas.application.input;

import java.math.BigDecimal;

public record AplicarDescontoVendaInput(
        String idVenda,
        BigDecimal valorDesconto
) {}