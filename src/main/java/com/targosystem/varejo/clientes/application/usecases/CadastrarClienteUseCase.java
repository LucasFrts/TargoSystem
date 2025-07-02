package com.targosystem.varejo.clientes.application.usecases;

import com.targosystem.varejo.clientes.application.input.CadastrarClienteInput;
import com.targosystem.varejo.clientes.application.output.ClienteOutput;
import com.targosystem.varejo.clientes.domain.model.Cliente;
import com.targosystem.varejo.clientes.domain.repository.ClienteRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Objects;

public class CadastrarClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final EntityManager entityManager;

    public CadastrarClienteUseCase(ClienteRepository clienteRepository, EntityManager entityManager) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository, "ClienteRepository cannot be null.");
        this.entityManager = Objects.requireNonNull(entityManager, "EntityManager cannot be null.");
    }

    public ClienteOutput execute(CadastrarClienteInput input) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean newTransaction = false;

        try {
            if (!transaction.isActive()) {
                transaction.begin();
                newTransaction = true;
            }

            Objects.requireNonNull(input.nome(), "Nome do cliente não pode ser nulo.");
            Objects.requireNonNull(input.cpf(), "CPF do cliente não pode ser nulo.");

            if (clienteRepository.existsByCpf(input.cpf())) {
                throw new DomainException("Já existe um cliente cadastrado com o CPF: " + input.cpf());
            }

            Cliente novoCliente = new Cliente(
                    input.nome(),
                    input.cpf(),
                    input.email(),
                    input.telefone()
            );

            Cliente clienteSalvo = clienteRepository.save(novoCliente);

            if (newTransaction) {
                transaction.commit();
            }

            return ClienteOutput.from(clienteSalvo);

        } catch (Exception e) {
            if (transaction.isActive() && newTransaction) {
                transaction.rollback();
            }
            throw new DomainException("Erro ao cadastrar cliente: " + e.getMessage(), e);
        }
    }
}
