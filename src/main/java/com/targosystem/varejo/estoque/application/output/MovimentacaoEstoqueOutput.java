package com.targosystem.varejo.estoque.application.output;

import com.targosystem.varejo.estoque.domain.model.Estoque;
import com.targosystem.varejo.estoque.domain.model.MovimentacaoEstoque;
import com.targosystem.varejo.estoque.domain.model.TipoMovimentacao;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

record MovimentacaoEstoqueOutput(
        String id,
        String estoqueId,
        TipoMovimentacao tipo,
        int quantidade,
        LocalDateTime dataHora,
        String motivo,
        LoteOutput loteAfetado, // NOVO
        LocalizacaoArmazenamentoOutput localizacaoAfetada // NOVO
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