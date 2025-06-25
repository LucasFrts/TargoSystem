package com.targosystem.varejo.produtos.application.query;

import com.targosystem.varejo.produtos.application.output.ProdutoOutput;
import com.targosystem.varejo.produtos.domain.repository.ProdutoRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListarTodosProdutosQuery {

    private final ProdutoRepository produtoRepository;

    public ListarTodosProdutosQuery(ProdutoRepository produtoRepository) {
        this.produtoRepository = Objects.requireNonNull(produtoRepository, "ProdutoRepository cannot be null");
    }

    public List<ProdutoOutput> execute() {
        return produtoRepository.findAll()
                .stream()
                .map(ProdutoOutput::from)
                .collect(Collectors.toList());
    }
}