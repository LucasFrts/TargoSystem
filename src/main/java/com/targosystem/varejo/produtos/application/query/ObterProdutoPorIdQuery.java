package com.targosystem.varejo.produtos.application.query;

import com.targosystem.varejo.produtos.application.output.ProdutoOutput;
import com.targosystem.varejo.produtos.domain.model.ProdutoId;
import com.targosystem.varejo.produtos.domain.repository.ProdutoRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;

public class ObterProdutoPorIdQuery {

    private final ProdutoRepository produtoRepository;

    public ObterProdutoPorIdQuery(ProdutoRepository produtoRepository) {
        this.produtoRepository = Objects.requireNonNull(produtoRepository, "ProdutoRepository cannot be null");
    }

    public ProdutoOutput execute(ProdutoId id) {
        Objects.requireNonNull(id, "Product ID cannot be null");
        return produtoRepository.findById(id)
                .map(ProdutoOutput::from)
                .orElseThrow(() -> new DomainException("Product with ID " + id.getValue() + " not found."));
    }
}