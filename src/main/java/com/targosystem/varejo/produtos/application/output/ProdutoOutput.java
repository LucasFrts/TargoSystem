package com.targosystem.varejo.produtos.application.output;

import com.targosystem.varejo.produtos.domain.model.Produto;
import java.time.LocalDateTime;

public record ProdutoOutput(
        String id,
        String nome,
        String descricao,
        String codigoBarras,
        String categoriaNome,
        String marca,
        String precoSugerido,
        boolean ativo,
        LocalDateTime dataCadastro,
        LocalDateTime ultimaAtualizacao
) {
    public static ProdutoOutput from(Produto produto) {
        return new ProdutoOutput(
                produto.getId().value(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getCodigoBarras(),
                produto.getCategoria().getNome(),
                produto.getMarca(),
                produto.getPrecoVenda().toString(),
                produto.getStatus().equals("ATIVO"),
                produto.getDataCriacao(),
                produto.getDataAtualizacao()
        );
    }
}