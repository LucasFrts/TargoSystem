package com.targosystem.varejo.seguranca.infra.persistence;

import com.targosystem.varejo.seguranca.domain.model.Papel;
import com.targosystem.varejo.seguranca.domain.repository.PapelRepository;
import com.targosystem.varejo.seguranca.infra.persistence.entity.PapelJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PapelDao implements PapelRepository {

    private final EntityManager entityManager;

    public PapelDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Papel> findById(Integer id) {
        PapelJpaEntity entity = entityManager.find(PapelJpaEntity.class, id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<Papel> findByNome(String nome) {
        try {
            TypedQuery<PapelJpaEntity> query = entityManager.createQuery(
                    "SELECT p FROM PapelJpaEntity p WHERE p.nome = :nome", PapelJpaEntity.class);
            query.setParameter("nome", nome);
            return Optional.of(query.getSingleResult()).map(this::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Papel> findAll() {
        TypedQuery<PapelJpaEntity> query = entityManager.createQuery(
                "SELECT p FROM PapelJpaEntity p", PapelJpaEntity.class);
        return query.getResultList().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Papel save(Papel papel) {
        PapelJpaEntity entity = toJpaEntity(papel);
        if (entity.getId() == null || entityManager.find(PapelJpaEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
            entityManager.flush(); // Garante que o ID seja gerado e disponível
        } else {
            entityManager.merge(entity);
        }
        return toDomain(entity);
    }

    @Override
    public void delete(Integer id) {
        PapelJpaEntity entity = entityManager.find(PapelJpaEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public boolean existsByNome(String nome) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(p) FROM PapelJpaEntity p WHERE p.nome = :nome", Long.class);
        query.setParameter("nome", nome);
        return query.getSingleResult() > 0;
    }

    // --- Mappers entre Domínio e JPA Entity ---
    private Papel toDomain(PapelJpaEntity entity) {
        if (entity == null) return null;
        return new Papel(entity.getId(), entity.getNome(), entity.getDescricao());
    }

    private PapelJpaEntity toJpaEntity(Papel domain) {
        if (domain == null) return null;
        return new PapelJpaEntity(domain.getId(), domain.getNome(), domain.getDescricao());
    }
}