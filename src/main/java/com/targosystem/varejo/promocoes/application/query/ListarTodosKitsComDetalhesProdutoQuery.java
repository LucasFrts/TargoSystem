package com.targosystem.varejo.promocoes.application.query;

import com.targosystem.varejo.promocoes.application.output.KitPromocionalOutput;
import com.targosystem.varejo.promocoes.domain.repository.KitPromocionalRepository;
import com.targosystem.varejo.produtos.application.ProdutoService; // Para buscar os nomes dos produtos

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListarTodosKitsComDetalhesProdutoQuery {

    private final KitPromocionalRepository kitPromocionalRepository;
    private final ProdutoService produtoService; // Injetado para buscar detalhes do produto

    public ListarTodosKitsComDetalhesProdutoQuery(KitPromocionalRepository kitPromocionalRepository, ProdutoService produtoService) {
        this.kitPromocionalRepository = Objects.requireNonNull(kitPromocionalRepository, "KitPromocionalRepository cannot be null");
        this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService cannot be null");
    }

    public List<KitPromocionalOutput> execute() {
        return kitPromocionalRepository.findAll().stream()
                .map(kit -> KitPromocionalOutput.from(kit, produtoService))
                .collect(Collectors.toUnmodifiableList());
    }
}