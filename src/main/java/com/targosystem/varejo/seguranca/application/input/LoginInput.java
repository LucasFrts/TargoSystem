package com.targosystem.varejo.seguranca.application.input;

public record LoginInput(
        String username,
        String password
) {}