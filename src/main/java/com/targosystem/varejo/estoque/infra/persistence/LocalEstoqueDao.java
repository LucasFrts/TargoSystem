package com.targosystem.varejo.estoque.infra.persistence;

import com.targosystem.varejo.estoque.domain.model.LocalEstoque;
import com.targosystem.varejo.estoque.domain.model.TipoLocal;
import com.targosystem.varejo.estoque.domain.repository.LocalEstoqueRepository;
import com.targosystem.varejo.estoque.infra.persistence.entity.LocalEstoqueJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalEstoqueDao implements LocalEstoqueRepository {

    private final EntityManager entityManager;

    public LocalEstoqueDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public LocalEstoque save(LocalEstoque localEstoque) {
        LocalEstoqueJpaEntity jpaEntity = LocalEstoqueJpaEntity.fromDomain(localEstoque);
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            LocalEstoqueJpaEntity existingEntity = entityManager.find(LocalEstoqueJpaEntity.class, jpaEntity.getId());
            if (existingEntity == null) {
                entityManager.persist(jpaEntity);
            } else {
                existingEntity.setNome(jpaEntity.getNome());
                existingEntity.setTipo(jpaEntity.getTipo());
                existingEntity.setAtivo(jpaEntity.isAtivo());
                existingEntity.setDataAtualizacao(jpaEntity.getDataAtualizacao());
                jpaEntity = entityManager.merge(existingEntity);
            }
            transaction.commit();
            return jpaEntity.toDomain();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<LocalEstoque> findById(String id) {
        try {
            return Optional.ofNullable(entityManager.find(LocalEstoqueJpaEntity.class, id))
                    .map(LocalEstoqueJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<LocalEstoque> findAll() {
        TypedQuery<LocalEstoqueJpaEntity> query = entityManager.createQuery(
                "SELECT l FROM LocalEstoqueJpaEntity l", LocalEstoqueJpaEntity.class);
        return query.getResultList().stream()
                .map(LocalEstoqueJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocalEstoque> findByTipo(TipoLocal tipo) {
        TypedQuery<LocalEstoqueJpaEntity> query = entityManager.createQuery(
                "SELECT l FROM LocalEstoqueJpaEntity l WHERE l.tipo = :tipo", LocalEstoqueJpaEntity.class);
        query.setParameter("tipo", tipo);
        return query.getResultList().stream()
                .map(LocalEstoqueJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            LocalEstoqueJpaEntity entity = entityManager.find(LocalEstoqueJpaEntity.class, id);
            if (entity != null) {
                entityManager.remove(entity);
            }
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}