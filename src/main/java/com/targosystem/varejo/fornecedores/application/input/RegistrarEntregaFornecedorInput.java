package com.targosystem.varejo.fornecedores.application.input;

import java.time.LocalDate;

public record RegistrarEntregaFornecedorInput(
        String fornecedorId,
        String numeroPedidoCompra,
        LocalDate dataPrevistaEntrega,
        int quantidadeItens,
        String observacoes
) {}