package com.targosystem.varejo.seguranca.application.input;

import java.util.List;

public record CriarUsuarioInput(
        String username,
        String password,
        String nomeCompleto,
        String email,
        List<String> papeisNomes
) {}