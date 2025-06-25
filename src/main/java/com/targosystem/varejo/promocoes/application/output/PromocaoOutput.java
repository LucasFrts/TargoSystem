package com.targosystem.varejo.promocoes.application.output;

import com.targosystem.varejo.promocoes.domain.model.Promocao;
import com.targosystem.varejo.promocoes.domain.model.TipoDesconto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromocaoOutput(
        String id,
        String nome,
        TipoDesconto tipoDesconto,
        BigDecimal valorDesconto,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        boolean ativa
) {
    public static PromocaoOutput fromDomain(Promocao promocao) {
        return new PromocaoOutput(
                promocao.getId(),
                promocao.getNome(),
                promocao.getTipoDesconto(),
                promocao.getValorDesconto(),
                promocao.getDataInicio(),
                promocao.getDataFim(),
                promocao.isAtiva()
        );
    }
}