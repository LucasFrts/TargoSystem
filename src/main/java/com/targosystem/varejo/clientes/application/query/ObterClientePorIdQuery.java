package com.targosystem.varejo.clientes.application.query;

import com.targosystem.varejo.clientes.application.output.ClienteOutput;
import com.targosystem.varejo.clientes.domain.model.ClienteId;
import com.targosystem.varejo.clientes.domain.repository.ClienteRepository;
import com.targosystem.varejo.shared.domain.DomainException;

import java.util.Objects;

public class ObterClientePorIdQuery {
    private final ClienteRepository clienteRepository;

    public ObterClientePorIdQuery(ClienteRepository clienteRepository) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository, "ClienteRepository cannot be null.");
    }

    public ClienteOutput execute(ClienteId id) {
        Objects.requireNonNull(id, "ID do cliente não pode ser nulo.");
        return clienteRepository.findById(id)
                .map(ClienteOutput::from)
                .orElseThrow(() -> new DomainException("Cliente com ID " + id.value() + " não encontrado."));
    }
}