package com.targosystem.varejo.produtos.application.output;

import com.targosystem.varejo.produtos.domain.model.Categoria;
import com.targosystem.varejo.produtos.domain.model.CategoriaId;

public record CategoriaOutput(
        String id,
        String nome,
        String descricao
) {
    public static CategoriaOutput fromDomain(Categoria categoria) {
        return new CategoriaOutput(
                categoria.getId().value(),
                categoria.getNome(),
                categoria.getDescricao()
        );
    }
}