package com.targosystem.varejo.promocoes.application;

import com.targosystem.varejo.promocoes.application.input.AtualizarPromocaoInput;
import com.targosystem.varejo.promocoes.application.input.CriarKitPromocionalInput;
import com.targosystem.varejo.promocoes.application.input.CriarPromocaoInput;
import com.targosystem.varejo.promocoes.application.output.KitPromocionalOutput;
import com.targosystem.varejo.promocoes.application.output.PromocaoOutput;
import com.targosystem.varejo.promocoes.application.query.ListarPromocoesAtivasQuery;
import com.targosystem.varejo.promocoes.application.query.ObterKitPromocionalPorIdQuery;
import com.targosystem.varejo.promocoes.application.query.ObterPromocaoPorIdQuery;
import com.targosystem.varejo.promocoes.application.usecases.CriarKitPromocionalUseCase;
import com.targosystem.varejo.promocoes.application.usecases.CriarPromocaoUseCase;
import com.targosystem.varejo.promocoes.application.usecases.AtualizarPromocaoUseCase;

import java.util.List;
import java.util.Objects;

public class PromocaoService {

    private final CriarPromocaoUseCase criarPromocaoUseCase;
    private final AtualizarPromocaoUseCase atualizarPromocaoUseCase;
    private final ObterPromocaoPorIdQuery obterPromocaoPorIdQuery;
    private final ListarPromocoesAtivasQuery listarPromocoesAtivasQuery;
    private final CriarKitPromocionalUseCase criarKitPromocionalUseCase; // NOVO
    private final ObterKitPromocionalPorIdQuery obterKitPromocionalPorIdQuery; // NOVO


    public PromocaoService(
            CriarPromocaoUseCase criarPromocaoUseCase,
            AtualizarPromocaoUseCase atualizarPromocaoUseCase,
            ObterPromocaoPorIdQuery obterPromocaoPorIdQuery,
            ListarPromocoesAtivasQuery listarPromocoesAtivasQuery,
            CriarKitPromocionalUseCase criarKitPromocionalUseCase,
            ObterKitPromocionalPorIdQuery obterKitPromocionalPorIdQuery
            ) {
        this.criarPromocaoUseCase = Objects.requireNonNull(criarPromocaoUseCase, "CriarPromocaoUseCase cannot be null");
        this.atualizarPromocaoUseCase = Objects.requireNonNull(atualizarPromocaoUseCase, "AtualizarPromocaoUseCase cannot be null");
        this.obterPromocaoPorIdQuery = Objects.requireNonNull(obterPromocaoPorIdQuery, "ObterPromocaoPorIdQuery cannot be null");
        this.listarPromocoesAtivasQuery = Objects.requireNonNull(listarPromocoesAtivasQuery, "ListarPromocoesAtivasQuery cannot be null");
        this.criarKitPromocionalUseCase = Objects.requireNonNull(criarKitPromocionalUseCase, "CriarKitPromocionalUseCase cannot be null");
        this.obterKitPromocionalPorIdQuery = Objects.requireNonNull(obterKitPromocionalPorIdQuery, "ObterKitPromocionalPorIdQuery cannot be null");
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
}