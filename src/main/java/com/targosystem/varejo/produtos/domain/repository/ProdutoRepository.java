package com.targosystem.varejo.produtos.domain.repository;

import com.targosystem.varejo.produtos.domain.model.Produto;
import com.targosystem.varejo.produtos.domain.model.ProdutoId;
import java.util.Optional;
import java.util.List;

public interface ProdutoRepository {
    Optional<Produto> findById(ProdutoId id);
    Optional<Produto> findByCodigoBarras(String codigoBarras);
    List<Produto> findAll();
    Produto save(Produto produto); // Persiste ou atualiza
    void delete(ProdutoId id);
    boolean existsByCodigoBarras(String codigoBarras);
}