package com.targosystem.varejo.clientes.application.input;

public record CadastrarClienteInput(
        String nome,
        String cpf,
        String email,
        String telefone
) {}