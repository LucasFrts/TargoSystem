package com.targosystem.varejo.seguranca.application.input;

import java.util.List;

public record AtualizarUsuarioInput(
        String id, // ID do usuário a ser atualizado
        String nomeCompleto,
        String email,
        Boolean ativo,
        List<String> papeisNomes // Nomes dos papéis atualizados
) {}