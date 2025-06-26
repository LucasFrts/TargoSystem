package com.targosystem.varejo.fornecedores.application.input;

public record AvaliarEntregaFornecedorInput(
        String entregaId,
        int nota, // 1 a 5
        String comentario
) {}