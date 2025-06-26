package com.targosystem.varejo.clientes.application;

import com.targosystem.varejo.clientes.application.input.AtualizarClienteInput;
import com.targosystem.varejo.clientes.application.input.CadastrarClienteInput;
import com.targosystem.varejo.clientes.application.output.ClienteOutput;
import com.targosystem.varejo.clientes.application.query.ListarTodosClientesQuery;
import com.targosystem.varejo.clientes.application.query.ObterClientePorIdQuery;
import com.targosystem.varejo.clientes.application.usecases.AtualizarClienteUseCase;
import com.targosystem.varejo.clientes.application.usecases.CadastrarClienteUseCase;
import com.targosystem.varejo.clientes.domain.model.ClienteId;

import java.util.List;
import java.util.Objects;

public class ClienteService {

    private final CadastrarClienteUseCase cadastrarClienteUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final ObterClientePorIdQuery obterClientePorIdQuery;
    private final ListarTodosClientesQuery listarTodosClientesQuery;

    public ClienteService(
            CadastrarClienteUseCase cadastrarClienteUseCase,
            AtualizarClienteUseCase atualizarClienteUseCase,
            ObterClientePorIdQuery obterClientePorIdQuery,
            ListarTodosClientesQuery listarTodosClientesQuery) {
        this.cadastrarClienteUseCase = Objects.requireNonNull(cadastrarClienteUseCase, "CadastrarClienteUseCase cannot be null");
        this.atualizarClienteUseCase = Objects.requireNonNull(atualizarClienteUseCase, "AtualizarClienteUseCase cannot be null");
        this.obterClientePorIdQuery = Objects.requireNonNull(obterClientePorIdQuery, "ObterClientePorIdQuery cannot be null");
        this.listarTodosClientesQuery = Objects.requireNonNull(listarTodosClientesQuery, "ListarTodosClientesQuery cannot be null");
    }

    public ClienteOutput cadastrarCliente(CadastrarClienteInput input) {
        return cadastrarClienteUseCase.execute(input);
    }

    public ClienteOutput atualizarCliente(AtualizarClienteInput input) {
        return atualizarClienteUseCase.execute(input);
    }

    public ClienteOutput obterClientePorId(String id) {
        return obterClientePorIdQuery.execute(ClienteId.from(id));
    }

    public List<ClienteOutput> listarTodosClientes() {
        return listarTodosClientesQuery.execute();
    }
}