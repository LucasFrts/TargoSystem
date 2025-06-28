package com.targosystem.varejo.produtos.application.query;

import com.targosystem.varejo.produtos.application.output.ProdutoOutput;
import com.targosystem.varejo.produtos.domain.repository.ProdutoRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConsultarProdutosAtivosPorNomeOuCodigoQuery {

    private final ProdutoRepository produtoRepository;

    public ConsultarProdutosAtivosPorNomeOuCodigoQuery(ProdutoRepository produtoRepository) {
        this.produtoRepository = Objects.requireNonNull(produtoRepository, "ProdutoRepository cannot be null.");
    }

    public List<ProdutoOutput> execute(String termoBusca){
        return produtoRepository.findAll().stream()
                .filter(p -> p.isAtivo() &&
                        (p.getNome().toLowerCase().contains(termoBusca.toLowerCase()) ||
                                p.getCodigoBarras().toLowerCase().contains(termoBusca.toLowerCase())))
                .map(ProdutoOutput::from)
                .collect(Collectors.toList());
    }
}
