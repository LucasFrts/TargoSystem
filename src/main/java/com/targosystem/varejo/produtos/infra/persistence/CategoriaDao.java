package com.targosystem.varejo.produtos.infra.persistence;

import com.targosystem.varejo.produtos.domain.model.Categoria;
import com.targosystem.varejo.produtos.domain.repository.CategoriaRepository;
import com.targosystem.varejo.produtos.infra.persistence.entity.CategoriaJpaEntity;
import jakarta.persistence.EntityManager;
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
    public Optional<Categoria> findById(Integer id) {
        CategoriaJpaEntity entity = entityManager.find(CategoriaJpaEntity.class, id);
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
        CategoriaJpaEntity entity = toJpaEntity(categoria);
        if (entity.getId() == null || entityManager.find(CategoriaJpaEntity.class, entity.getId()) == null) {
            entityManager.persist(entity); // Nova categoria (ID será gerado)
            entityManager.flush(); // Garante que o ID seja gerado e disponível
        } else {
            entityManager.merge(entity); // Categoria existente
        }
        // Retorna a entidade de domínio atualizada, incluindo o ID se foi recém-persistida
        return toDomain(entity);
    }

    @Override
    public void delete(Integer id) {
        CategoriaJpaEntity entity = entityManager.find(CategoriaJpaEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public boolean existsByName(String nome) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM CategoriaJpaEntity c WHERE c.nome = :nome", Long.class);
        query.setParameter("nome", nome);
        return query.getSingleResult() > 0;
    }

    // --- Mappers entre Domínio e JPA Entity ---
    private Categoria toDomain(CategoriaJpaEntity entity) {
        if (entity == null) return null;
        return new Categoria(entity.getId(), entity.getNome(), entity.getDescricao());
    }

    private CategoriaJpaEntity toJpaEntity(Categoria domain) {
        if (domain == null) return null;
        return new CategoriaJpaEntity(domain.getId(), domain.getNome(), domain.getDescricao());
    }
}