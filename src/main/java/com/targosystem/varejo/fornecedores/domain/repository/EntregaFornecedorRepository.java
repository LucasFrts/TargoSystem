package com.targosystem.varejo.fornecedores.domain.repository;

import com.targosystem.varejo.fornecedores.domain.model.EntregaFornecedor;
import com.targosystem.varejo.fornecedores.domain.model.FornecedorId;

import java.util.List;
import java.util.Optional;

public interface EntregaFornecedorRepository {
    EntregaFornecedor save(EntregaFornecedor entrega);
    Optional<EntregaFornecedor> findById(String id);
    List<EntregaFornecedor> findByFornecedorId(FornecedorId fornecedorId);
}