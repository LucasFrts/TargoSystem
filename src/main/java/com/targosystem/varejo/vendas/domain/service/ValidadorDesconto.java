package com.targosystem.varejo.vendas.domain.service;

import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.vendas.domain.model.Venda;

import java.math.BigDecimal;

public class ValidadorDesconto {

    /**
     * Valida se um desconto pode ser aplicado a uma venda.
     * @param venda A venda à qual o desconto será aplicado.
     * @param valorDesconto O valor do desconto proposto.
     */
    public void validar(Venda venda, BigDecimal valorDesconto) {
        if (valorDesconto == null || valorDesconto.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("O valor do desconto não pode ser nulo ou negativo.");
        }
        if (valorDesconto.compareTo(venda.getValorTotal()) > 0) {
            throw new DomainException("O desconto não pode ser maior que o valor total da venda.");
        }
        if (!venda.getStatus().equals("PENDENTE")) {
            throw new DomainException("Não é possível aplicar desconto em uma venda que não está PENDENTE.");
        }
        // Poderiam existir outras regras, como limite máximo de desconto por tipo de produto, etc.
    }
}