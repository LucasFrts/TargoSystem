package com.targosystem.varejo.vendas.application.input;

import java.math.BigDecimal;
import java.util.List;
import com.targosystem.varejo.produtos.domain.model.ProdutoId; // Certifique-se de que ProdutoId é usado no input

public record RealizarVendaInput(
        String idCliente, // Apenas o ID do cliente
        List<ItemVendaInput> itens,
        BigDecimal valorDesconto
) {
    public record ItemVendaInput(
            String idProduto,
            int quantidade
    ) {}
}