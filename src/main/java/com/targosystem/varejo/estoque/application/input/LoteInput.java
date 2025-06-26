package com.targosystem.varejo.estoque.application.input;

import java.time.LocalDate;

public record LoteInput(String numeroLote, LocalDate dataFabricacao, LocalDate dataValidade) {}
