package com.targosystem.varejo.produtos.domain.repository;

import com.targosystem.varejo.produtos.domain.model.Produto;
import com.targosystem.varejo.produtos.domain.model.ProdutoId;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository {
    Produto save(Produto produto);
    Optional<Produto> findById(ProdutoId id);
    List<Produto> findAll();
    Optional<Produto> findByCodigoBarras(String codigoBarras); // Novo método
    boolean existsByCodigoBarras(String codigoBarras); // Novo método
    void delete(ProdutoId id); // Adicionado para demonstração no DAO
}