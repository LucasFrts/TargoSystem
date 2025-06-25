package com.targosystem.varejo.produtos.application.output;

import com.targosystem.varejo.produtos.domain.model.ProdutoId;
import com.targosystem.varejo.shared.domain.Price;
import java.time.LocalDateTime;

public record ProdutoOutput(
        String id,
        String nome,
        String descricao,
        String codigoBarras,
        String categoriaNome,
        String marca,
        String precoSugerido, // String para formatação na UI
        boolean ativo,
        LocalDateTime dataCadastro,
        LocalDateTime ultimaAtualizacao
) {
    public static ProdutoOutput from(com.targosystem.varejo.produtos.domain.model.Produto produto) {
        return new ProdutoOutput(
                produto.getId().getValue(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getCodigoBarras(),
                produto.getCategoria().getNome(),
                produto.getMarca(),
                produto.getPrecoSugerido().toString(),
                produto.isAtivo(),
                produto.getDataCadastro(),
                produto.getUltimaAtualizacao()
        );
    }
}