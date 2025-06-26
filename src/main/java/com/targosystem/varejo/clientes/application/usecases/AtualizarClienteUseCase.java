package com.targosystem.varejo.clientes.application.usecases;

import com.targosystem.varejo.clientes.application.input.AtualizarClienteInput;
import com.targosystem.varejo.clientes.application.output.ClienteOutput;
import com.targosystem.varejo.clientes.domain.model.Cliente;
import com.targosystem.varejo.clientes.domain.model.ClienteId;
import com.targosystem.varejo.clientes.domain.repository.ClienteRepository;
import com.targosystem.varejo.shared.domain.DomainException;

import java.util.Objects;

public class AtualizarClienteUseCase {
    private final ClienteRepository clienteRepository;

    public AtualizarClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository, "ClienteRepository cannot be null.");
    }

    public ClienteOutput execute(AtualizarClienteInput input) {
        Objects.requireNonNull(input.id(), "ID do cliente para atualização não pode ser nulo.");
        Objects.requireNonNull(input.nome(), "Nome do cliente não pode ser nulo.");

        Cliente clienteExistente = clienteRepository.findById(ClienteId.from(input.id()))
                .orElseThrow(() -> new DomainException("Cliente com ID " + input.id() + " não encontrado."));

        // Atualiza as informações do cliente (lógica encapsulada no domínio)
        clienteExistente.atualizarInformacoes(
                input.nome(),
                input.email(),
                input.telefone()
        );

        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        return ClienteOutput.from(clienteAtualizado);
    }
}