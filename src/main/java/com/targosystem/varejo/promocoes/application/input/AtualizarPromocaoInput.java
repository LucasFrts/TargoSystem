package com.targosystem.varejo.promocoes.application.input;

import com.targosystem.varejo.promocoes.domain.model.TipoDesconto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List; // Importar List
import java.util.Optional;

public record AtualizarPromocaoInput(
        String promocaoId,
        Optional<String> nome,
        Optional<TipoDesconto> tipoDesconto,
        Optional<BigDecimal> valorDesconto,
        Optional<LocalDateTime> dataInicio,
        Optional<LocalDateTime> dataFim,
        Optional<Boolean> ativa,
        Optional<List<String>> produtoIds // NOVO: Optional de Lista de IDs de produtos
) {}