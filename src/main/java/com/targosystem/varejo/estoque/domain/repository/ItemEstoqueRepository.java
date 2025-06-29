package com.targosystem.varejo.estoque.domain.repository;

import com.targosystem.varejo.estoque.domain.model.ItemEstoque;

import java.util.List;
import java.util.Optional;

public interface ItemEstoqueRepository {

    ItemEstoque save(ItemEstoque item);

    Optional<ItemEstoque> findById(String id);

    List<ItemEstoque> findAll();

    void delete(String id);

    List<ItemEstoque> findByLocalEstoqueId(String localEstoqueId);

    List<ItemEstoque> findByProdutoIdAndLocalEstoqueId(String produtoId, String localEstoqueId);
}
