package com.targosystem.varejo.fornecedores.application.usecases;

import com.targosystem.varejo.fornecedores.application.input.InativarFornecedorInput;
import com.targosystem.varejo.fornecedores.application.output.FornecedorOutput;
import com.targosystem.varejo.fornecedores.domain.model.Fornecedor;
import com.targosystem.varejo.fornecedores.domain.model.FornecedorId; // NOVO
import com.targosystem.varejo.fornecedores.domain.repository.FornecedorRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InativarFornecedorUseCase {

    private static final Logger logger = LoggerFactory.getLogger(InativarFornecedorUseCase.class);

    private final FornecedorRepository fornecedorRepository;
    private final EventPublisher eventPublisher;

    public InativarFornecedorUseCase(FornecedorRepository fornecedorRepository, EventPublisher eventPublisher) {
        this.fornecedorRepository = Objects.requireNonNull(fornecedorRepository, "FornecedorRepository cannot be null.");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public FornecedorOutput execute(InativarFornecedorInput input) {
        logger.info("Tentando inativar fornecedor com ID: {}", input.id());

        FornecedorId fornecedorId = new FornecedorId(input.id()); // Converte para FornecedorId
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new DomainException("Fornecedor não encontrado com ID: " + input.id()));

        if (!fornecedor.isAtivo()) {
            throw new DomainException("Fornecedor com ID " + input.id() + " já está inativo.");
        }

        fornecedor.inativar();

        Fornecedor fornecedorSalvo = fornecedorRepository.save(fornecedor);
        logger.info("Fornecedor inativado com sucesso. ID: {}", fornecedorSalvo.getId().value()); // Usa .value()

        return FornecedorOutput.fromDomain(fornecedorSalvo);
    }
}