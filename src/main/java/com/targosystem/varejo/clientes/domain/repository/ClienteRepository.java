package com.targosystem.varejo.clientes.domain.repository;

import com.targosystem.varejo.clientes.domain.model.Cliente;
import com.targosystem.varejo.clientes.domain.model.ClienteId;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(ClienteId id);
    Optional<Cliente> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    List<Cliente> findAll();
    void delete(ClienteId id);
}