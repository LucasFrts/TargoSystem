package com.targosystem.varejo.estoque.application.output;

import com.targosystem.varejo.estoque.domain.model.LocalEstoque;
import com.targosystem.varejo.estoque.domain.model.TipoLocal;
import java.time.LocalDateTime;

public record LocalEstoqueOutput(
        String id,
        String nome,
        TipoLocal tipo,
        boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
    public static LocalEstoqueOutput fromDomain(LocalEstoque localEstoque) {
        return new LocalEstoqueOutput(
                localEstoque.getId(),
                localEstoque.getNome(),
                localEstoque.getTipo(),
                localEstoque.isAtivo(),
                localEstoque.getDataCriacao(),
                localEstoque.getDataAtualizacao()
        );
    }
}