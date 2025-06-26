package com.targosystem.varejo.fornecedores.domain.repository;

import com.targosystem.varejo.fornecedores.domain.model.Fornecedor;
import com.targosystem.varejo.fornecedores.domain.model.FornecedorId; // Importar FornecedorId
import java.util.Optional;
import java.util.List;

public interface FornecedorRepository {
    Fornecedor save(Fornecedor fornecedor);
    Optional<Fornecedor> findById(FornecedorId id); // Usando FornecedorId
    Optional<Fornecedor> findByCnpj(String cnpj);
    List<Fornecedor> findAll();
}