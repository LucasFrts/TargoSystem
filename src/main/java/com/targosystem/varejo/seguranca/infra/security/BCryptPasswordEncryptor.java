package com.targosystem.varejo.seguranca.infra.security;

import com.targosystem.varejo.seguranca.domain.service.PasswordEncryptor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

public class BCryptPasswordEncryptor implements PasswordEncryptor {

    private final BCryptPasswordEncoder encoder;

    public BCryptPasswordEncryptor() {
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encrypt(String rawPassword) {
        Objects.requireNonNull(rawPassword, "Raw password cannot be null for encryption");
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        Objects.requireNonNull(rawPassword, "Raw password cannot be null for matching");
        Objects.requireNonNull(encodedPassword, "Encoded password cannot be null for matching");
        return encoder.matches(rawPassword, encodedPassword);
    }
}