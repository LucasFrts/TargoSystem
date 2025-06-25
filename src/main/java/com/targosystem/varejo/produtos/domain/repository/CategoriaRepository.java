package com.targosystem.varejo.produtos.domain.repository;

import com.targosystem.varejo.produtos.domain.model.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaRepository {
    Optional<Categoria> findById(Integer id);
    Optional<Categoria> findByNome(String nome);
    List<Categoria> findAll();
    Categoria save(Categoria categoria); // Persiste ou atualiza
    void delete(Integer id);
    boolean existsByName(String nome);
}