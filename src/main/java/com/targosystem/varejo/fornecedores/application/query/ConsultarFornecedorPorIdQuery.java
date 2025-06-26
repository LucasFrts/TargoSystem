package com.targosystem.varejo.fornecedores.application.query;

import com.targosystem.varejo.fornecedores.application.output.FornecedorOutput;
import com.targosystem.varejo.fornecedores.domain.model.FornecedorId; // NOVO
import com.targosystem.varejo.fornecedores.domain.repository.FornecedorRepository;
import com.targosystem.varejo.shared.domain.DomainException;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsultarFornecedorPorIdQuery {

    private static final Logger logger = LoggerFactory.getLogger(ConsultarFornecedorPorIdQuery.class);

    private final FornecedorRepository fornecedorRepository;

    public ConsultarFornecedorPorIdQuery(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = Objects.requireNonNull(fornecedorRepository, "FornecedorRepository cannot be null.");
    }

    public FornecedorOutput execute(String id) {
        logger.info("Consultando fornecedor por ID: {}", id);
        FornecedorId fornecedorId = new FornecedorId(id); // Converte para FornecedorId
        return fornecedorRepository.findById(fornecedorId)
                .map(FornecedorOutput::fromDomain)
                .orElseThrow(() -> new DomainException("Fornecedor não encontrado com ID: " + id));
    }
}