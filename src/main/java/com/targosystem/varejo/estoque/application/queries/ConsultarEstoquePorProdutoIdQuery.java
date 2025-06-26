package com.targosystem.varejo.estoque.application.queries;

import com.targosystem.varejo.estoque.application.output.EstoqueOutput;
import com.targosystem.varejo.estoque.domain.repository.EstoqueRepository;
import com.targosystem.varejo.shared.domain.DomainException; // Se usar
import java.util.Objects;

public class ConsultarEstoquePorProdutoIdQuery {

    private final EstoqueRepository estoqueRepository;

    public ConsultarEstoquePorProdutoIdQuery(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = Objects.requireNonNull(estoqueRepository, "EstoqueRepository cannot be null.");
    }

    public EstoqueOutput execute(String produtoId) {
        return estoqueRepository.findByProdutoId(produtoId)
                .map(EstoqueOutput::fromDomain)
                .orElseThrow(() -> new DomainException("Estoque não encontrado para o produto com ID: " + produtoId));
    }
}