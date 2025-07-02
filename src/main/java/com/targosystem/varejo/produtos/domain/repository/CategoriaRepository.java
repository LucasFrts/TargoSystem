package com.targosystem.varejo.produtos.domain.repository;

import com.targosystem.varejo.produtos.domain.model.Categoria;
import com.targosystem.varejo.produtos.domain.model.CategoriaId;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository {
    Optional<Categoria> findById(CategoriaId id);
    Optional<Categoria> findByNome(String nome);
    List<Categoria> findAll();
    Categoria save(Categoria categoria);
    void delete(CategoriaId id);
    boolean existsByName(String nome);
}