// src/main/java/com/targosystem/varejo/vendas/application/output/ItemVendaOutput.java
package com.targosystem.varejo.vendas.application.output;

import com.targosystem.varejo.vendas.domain.model.ItemVenda; // Import the domain ItemVenda
import java.math.BigDecimal; // Import BigDecimal if used

public record ItemVendaOutput(
        String id,
        String idProduto,
        String nomeProduto,
        int quantidade,
        String precoUnitario, // Use String for formatted BigDecimals for output
        String precoTotal    // Use String for formatted BigDecimals for output
) {
    public static ItemVendaOutput from(ItemVenda itemVenda) {
        return new ItemVendaOutput(
                itemVenda.getId().value(), // Assuming ItemVendaId has a .value() method
                itemVenda.getIdProduto().value(), // Assuming ProdutoId has a .value() method
                itemVenda.getNomeProduto(),
                itemVenda.getQuantidade(),
                itemVenda.getPrecoUnitario().toPlainString(), // Format BigDecimal as String
                itemVenda.getPrecoTotal().toPlainString()     // Format BigDecimal as String
        );
    }
}