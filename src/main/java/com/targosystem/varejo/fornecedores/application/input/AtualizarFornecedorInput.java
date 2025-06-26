package com.targosystem.varejo.fornecedores.application.input;

public record AtualizarFornecedorInput(
        String id, // O ID do fornecedor a ser atualizado
        String nome,
        String cnpj,
        String emailContato,
        String telefoneContato,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        boolean ativo
) {}