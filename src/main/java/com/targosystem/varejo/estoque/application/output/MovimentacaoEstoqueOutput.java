package com.targosystem.varejo.estoque.application.output;

import com.targosystem.varejo.estoque.domain.model.MovimentacaoEstoque;
import com.targosystem.varejo.estoque.domain.model.TipoMovimentacao;
import java.time.LocalDateTime;

record MovimentacaoEstoqueOutput(
        String id,
        String estoqueId,
        TipoMovimentacao tipo,
        int quantidade,
        LocalDateTime dataHora,
        String motivo,
        LoteOutput loteAfetado,
        LocalizacaoArmazenamentoOutput localizacaoAfetada
) {
    static MovimentacaoEstoqueOutput fromDomain(MovimentacaoEstoque mov) {
        return new MovimentacaoEstoqueOutput(
                mov.getId(),
                mov.getEstoqueId(),
                mov.getTipo(),
                mov.getQuantidade(),
                mov.getDataHora(),
                mov.getMotivo(),
                mov.getLoteAfetado() != null ? LoteOutput.fromDomain(mov.getLoteAfetado()) : null,
                mov.getLocalizacaoAfetada() != null ? LocalizacaoArmazenamentoOutput.fromDomain(mov.getLocalizacaoAfetada()) : null
        );
    }
}