package com.targosystem.varejo.vendas.domain.service;

import com.targosystem.varejo.vendas.domain.model.ItemVenda;
import com.targosystem.varejo.vendas.domain.model.Venda;

import java.math.BigDecimal;
import java.util.List;

public class CalculadorPrecoVenda {

    /**
     * Calcula o valor total de uma lista de itens de venda.
     * @param itens A lista de itens da venda.
     * @return O valor total dos itens.
     */
    public BigDecimal calcularTotalItens(List<ItemVenda> itens) {
        return itens.stream()
                .map(ItemVenda::getPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula o valor final da venda após aplicar o desconto.
     * @param valorTotal O valor total da venda antes do desconto.
     * @param valorDesconto O valor do desconto a ser aplicado.
     * @return O valor final da venda.
     */
    public BigDecimal calcularValorFinalComDesconto(BigDecimal valorTotal, BigDecimal valorDesconto) {
        return valorTotal.subtract(valorDesconto);
    }
}