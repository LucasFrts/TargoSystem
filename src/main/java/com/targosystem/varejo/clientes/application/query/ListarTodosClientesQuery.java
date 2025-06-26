package com.targosystem.varejo.clientes.application.query;

import com.targosystem.varejo.clientes.application.output.ClienteOutput;
import com.targosystem.varejo.clientes.domain.repository.ClienteRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListarTodosClientesQuery {
    private final ClienteRepository clienteRepository;

    public ListarTodosClientesQuery(ClienteRepository clienteRepository) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository, "ClienteRepository cannot be null.");
    }

    public List<ClienteOutput> execute() {
        return clienteRepository.findAll().stream()
                .map(ClienteOutput::from)
                .collect(Collectors.toList());
    }
}