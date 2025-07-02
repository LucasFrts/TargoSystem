package com.targosystem.varejo.vendas.application;

import com.targosystem.varejo.vendas.application.input.RealizarVendaInput;
import com.targosystem.varejo.vendas.application.input.CancelarVendaInput;
import com.targosystem.varejo.vendas.application.input.AplicarDescontoVendaInput;

import com.targosystem.varejo.vendas.application.output.VendaOutput;

import com.targosystem.varejo.vendas.application.query.ConsultarVendaPorIdQuery;
import com.targosystem.varejo.vendas.application.query.ListarTodasVendasQuery;

import com.targosystem.varejo.vendas.application.usecases.RealizarVendaUseCase;
import com.targosystem.varejo.vendas.application.usecases.CancelarVendaUseCase;
import com.targosystem.varejo.vendas.application.usecases.AplicarDescontoVendaUseCase;

import java.util.List;
import java.util.Objects;

public class VendaService {

    private final RealizarVendaUseCase realizarVendaUseCase;
    private final CancelarVendaUseCase cancelarVendaUseCase;
    private final AplicarDescontoVendaUseCase aplicarDescontoVendaUseCase;

    private final ConsultarVendaPorIdQuery consultarVendaPorIdQuery;
    private final ListarTodasVendasQuery listarTodasVendasQuery;

    public VendaService(RealizarVendaUseCase realizarVendaUseCase,
                                   CancelarVendaUseCase cancelarVendaUseCase,
                                   AplicarDescontoVendaUseCase aplicarDescontoVendaUseCase,
                                   ConsultarVendaPorIdQuery consultarVendaPorIdQuery,
                                   ListarTodasVendasQuery listarTodasVendasQuery) {
        this.realizarVendaUseCase = Objects.requireNonNull(realizarVendaUseCase, "RealizarVendaUseCase cannot be null.");
        this.cancelarVendaUseCase = Objects.requireNonNull(cancelarVendaUseCase, "CancelarVendaUseCase cannot be null.");
        this.aplicarDescontoVendaUseCase = Objects.requireNonNull(aplicarDescontoVendaUseCase, "AplicarDescontoVendaUseCase cannot be null.");
        this.consultarVendaPorIdQuery = Objects.requireNonNull(consultarVendaPorIdQuery, "ConsultarVendaPorIdQuery cannot be null.");
        this.listarTodasVendasQuery = Objects.requireNonNull(listarTodasVendasQuery, "ListarTodasVendasQuery cannot be null.");
    }

    public VendaOutput realizarVenda(RealizarVendaInput input) {
        return realizarVendaUseCase.execute(input);
    }

    public VendaOutput cancelarVenda(CancelarVendaInput input) {
        return cancelarVendaUseCase.execute(input);
    }

    public VendaOutput aplicarDescontoVenda(AplicarDescontoVendaInput input) {
        return aplicarDescontoVendaUseCase.execute(input);
    }

    public VendaOutput consultarVendaPorId(String idVenda) {
        return consultarVendaPorIdQuery.execute(idVenda);
    }

    public List<VendaOutput> listarTodasVendas() {
        return listarTodasVendasQuery.execute();
    }
}