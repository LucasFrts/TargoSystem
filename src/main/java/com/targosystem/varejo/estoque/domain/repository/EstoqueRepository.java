package com.targosystem.varejo.estoque.domain.repository;

import com.targosystem.varejo.estoque.domain.model.Estoque;
import java.util.Optional;

public interface EstoqueRepository {
    Estoque save(Estoque estoque);
    Optional<Estoque> findById(String id);
    Optional<Estoque> findByProdutoId(String produtoId); // Para buscar o estoque de um produto específico
}