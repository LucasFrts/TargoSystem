package com.targosystem.varejo.estoque.domain.repository;

import com.targosystem.varejo.estoque.domain.model.MovimentacaoEstoque;
import java.util.Optional;

public interface MovimentacaoEstoqueRepository {
    MovimentacaoEstoque save(MovimentacaoEstoque movimentacaoEstoque);
    Optional<MovimentacaoEstoque> findById(String id);
}