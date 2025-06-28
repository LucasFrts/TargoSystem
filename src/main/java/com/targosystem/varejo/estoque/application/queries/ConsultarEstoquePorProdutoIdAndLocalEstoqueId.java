package com.targosystem.varejo.estoque.application.queries;

import com.targosystem.varejo.estoque.application.output.EstoqueOutput;
import com.targosystem.varejo.estoque.domain.repository.EstoqueRepository;
import com.targosystem.varejo.shared.domain.DomainException; // Se usar
import java.util.Objects;

public class ConsultarEstoquePorProdutoIdAndLocalEstoqueId {

    private final EstoqueRepository estoqueRepository;

    public ConsultarEstoquePorProdutoIdAndLocalEstoqueId(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = Objects.requireNonNull(estoqueRepository, "EstoqueRepository cannot be null.");
    }

    public EstoqueOutput execute(String produtoId, String localEstoqueId) {
        return estoqueRepository.findByProdutoIdAndLocalEstoqueId(produtoId, localEstoqueId)
                .map(EstoqueOutput::fromDomain)
                .orElseThrow(() -> new DomainException("Estoque não encontrado para o produto com ID: " + produtoId));
    }
}