package com.targosystem.varejo.produtos.application;

import com.targosystem.varejo.produtos.application.input.AtualizarProdutoInput;
import com.targosystem.varejo.produtos.application.input.CadastrarProdutoInput;
import com.targosystem.varejo.produtos.application.output.ProdutoOutput;
import com.targosystem.varejo.produtos.application.query.ListarTodosProdutosQuery;
import com.targosystem.varejo.produtos.application.query.ObterProdutoPorIdQuery;
import com.targosystem.varejo.produtos.application.usecases.AtualizarProdutoUseCase;
import com.targosystem.varejo.produtos.application.usecases.CadastrarProdutoUseCase;
import com.targosystem.varejo.produtos.domain.model.ProdutoId;
import java.util.List;
import java.util.Objects;

public class ProdutoService {

    private final CadastrarProdutoUseCase cadastrarProdutoUseCase;
    private final AtualizarProdutoUseCase atualizarProdutoUseCase;
    private final ObterProdutoPorIdQuery obterProdutoPorIdQuery;
    private final ListarTodosProdutosQuery listarTodosProdutosQuery;
    // ... outros use cases/queries como inativar, ativar, etc.

    public ProdutoService(
            CadastrarProdutoUseCase cadastrarProdutoUseCase,
            AtualizarProdutoUseCase atualizarProdutoUseCase,
            ObterProdutoPorIdQuery obterProdutoPorIdQuery,
            ListarTodosProdutosQuery listarTodosProdutosQuery) {
        this.cadastrarProdutoUseCase = Objects.requireNonNull(cadastrarProdutoUseCase, "CadastrarProdutoUseCase cannot be null");
        this.atualizarProdutoUseCase = Objects.requireNonNull(atualizarProdutoUseCase, "AtualizarProdutoUseCase cannot be null");
        this.obterProdutoPorIdQuery = Objects.requireNonNull(obterProdutoPorIdQuery, "ObterProdutoPorIdQuery cannot be null");
        this.listarTodosProdutosQuery = Objects.requireNonNull(listarTodosProdutosQuery, "ListarTodosProdutosQuery cannot be null");
    }

    public ProdutoOutput cadastrarProduto(CadastrarProdutoInput input) {
        return cadastrarProdutoUseCase.execute(input);
    }

    public ProdutoOutput atualizarProduto(AtualizarProdutoInput input) {
        return atualizarProdutoUseCase.execute(input);
    }

    public ProdutoOutput obterProdutoPorId(String id) {
        return obterProdutoPorIdQuery.execute(ProdutoId.from(id));
    }

    public List<ProdutoOutput> listarTodosProdutos() {
        return listarTodosProdutosQuery.execute();
    }
}