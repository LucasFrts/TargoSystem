package com.targosystem.varejo.seguranca.domain.repository;

import com.targosystem.varejo.seguranca.domain.model.Papel;
import java.util.List;
import java.util.Optional;

public interface PapelRepository {
    Optional<Papel> findById(Integer id);
    Optional<Papel> findByNome(String nome);
    List<Papel> findAll();
    Papel save(Papel papel); // Persiste ou atualiza
    void delete(Integer id);
    boolean existsByNome(String nome);
}