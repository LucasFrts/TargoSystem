package com.targosystem.varejo.fornecedores.application.query;

import com.targosystem.varejo.fornecedores.application.output.FornecedorOutput;
import com.targosystem.varejo.fornecedores.domain.repository.FornecedorRepository;
import com.targosystem.varejo.shared.domain.DomainException;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsultarFornecedorPorCnpjQuery {

    private static final Logger logger = LoggerFactory.getLogger(ConsultarFornecedorPorCnpjQuery.class);

    private final FornecedorRepository fornecedorRepository;

    public ConsultarFornecedorPorCnpjQuery(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = Objects.requireNonNull(fornecedorRepository, "FornecedorRepository cannot be null.");
    }

    public FornecedorOutput execute(String cnpj) {
        logger.info("Consultando fornecedor por CNPJ: {}", cnpj);
        return fornecedorRepository.findByCnpj(cnpj)
                .map(FornecedorOutput::fromDomain)
                .orElseThrow(() -> new DomainException("Fornecedor não encontrado com CNPJ: " + cnpj));
    }
}