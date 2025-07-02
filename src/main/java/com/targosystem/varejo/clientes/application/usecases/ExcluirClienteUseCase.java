package com.targosystem.varejo.clientes.application.usecases;

import com.targosystem.varejo.clientes.domain.model.ClienteId;
import com.targosystem.varejo.clientes.domain.repository.ClienteRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Objects;

public class ExcluirClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final EntityManager entityManager;

    public ExcluirClienteUseCase(ClienteRepository clienteRepository, EntityManager entityManager) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository);
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    public void execute(String id) {
        Objects.requireNonNull(id, "ID do cliente não pode ser nulo");

        ClienteId clienteId = new ClienteId(id);

        if (clienteRepository.findById(clienteId).isEmpty()) {
            throw new DomainException("Cliente com ID " + id + " não encontrado.");
        }

        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            clienteRepository.delete(clienteId);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
