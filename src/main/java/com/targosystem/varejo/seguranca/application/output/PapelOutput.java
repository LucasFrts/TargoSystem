package com.targosystem.varejo.seguranca.application.output;

import com.targosystem.varejo.seguranca.domain.model.Papel;

public record PapelOutput(
        Integer id,
        String nome,
        String descricao
) {
    public static PapelOutput from(Papel papel) {
        return new PapelOutput(papel.getId(), papel.getNome(), papel.getDescricao());
    }
}