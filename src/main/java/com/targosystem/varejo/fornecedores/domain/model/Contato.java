package com.targosystem.varejo.fornecedores.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.domain.ValueObject;

public record Contato(
        String email,
        String telefone
) implements ValueObject {

    public Contato {
        if (email == null || !email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
            throw new DomainException("Email inválido.");
        }
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new DomainException("Telefone do contato não pode ser nulo ou vazio.");
        }
    }
}