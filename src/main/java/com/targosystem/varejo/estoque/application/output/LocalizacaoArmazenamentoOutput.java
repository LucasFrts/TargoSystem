package com.targosystem.varejo.estoque.application.output;

import com.targosystem.varejo.estoque.domain.model.LocalizacaoArmazenamento;

record LocalizacaoArmazenamentoOutput(String corredor, String prateleira, String nivel) {
    static LocalizacaoArmazenamentoOutput fromDomain(LocalizacaoArmazenamento loc) {
        return new LocalizacaoArmazenamentoOutput(loc.getCorredor(), loc.getPrateleira(), loc.getNivel());
    }
}
