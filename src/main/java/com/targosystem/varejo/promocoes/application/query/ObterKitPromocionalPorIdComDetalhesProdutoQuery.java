package com.targosystem.varejo.promocoes.application.query;

import com.targosystem.varejo.promocoes.application.output.KitPromocionalOutput;
import com.targosystem.varejo.promocoes.domain.repository.KitPromocionalRepository;
import com.targosystem.varejo.produtos.application.ProdutoService; // Para buscar os nomes dos produtos
import com.targosystem.varejo.shared.domain.DomainException;

import java.util.Objects;

public class ObterKitPromocionalPorIdComDetalhesProdutoQuery {

    private final KitPromocionalRepository kitPromocionalRepository;
    private final ProdutoService produtoService; // Injetado para buscar detalhes do produto

    public ObterKitPromocionalPorIdComDetalhesProdutoQuery(KitPromocionalRepository kitPromocionalRepository, ProdutoService produtoService) {
        this.kitPromocionalRepository = Objects.requireNonNull(kitPromocionalRepository, "KitPromocionalRepository cannot be null");
        this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService cannot be null");
    }

    public KitPromocionalOutput execute(String id) {
        return kitPromocionalRepository.findById(id)
                .map(kit -> KitPromocionalOutput.from(kit, produtoService)) // Usa o 'from' que requer ProdutoService
                .orElseThrow(() -> new DomainException("Kit promocional com ID " + id + " não encontrado."));
    }
}