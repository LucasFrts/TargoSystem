package com.targosystem.varejo.estoque.application.input;

import java.time.LocalDate;

// Para Lote
public record RegistrarEntradaEstoqueInput(
        String produtoId,
        int quantidade,
        LoteInput lote,
        LocalizacaoArmazenamentoInput localizacao,
        String motivo
) {}