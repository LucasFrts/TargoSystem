package com.targosystem.varejo.vendas.application.input;

import java.math.BigDecimal;
import java.util.List;
import com.targosystem.varejo.produtos.domain.model.ProdutoId; // Certifique-se de que ProdutoId é usado no input

public record RealizarVendaInput(
        String idCliente, // Apenas o ID do cliente
        List<ItemVendaInput> itens,
        BigDecimal valorDesconto
) {
    // Definir o record ItemVendaInput dentro ou como um arquivo separado
    public record ItemVendaInput(
            String idProduto, // ID do produto como String
            int quantidade
            // Remover nomeProduto, precoUnitario, etc. do input, eles serão buscados
    ) {}
}