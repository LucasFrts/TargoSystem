package com.targosystem.varejo.vendas.domain.service;

import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.produtos.application.output.PrecoProdutoOutput;
import com.targosystem.varejo.produtos.application.query.ConsultarPrecoProdutoQuery; // Supondo que exista

import java.math.BigDecimal;
import java.util.Objects;

public class GestorPoliticaPreco {

    private final ConsultarPrecoProdutoQuery consultarPrecoProdutoQuery;

    public GestorPoliticaPreco(ConsultarPrecoProdutoQuery consultarPrecoProdutoQuery) {
        this.consultarPrecoProdutoQuery = Objects.requireNonNull(consultarPrecoProdutoQuery, "ConsultarPrecoProdutoQuery cannot be null.");
    }

    /**
     * Obtém o preço unitário de um produto considerando as políticas de preço atuais.
     * @param idProduto O ID do produto.
     * @return O preço unitário do produto.
     */
    public BigDecimal getPrecoUnitario(String idProduto) {
        PrecoProdutoOutput precoProduto = consultarPrecoProdutoQuery.execute(idProduto);
        if (precoProduto == null) {
            throw new DomainException("Preço do produto " + idProduto + " não encontrado.");
        }
        // Aqui poderia haver lógica complexa para aplicar promoções, descontos por volume, etc.
        // Por enquanto, apenas retorna o preço base.
        return precoProduto.preco();
    }
}