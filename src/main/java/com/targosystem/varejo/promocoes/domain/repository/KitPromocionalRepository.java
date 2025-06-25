package com.targosystem.varejo.promocoes.domain.repository;

import com.targosystem.varejo.promocoes.domain.model.KitPromocional;
import java.util.List;
import java.util.Optional;

public interface KitPromocionalRepository {
    KitPromocional save(KitPromocional kitPromocional);
    Optional<KitPromocional> findById(String id);
    List<KitPromocional> findAll();
    void delete(KitPromocional kitPromocional);
}