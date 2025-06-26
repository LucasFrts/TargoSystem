package com.targosystem.varejo.fornecedores.domain.model;

import com.targosystem.varejo.shared.domain.ValueObject;
import com.targosystem.varejo.shared.domain.DomainException;

import java.util.Objects;

public record Endereco(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep
) implements ValueObject {

    public Endereco {
        if (logradouro == null || logradouro.trim().isEmpty()) {
            throw new DomainException("Logradouro não pode ser nulo ou vazio.");
        }
        if (numero == null || numero.trim().isEmpty()) {
            throw new DomainException("Número não pode ser nulo ou vazio.");
        }
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new DomainException("Bairro não pode ser nulo ou vazio.");
        }
        if (cidade == null || cidade.trim().isEmpty()) {
            throw new DomainException("Cidade não pode ser nula ou vazia.");
        }
        if (estado == null || estado.trim().isEmpty() || estado.length() != 2) {
            throw new DomainException("Estado inválido. Deve ser uma sigla de 2 letras.");
        }
        if (cep == null || !cep.matches("\\d{8}")) { // Validação básica de 8 dígitos para CEP
            throw new DomainException("CEP inválido. Deve conter 8 dígitos numéricos.");
        }
    }
}