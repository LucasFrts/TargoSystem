package com.targosystem.varejo.estoque.application.input;

public record RegistrarSaidaEstoqueInput(
        String produtoId,
        int quantidade,
        String motivo // Opcional, ex: "Venda", "Perda", "Devolução ao fornecedor"
) {}
