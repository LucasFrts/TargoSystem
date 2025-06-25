package com.targosystem.varejo.seguranca.application.input;

import java.util.List;

public record CriarUsuarioInput(
        String username,
        String password, // Senha em texto plano, será hashed no Use Case
        String nomeCompleto,
        String email,
        List<String> papeisNomes // Nomes dos papéis a serem associados
) {}