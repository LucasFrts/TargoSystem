package com.targosystem.varejo.promocoes.domain.repository;

import com.targosystem.varejo.promocoes.domain.model.Promocao;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromocaoRepository {
    Promocao save(Promocao promocao);
    Optional<Promocao> findById(String id);
    List<Promocao> findAll();
    List<Promocao> findActivePromotions(LocalDateTime now);
    void delete(Promocao promocao);
}