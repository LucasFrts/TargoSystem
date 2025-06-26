package com.targosystem.varejo.estoque.application;

import com.targosystem.varejo.estoque.application.input.RegistrarEntradaEstoqueInput; // NOVO
import com.targosystem.varejo.estoque.application.input.RegistrarMovimentacaoEstoqueInput;
import com.targosystem.varejo.estoque.application.input.RegistrarSaidaEstoqueInput;   // NOVO
import com.targosystem.varejo.estoque.application.output.EstoqueOutput;
import com.targosystem.varejo.estoque.application.queries.ConsultarEstoquePorProdutoIdQuery;
import com.targosystem.varejo.estoque.application.usecases.RegistrarEntradaEstoqueUseCase; // NOVO
import com.targosystem.varejo.estoque.application.usecases.RegistrarMovimentacaoEstoqueUseCase;
import com.targosystem.varejo.estoque.application.usecases.RegistrarSaidaEstoqueUseCase;   // NOVO

import java.util.Objects;

public class EstoqueService {

    private final RegistrarMovimentacaoEstoqueUseCase registrarMovimentacaoEstoqueUseCase;
    private final RegistrarEntradaEstoqueUseCase registrarEntradaEstoqueUseCase;
    private final RegistrarSaidaEstoqueUseCase registrarSaidaEstoqueUseCase;
    private final ConsultarEstoquePorProdutoIdQuery consultarEstoquePorProdutoIdQuery;

    public EstoqueService(
            RegistrarEntradaEstoqueUseCase registrarEntradaEstoqueUseCase,
            RegistrarSaidaEstoqueUseCase registrarSaidaEstoqueUseCase,
            ConsultarEstoquePorProdutoIdQuery consultarEstoquePorProdutoIdQuery,
            RegistrarMovimentacaoEstoqueUseCase registrarMovimentacaoEstoqueUseCase
            ) {
        this.registrarEntradaEstoqueUseCase = Objects.requireNonNull(registrarEntradaEstoqueUseCase, "RegistrarEntradaEstoqueUseCase cannot be null.");
        this.registrarSaidaEstoqueUseCase = Objects.requireNonNull(registrarSaidaEstoqueUseCase, "RegistrarSaidaEstoqueUseCase cannot be null.");
        this.consultarEstoquePorProdutoIdQuery = Objects.requireNonNull(consultarEstoquePorProdutoIdQuery, "ConsultarEstoquePorProdutoIdQuery cannot be null.");
        this.registrarMovimentacaoEstoqueUseCase = Objects.requireNonNull(registrarMovimentacaoEstoqueUseCase, "RegistrarMovimentacaoEstoqueUseCase cannot be null.");
    }

    public EstoqueOutput registrarEntrada(RegistrarEntradaEstoqueInput input) {
        return registrarEntradaEstoqueUseCase.execute(input);
    }

    public EstoqueOutput registrarSaida(RegistrarSaidaEstoqueInput input) {
        return registrarSaidaEstoqueUseCase.execute(input);
    }

    public EstoqueOutput registrarMovimentacao(RegistrarMovimentacaoEstoqueInput input) {
        return registrarMovimentacaoEstoqueUseCase.execute(input);
    }

    public EstoqueOutput consultarEstoquePorProdutoId(String produtoId) {
        return consultarEstoquePorProdutoIdQuery.execute(produtoId);
    }

}