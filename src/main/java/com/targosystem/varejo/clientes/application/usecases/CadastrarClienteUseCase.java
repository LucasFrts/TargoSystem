package com.targosystem.varejo.clientes.application.usecases;

import com.targosystem.varejo.clientes.application.input.CadastrarClienteInput;
import com.targosystem.varejo.clientes.application.output.ClienteOutput;
import com.targosystem.varejo.clientes.domain.model.Cliente;
import com.targosystem.varejo.clientes.domain.repository.ClienteRepository;
import com.targosystem.varejo.shared.domain.DomainException;

import java.util.Objects;

public class CadastrarClienteUseCase {
    private final ClienteRepository clienteRepository;

    public CadastrarClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository, "ClienteRepository cannot be null.");
    }

    public ClienteOutput execute(CadastrarClienteInput input) {
        Objects.requireNonNull(input.nome(), "Nome do cliente não pode ser nulo.");
        Objects.requireNonNull(input.cpf(), "CPF do cliente não pode ser nulo.");

        // Validação de unicidade do CPF
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
        return ClienteOutput.from(clienteSalvo);
    }
}