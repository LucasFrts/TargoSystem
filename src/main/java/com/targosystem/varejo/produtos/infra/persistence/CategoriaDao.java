package com.targosystem.varejo.produtos.infra.persistence;

import com.targosystem.varejo.produtos.domain.model.Categoria;
import com.targosystem.varejo.produtos.domain.model.CategoriaId;
import com.targosystem.varejo.produtos.domain.repository.CategoriaRepository;
import com.targosystem.varejo.produtos.infra.persistence.entity.CategoriaJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CategoriaDao implements CategoriaRepository {

    private final EntityManager entityManager;

    public CategoriaDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Categoria> findById(CategoriaId id) {
        CategoriaJpaEntity entity = entityManager.find(CategoriaJpaEntity.class, id.value());
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<Categoria> findByNome(String nome) {
        try {
            TypedQuery<CategoriaJpaEntity> query = entityManager.createQuery(
                    "SELECT c FROM CategoriaJpaEntity c WHERE c.nome = :nome", CategoriaJpaEntity.class);
            query.setParameter("nome", nome);
            return Optional.of(query.getSingleResult()).map(this::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Categoria> findAll() {
        TypedQuery<CategoriaJpaEntity> query = entityManager.createQuery(
                "SELECT c FROM CategoriaJpaEntity c", CategoriaJpaEntity.class);
        return query.getResultList().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Categoria save(Categoria categoria) {
        try {
            CategoriaJpaEntity jpaEntity = CategoriaJpaEntity.fromDomain(categoria);
            jpaEntity = entityManager.merge(jpaEntity);

            entityManager.flush();
            return toDomain(jpaEntity);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public void delete(CategoriaId id) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            if (!transaction.isActive()) {
                transaction.begin();
            }

            CategoriaJpaEntity entity = entityManager.find(CategoriaJpaEntity.class, id.value());
            if (entity != null) {
                entityManager.remove(entity);
            }

            if (transaction.isActive() && !transaction.getRollbackOnly()) {
                transaction.commit();
            }
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public boolean existsByName(String nome) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(c) FROM CategoriaJpaEntity c WHERE c.nome = :nome", Long.class);
            query.setParameter("nome", nome);
            Long count = query.getSingleResult();
            return count > 0;
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private Categoria toDomain(CategoriaJpaEntity entity) {
        if (entity == null) return null;
        return new Categoria(
                new CategoriaId(entity.getId()),
                entity.getNome(),
                entity.getDescricao(),
                entity.getDataCriacao(),
                entity.getDataAtualizacao()
        );
    }

}