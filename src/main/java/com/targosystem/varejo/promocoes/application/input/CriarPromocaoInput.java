package com.targosystem.varejo.promocoes.application.input;

import com.targosystem.varejo.promocoes.domain.model.TipoDesconto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CriarPromocaoInput(
        String nome,
        TipoDesconto tipoDesconto,
        BigDecimal valorDesconto,
        LocalDateTime dataInicio,
        LocalDateTime dataFim
) {}