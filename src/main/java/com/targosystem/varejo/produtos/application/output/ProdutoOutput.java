package com.targosystem.varejo.produtos.application.output;

import com.targosystem.varejo.produtos.domain.model.Produto; // Ensure Produto is imported
import com.targosystem.varejo.produtos.domain.model.ProdutoId; // This might not be needed if not directly used in the record components
import com.targosystem.varejo.shared.domain.Price; // This might not be needed if not directly used in the record components
import java.time.LocalDateTime;

public record ProdutoOutput(
        String id,
        String nome,
        String descricao,
        String codigoBarras,
        String categoriaNome,
        String marca,
        String precoSugerido, // String for UI formatting
        boolean ativo,
        LocalDateTime dataCadastro,
        LocalDateTime ultimaAtualizacao
) {
    public static ProdutoOutput from(Produto produto) { // Use the fully qualified class name or import properly
        return new ProdutoOutput(
                produto.getId().value(), // Assuming ProdutoId has a .value() method
                produto.getNome(),
                produto.getDescricao(),
                produto.getCodigoBarras(),
                produto.getCategoria().getNome(),
                produto.getMarca(),
                produto.getPrecoVenda().toString(), // CORRECTED: Call getPrecoVenda() and convert to String
                produto.getStatus().equals("ATIVO"), // Assuming "ativo" status is derived from a String status field like "ATIVO" or "INATIVO"
                produto.getDataCriacao(), // Assuming dataCadastro maps to dataCriacao
                produto.getDataAtualizacao() // Assuming ultimaAtualizacao maps to dataAtualizacao
        );
    }
}