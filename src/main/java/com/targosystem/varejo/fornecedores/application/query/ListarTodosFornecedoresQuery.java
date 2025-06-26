package com.targosystem.varejo.fornecedores.application.query;

import com.targosystem.varejo.fornecedores.application.output.FornecedorOutput;
import com.targosystem.varejo.fornecedores.domain.repository.FornecedorRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListarTodosFornecedoresQuery {

    private static final Logger logger = LoggerFactory.getLogger(ListarTodosFornecedoresQuery.class);

    private final FornecedorRepository fornecedorRepository;

    public ListarTodosFornecedoresQuery(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = Objects.requireNonNull(fornecedorRepository, "FornecedorRepository cannot be null.");
    }

    public List<FornecedorOutput> execute() {
        logger.info("Listando todos os fornecedores.");
        return fornecedorRepository.findAll().stream()
                .map(FornecedorOutput::fromDomain)
                .collect(Collectors.toList());
    }
}