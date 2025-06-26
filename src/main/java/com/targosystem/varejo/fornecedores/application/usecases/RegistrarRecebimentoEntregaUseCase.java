package com.targosystem.varejo.fornecedores.application.usecases;

import com.targosystem.varejo.fornecedores.application.input.RegistrarRecebimentoEntregaInput;
import com.targosystem.varejo.fornecedores.application.output.EntregaFornecedorOutput;
import com.targosystem.varejo.fornecedores.domain.model.EntregaFornecedor;
import com.targosystem.varejo.fornecedores.domain.repository.EntregaFornecedorRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrarRecebimentoEntregaUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RegistrarRecebimentoEntregaUseCase.class);

    private final EntregaFornecedorRepository entregaRepository;
    private final EventPublisher eventPublisher;

    public RegistrarRecebimentoEntregaUseCase(EntregaFornecedorRepository entregaRepository, EventPublisher eventPublisher) {
        this.entregaRepository = Objects.requireNonNull(entregaRepository, "EntregaFornecedorRepository cannot be null.");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public EntregaFornecedorOutput execute(RegistrarRecebimentoEntregaInput input) {
        logger.info("Tentando registrar recebimento para entrega ID: {} na data: {}", input.entregaId(), input.dataRealizacao());

        EntregaFornecedor entrega = entregaRepository.findById(input.entregaId())
                .orElseThrow(() -> new DomainException("Entrega não encontrada com ID: " + input.entregaId()));

        entrega.registrarRecebimento(input.dataRealizacao());

        EntregaFornecedor entregaSalva = entregaRepository.save(entrega);
        logger.info("Recebimento da entrega ID {} registrado com sucesso. Status: {}", entregaSalva.getId(), entregaSalva.getStatus());

        return EntregaFornecedorOutput.fromDomain(entregaSalva);
    }
}