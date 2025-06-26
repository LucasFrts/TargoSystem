package com.targosystem.varejo.vendas.domain.repository;

import com.targosystem.varejo.vendas.domain.model.Venda;
import com.targosystem.varejo.vendas.domain.model.VendaId;

import java.util.List;
import java.util.Optional;

public interface VendaRepository {
    Venda save(Venda venda);
    Optional<Venda> findById(VendaId id);
    List<Venda> findAll();
    // ... outros métodos de busca ou remoção se necessário
}