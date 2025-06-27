package com.targosystem.varejo.promocoes.infra.persistence;

import com.targosystem.varejo.promocoes.domain.model.KitPromocional;
import com.targosystem.varejo.promocoes.domain.repository.KitPromocionalRepository;
import com.targosystem.varejo.promocoes.infra.persistence.entity.KitPromocionalJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class KitPromocionalDao implements KitPromocionalRepository {

    private final EntityManager entityManager;

    public KitPromocionalDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public KitPromocional save(KitPromocional kitPromocional) {
        KitPromocionalJpaEntity jpaEntity = KitPromocionalJpaEntity.fromDomain(kitPromocional);
        try {
            KitPromocionalJpaEntity existingEntity = entityManager.find(KitPromocionalJpaEntity.class, jpaEntity.getId());
            if (existingEntity == null) {
                entityManager.persist(jpaEntity);
            } else {
                existingEntity.setNome(jpaEntity.getNome());
                existingEntity.setDescricao(jpaEntity.getDescricao());
                existingEntity.setPrecoFixoKit(jpaEntity.getPrecoFixoKit());
                existingEntity.setItens(jpaEntity.getItens());
                jpaEntity = entityManager.merge(existingEntity);
            }
            entityManager.flush();
            return jpaEntity.toDomain();
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public Optional<KitPromocional> findById(String id) {
        KitPromocionalJpaEntity jpaEntity = entityManager.find(KitPromocionalJpaEntity.class, id);
        return Optional.ofNullable(jpaEntity).map(KitPromocionalJpaEntity::toDomain);
    }

    @Override
    public List<KitPromocional> findAll() {
        TypedQuery<KitPromocionalJpaEntity> query = entityManager.createQuery("SELECT k FROM KitPromocionalJpaEntity k", KitPromocionalJpaEntity.class);
        return query.getResultList().stream()
                .map(KitPromocionalJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(KitPromocional kitPromocional) {
        try {
            KitPromocionalJpaEntity jpaEntity = entityManager.find(KitPromocionalJpaEntity.class, kitPromocional.getId());
            if (jpaEntity != null) {
                entityManager.remove(jpaEntity);
            }
            entityManager.flush();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}