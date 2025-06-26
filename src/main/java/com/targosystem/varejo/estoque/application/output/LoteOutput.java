package com.targosystem.varejo.estoque.application.output;

import com.targosystem.varejo.estoque.domain.model.Lote;

import java.time.LocalDate;

record LoteOutput(String numeroLote, LocalDate dataFabricacao, LocalDate dataValidade) {
    static LoteOutput fromDomain(Lote lote) {
        return new LoteOutput(lote.getNumeroLote(), lote.getDataFabricacao(), lote.getDataValidade());
    }
}
