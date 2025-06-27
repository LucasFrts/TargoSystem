package com.targosystem.varejo.promocoes.application.input;

import com.targosystem.varejo.promocoes.domain.model.TipoDesconto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List; // Importar List

public record CriarPromocaoInput(
        String nome,
        TipoDesconto tipoDesconto,
        BigDecimal valorDesconto,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        List<String> produtoIds // NOVO: Lista de IDs de produtos
) {}