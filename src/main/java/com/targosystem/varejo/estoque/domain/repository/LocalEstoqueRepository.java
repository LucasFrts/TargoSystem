package com.targosystem.varejo.estoque.domain.repository;

import com.targosystem.varejo.estoque.domain.model.LocalEstoque;
import com.targosystem.varejo.estoque.domain.model.TipoLocal;

import java.util.List;
import java.util.Optional;

public interface LocalEstoqueRepository {
    LocalEstoque save(LocalEstoque localEstoque);
    Optional<LocalEstoque> findById(String id);
    List<LocalEstoque> findAll();
    List<LocalEstoque> findByTipo(TipoLocal tipo);
    void delete(String id);
}