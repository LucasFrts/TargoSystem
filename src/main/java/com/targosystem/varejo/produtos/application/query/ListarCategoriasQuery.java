package com.targosystem.varejo.produtos.application.query;

import com.targosystem.varejo.produtos.application.output.CategoriaOutput;
import com.targosystem.varejo.produtos.domain.repository.CategoriaRepository;

import java.util.List;
import java.util.stream.Collectors;

public class ListarCategoriasQuery {

    private final CategoriaRepository categoriaRepository;

    public ListarCategoriasQuery(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaOutput> execute() {
        return categoriaRepository.findAll().stream()
                .map(CategoriaOutput::fromDomain)
                .collect(Collectors.toList());
    }
}