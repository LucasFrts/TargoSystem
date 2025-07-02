package com.targosystem.varejo.promocoes.application;

import com.targosystem.varejo.promocoes.application.input.AtualizarPromocaoInput;
import com.targosystem.varejo.promocoes.application.input.CriarKitPromocionalInput;
import com.targosystem.varejo.promocoes.application.input.CriarPromocaoInput;
import com.targosystem.varejo.promocoes.application.output.KitPromocionalOutput;
import com.targosystem.varejo.promocoes.application.output.PromocaoOutput;
import com.targosystem.varejo.promocoes.application.query.ListarPromocoesAtivasQuery;
import com.targosystem.varejo.promocoes.application.query.ObterKitPromocionalPorIdQuery;
import com.targosystem.varejo.promocoes.application.query.ListarTodosKitsQuery;
import com.targosystem.varejo.promocoes.application.query.ObterPromocaoPorIdQuery;
import com.targosystem.varejo.promocoes.application.usecases.CriarKitPromocionalUseCase;
import com.targosystem.varejo.promocoes.application.usecases.CriarPromocaoUseCase;
import com.targosystem.varejo.promocoes.application.usecases.AtualizarPromocaoUseCase;
import com.targosystem.varejo.promocoes.application.usecases.ExcluirPromocaoUseCase;
import com.targosystem.varejo.promocoes.application.usecases.ExcluirKitPromocionalUseCase;

// NOVOS IMPORTS para as queries detalhadas de kit
import com.targosystem.varejo.promocoes.application.query.ListarTodosKitsComDetalhesProdutoQuery;
import com.targosystem.varejo.promocoes.application.query.ObterKitPromocionalPorIdComDetalhesProdutoQuery;


import java.util.List;
import java.util.Objects;

public class PromocaoService {

    private final CriarPromocaoUseCase criarPromocaoUseCase;
    private final AtualizarPromocaoUseCase atualizarPromocaoUseCase;
    private final ObterPromocaoPorIdQuery obterPromocaoPorIdQuery;
    private final ListarPromocoesAtivasQuery listarPromocoesAtivasQuery;
    private final CriarKitPromocionalUseCase criarKitPromocionalUseCase;
    private final ObterKitPromocionalPorIdQuery obterKitPromocionalPorIdQuery;
    private final ListarTodosKitsQuery listarTodosKitsQuery;
    private final ExcluirPromocaoUseCase excluirPromocaoUseCase;
    private final ExcluirKitPromocionalUseCase excluirKitPromocionalUseCase;

    private final ListarTodosKitsComDetalhesProdutoQuery listarTodosKitsComDetalhesProdutoQuery;
    private final ObterKitPromocionalPorIdComDetalhesProdutoQuery obterKitPromocionalPorIdComDetalhesProdutoQuery;


    public PromocaoService(
            CriarPromocaoUseCase criarPromocaoUseCase,
            AtualizarPromocaoUseCase atualizarPromocaoUseCase,
            ObterPromocaoPorIdQuery obterPromocaoPorIdQuery,
            ListarPromocoesAtivasQuery listarPromocoesAtivasQuery,
            CriarKitPromocionalUseCase criarKitPromocionalUseCase,
            ObterKitPromocionalPorIdQuery obterKitPromocionalPorIdQuery,
            ListarTodosKitsQuery listarTodosKitsQuery,
            ExcluirPromocaoUseCase excluirPromocaoUseCase,
            ExcluirKitPromocionalUseCase excluirKitPromocionalUseCase,
            ListarTodosKitsComDetalhesProdutoQuery listarTodosKitsComDetalhesProdutoQuery,
            ObterKitPromocionalPorIdComDetalhesProdutoQuery obterKitPromocionalPorIdComDetalhesProdutoQuery
    ) {
        this.criarPromocaoUseCase = Objects.requireNonNull(criarPromocaoUseCase, "CriarPromocaoUseCase cannot be null");
        this.atualizarPromocaoUseCase = Objects.requireNonNull(atualizarPromocaoUseCase, "AtualizarPromocaoUseCase cannot be null");
        this.obterPromocaoPorIdQuery = Objects.requireNonNull(obterPromocaoPorIdQuery, "ObterPromocaoPorIdQuery cannot be null");
        this.listarPromocoesAtivasQuery = Objects.requireNonNull(listarPromocoesAtivasQuery, "ListarPromocoesAtivasQuery cannot be null");
        this.criarKitPromocionalUseCase = Objects.requireNonNull(criarKitPromocionalUseCase, "CriarKitPromocionalUseCase cannot be null");
        this.obterKitPromocionalPorIdQuery = Objects.requireNonNull(obterKitPromocionalPorIdQuery, "ObterKitPromocionalPorIdQuery cannot be null");
        this.listarTodosKitsQuery = Objects.requireNonNull(listarTodosKitsQuery, "ListarTodosKitsQuery cannot be null");
        this.excluirPromocaoUseCase = Objects.requireNonNull(excluirPromocaoUseCase, "ExcluirClienteUseCase cannot be null");
        this.excluirKitPromocionalUseCase = Objects.requireNonNull(excluirKitPromocionalUseCase, "ExcluirKitPromocionalUseCase cannot be null");

        this.listarTodosKitsComDetalhesProdutoQuery = Objects.requireNonNull(listarTodosKitsComDetalhesProdutoQuery, "ListarTodosKitsComDetalhesProdutoQuery cannot be null");
        this.obterKitPromocionalPorIdComDetalhesProdutoQuery = Objects.requireNonNull(obterKitPromocionalPorIdComDetalhesProdutoQuery, "ObterKitPromocionalPorIdComDetalhesProdutoQuery cannot be null");
    }

    public PromocaoOutput criarPromocao(CriarPromocaoInput input) {
        return criarPromocaoUseCase.execute(input);
    }

    public PromocaoOutput atualizarPromocao(AtualizarPromocaoInput input) {
        return atualizarPromocaoUseCase.execute(input);
    }

    public PromocaoOutput obterPromocaoPorId(String id) {
        return obterPromocaoPorIdQuery.execute(id);
    }

    public List<PromocaoOutput> listarPromocoesAtivas() {
        return listarPromocoesAtivasQuery.execute();
    }

    public KitPromocionalOutput criarKitPromocional(CriarKitPromocionalInput input) {
        return criarKitPromocionalUseCase.execute(input);
    }

    public KitPromocionalOutput obterKitPromocionalPorId(String id) {
        return obterKitPromocionalPorIdQuery.execute(id);
    }

    public List<KitPromocionalOutput> listarTodosKits() {
        return listarTodosKitsQuery.execute();
    }

    public List<KitPromocionalOutput> listarTodosKitsComDetalhesProduto() {
        return listarTodosKitsComDetalhesProdutoQuery.execute();
    }

    public KitPromocionalOutput obterKitPromocionalPorIdComDetalhesProduto(String id) {
        return obterKitPromocionalPorIdComDetalhesProdutoQuery.execute(id);
    }

    public void excluirPromocao(String id) {
        excluirPromocaoUseCase.execute(id);
    }

    public void excluirKitPromocional(String id) {
        excluirKitPromocionalUseCase.execute(id);
    }
}