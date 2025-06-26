package com.targosystem.varejo.clientes.application.input;

public record AtualizarClienteInput(
        String id, // ID para identificar o cliente a ser atualizado
        String nome,
        String email,
        String telefone
) {}