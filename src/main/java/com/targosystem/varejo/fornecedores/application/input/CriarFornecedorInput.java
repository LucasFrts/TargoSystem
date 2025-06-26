package com.targosystem.varejo.fornecedores.application.input;

public record CriarFornecedorInput(
        String nome,
        String cnpj,
        String emailContato, // Mapeado para Contato
        String telefoneContato, // Mapeado para Contato
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        boolean ativo
) {}