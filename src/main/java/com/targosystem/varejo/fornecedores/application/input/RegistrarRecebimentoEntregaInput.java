package com.targosystem.varejo.fornecedores.application.input;

import java.time.LocalDate;

public record RegistrarRecebimentoEntregaInput(
        String entregaId,
        LocalDate dataRealizacao
) {}