package com.targosystem.varejo.fornecedores.application;

import com.targosystem.varejo.fornecedores.application.input.CriarFornecedorInput;
import com.targosystem.varejo.fornecedores.application.input.AtualizarFornecedorInput;
import com.targosystem.varejo.fornecedores.application.input.InativarFornecedorInput;
import com.targosystem.varejo.fornecedores.application.input.RegistrarEntregaFornecedorInput; // NOVO
import com.targosystem.varejo.fornecedores.application.input.RegistrarRecebimentoEntregaInput; // NOVO
import com.targosystem.varejo.fornecedores.application.input.AvaliarEntregaFornecedorInput; // NOVO

import com.targosystem.varejo.fornecedores.application.output.FornecedorOutput;
import com.targosystem.varejo.fornecedores.application.output.EntregaFornecedorOutput; // NOVO

import com.targosystem.varejo.fornecedores.application.query.ConsultarFornecedorPorIdQuery;
import com.targosystem.varejo.fornecedores.application.query.ConsultarFornecedorPorCnpjQuery;
import com.targosystem.varejo.fornecedores.application.query.ListarTodosFornecedoresQuery;
import com.targosystem.varejo.fornecedores.application.query.ListarEntregasPorFornecedorQuery; // NOVO

import com.targosystem.varejo.fornecedores.application.usecases.CriarFornecedorUseCase;
import com.targosystem.varejo.fornecedores.application.usecases.AtualizarFornecedorUseCase;
import com.targosystem.varejo.fornecedores.application.usecases.InativarFornecedorUseCase;
import com.targosystem.varejo.fornecedores.application.usecases.RegistrarEntregaFornecedorUseCase; // NOVO
import com.targosystem.varejo.fornecedores.application.usecases.RegistrarRecebimentoEntregaUseCase; // NOVO
import com.targosystem.varejo.fornecedores.application.usecases.AvaliarEntregaFornecedorUseCase; // NOVO

import java.util.List;
import java.util.Objects;

public class FornecedorService {

    private final CriarFornecedorUseCase criarFornecedorUseCase;
    private final AtualizarFornecedorUseCase atualizarFornecedorUseCase;
    private final InativarFornecedorUseCase inativarFornecedorUseCase;
    private final RegistrarEntregaFornecedorUseCase registrarEntregaFornecedorUseCase; // NOVO
    private final RegistrarRecebimentoEntregaUseCase registrarRecebimentoEntregaUseCase; // NOVO
    private final AvaliarEntregaFornecedorUseCase avaliarEntregaFornecedorUseCase; // NOVO

    private final ConsultarFornecedorPorIdQuery consultarFornecedorPorIdQuery;
    private final ConsultarFornecedorPorCnpjQuery consultarFornecedorPorCnpjQuery;
    private final ListarTodosFornecedoresQuery listarTodosFornecedoresQuery;
    private final ListarEntregasPorFornecedorQuery listarEntregasPorFornecedorQuery; // NOVO

    public FornecedorService(
            CriarFornecedorUseCase criarFornecedorUseCase,
            AtualizarFornecedorUseCase atualizarFornecedorUseCase,
            InativarFornecedorUseCase inativarFornecedorUseCase,
            RegistrarEntregaFornecedorUseCase registrarEntregaFornecedorUseCase,
            RegistrarRecebimentoEntregaUseCase registrarRecebimentoEntregaUseCase,
            AvaliarEntregaFornecedorUseCase avaliarEntregaFornecedorUseCase,
            ConsultarFornecedorPorIdQuery consultarFornecedorPorIdQuery,
            ConsultarFornecedorPorCnpjQuery consultarFornecedorPorCnpjQuery,
            ListarTodosFornecedoresQuery listarTodosFornecedoresQuery,
            ListarEntregasPorFornecedorQuery listarEntregasPorFornecedorQuery) {
        this.criarFornecedorUseCase = Objects.requireNonNull(criarFornecedorUseCase, "CriarFornecedorUseCase cannot be null.");
        this.atualizarFornecedorUseCase = Objects.requireNonNull(atualizarFornecedorUseCase, "AtualizarFornecedorUseCase cannot be null.");
        this.inativarFornecedorUseCase = Objects.requireNonNull(inativarFornecedorUseCase, "InativarFornecedorUseCase cannot be null.");
        this.registrarEntregaFornecedorUseCase = Objects.requireNonNull(registrarEntregaFornecedorUseCase, "RegistrarEntregaFornecedorUseCase cannot be null.");
        this.registrarRecebimentoEntregaUseCase = Objects.requireNonNull(registrarRecebimentoEntregaUseCase, "RegistrarRecebimentoEntregaUseCase cannot be null.");
        this.avaliarEntregaFornecedorUseCase = Objects.requireNonNull(avaliarEntregaFornecedorUseCase, "AvaliarEntregaFornecedorUseCase cannot be null.");
        this.consultarFornecedorPorIdQuery = Objects.requireNonNull(consultarFornecedorPorIdQuery, "ConsultarFornecedorPorIdQuery cannot be null.");
        this.consultarFornecedorPorCnpjQuery = Objects.requireNonNull(consultarFornecedorPorCnpjQuery, "ConsultarFornecedorPorCnpjQuery cannot be null.");
        this.listarTodosFornecedoresQuery = Objects.requireNonNull(listarTodosFornecedoresQuery, "ListarTodosFornecedoresQuery cannot be null.");
        this.listarEntregasPorFornecedorQuery = Objects.requireNonNull(listarEntregasPorFornecedorQuery, "ListarEntregasPorFornecedorQuery cannot be null.");
    }

    public FornecedorOutput criarFornecedor(CriarFornecedorInput input) {
        return criarFornecedorUseCase.execute(input);
    }

    public FornecedorOutput atualizarFornecedor(AtualizarFornecedorInput input) {
        return atualizarFornecedorUseCase.execute(input);
    }

    public FornecedorOutput inativarFornecedor(InativarFornecedorInput input) {
        return inativarFornecedorUseCase.execute(input);
    }

    public EntregaFornecedorOutput registrarEntrega(RegistrarEntregaFornecedorInput input) {
        return registrarEntregaFornecedorUseCase.execute(input);
    }

    public EntregaFornecedorOutput registrarRecebimentoEntrega(RegistrarRecebimentoEntregaInput input) {
        return registrarRecebimentoEntregaUseCase.execute(input);
    }

    public EntregaFornecedorOutput avaliarEntrega(AvaliarEntregaFornecedorInput input) {
        return avaliarEntregaFornecedorUseCase.execute(input);
    }

    public FornecedorOutput consultarFornecedorPorId(String id) {
        return consultarFornecedorPorIdQuery.execute(id);
    }

    public FornecedorOutput consultarFornecedorPorCnpj(String cnpj) {
        return consultarFornecedorPorCnpjQuery.execute(cnpj);
    }

    public List<FornecedorOutput> listarTodosFornecedores() {
        return listarTodosFornecedoresQuery.execute();
    }

    public List<EntregaFornecedorOutput> listarEntregasPorFornecedor(String fornecedorId) {
        return listarEntregasPorFornecedorQuery.execute(fornecedorId);
    }
}