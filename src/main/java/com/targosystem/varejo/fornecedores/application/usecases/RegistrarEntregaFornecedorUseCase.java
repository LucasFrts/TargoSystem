package com.targosystem.varejo.fornecedores.application.usecases;

import com.targosystem.varejo.fornecedores.application.input.RegistrarEntregaFornecedorInput;
import com.targosystem.varejo.fornecedores.application.output.EntregaFornecedorOutput;
import com.targosystem.varejo.fornecedores.domain.model.EntregaFornecedor;
import com.targosystem.varejo.fornecedores.domain.model.FornecedorId;
import com.targosystem.varejo.fornecedores.domain.repository.EntregaFornecedorRepository;
import com.targosystem.varejo.fornecedores.domain.repository.FornecedorRepository; // Para verificar a existência do fornecedor
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrarEntregaFornecedorUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RegistrarEntregaFornecedorUseCase.class);

    private final EntregaFornecedorRepository entregaRepository;
    private final FornecedorRepository fornecedorRepository; // Para garantir que o fornecedor existe
    private final EventPublisher eventPublisher;

    public RegistrarEntregaFornecedorUseCase(EntregaFornecedorRepository entregaRepository, FornecedorRepository fornecedorRepository, EventPublisher eventPublisher) {
        this.entregaRepository = Objects.requireNonNull(entregaRepository, "EntregaFornecedorRepository cannot be null.");
        this.fornecedorRepository = Objects.requireNonNull(fornecedorRepository, "FornecedorRepository cannot be null.");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public EntregaFornecedorOutput execute(RegistrarEntregaFornecedorInput input) {
        logger.info("Tentando registrar entrega para fornecedor ID: {} com Pedido de Compra: {}", input.fornecedorId(), input.numeroPedidoCompra());

        FornecedorId fornecedorId = new FornecedorId(input.fornecedorId());
        // Verifica se o fornecedor existe e está ativo antes de registrar uma entrega para ele
        fornecedorRepository.findById(fornecedorId)
                .filter(f -> f.isAtivo())
                .orElseThrow(() -> new DomainException("Fornecedor com ID " + input.fornecedorId() + " não encontrado ou inativo."));

        EntregaFornecedor novaEntrega = new EntregaFornecedor(
                fornecedorId,
                input.numeroPedidoCompra(),
                input.dataPrevistaEntrega(),
                input.quantidadeItens(),
                input.observacoes()
        );

        EntregaFornecedor entregaSalva = entregaRepository.save(novaEntrega);
        logger.info("Entrega registrada com sucesso. ID: {}", entregaSalva.getId());

        return EntregaFornecedorOutput.fromDomain(entregaSalva);
    }
}