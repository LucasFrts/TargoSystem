package com.targosystem.varejo.promocoes.application.output;

import com.targosystem.varejo.promocoes.domain.model.Promocao;
import com.targosystem.varejo.promocoes.domain.model.TipoDesconto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List; // Importar List
import java.util.Collections; // Para Collections.unmodifiableList

public record PromocaoOutput(
        String id,
        String nome,
        TipoDesconto tipoDesconto,
        BigDecimal valorDesconto,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        boolean ativa,
        List<String> produtoIds,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
    public static PromocaoOutput from(Promocao promocao) {
        return new PromocaoOutput(
                promocao.getId(),
                promocao.getNome(),
                promocao.getTipoDesconto(),
                promocao.getValorDesconto(),
                promocao.getDataInicio(),
                promocao.getDataFim(),
                promocao.isAtiva(),
                Collections.unmodifiableList(promocao.getProdutoIds()),
                promocao.getDataCriacao(),
                promocao.getDataAtualizacao()
        );
    }
}