package com.targosystem.varejo.promocoes.infra.persistence;

import com.targosystem.varejo.promocoes.domain.model.Promocao;
import com.targosystem.varejo.promocoes.domain.repository.PromocaoRepository;
import com.targosystem.varejo.promocoes.infra.persistence.entity.PromocaoJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PromocaoDao implements PromocaoRepository {

    private final EntityManager entityManager;

    public PromocaoDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Promocao save(Promocao promocao) {
        PromocaoJpaEntity jpaEntity = PromocaoJpaEntity.fromDomain(promocao);
        try {
            PromocaoJpaEntity existingEntity = entityManager.find(PromocaoJpaEntity.class, jpaEntity.getId());
            if (existingEntity == null) {
                entityManager.persist(jpaEntity);
            } else {
                existingEntity.setNome(jpaEntity.getNome());
                existingEntity.setTipoDesconto(jpaEntity.getTipoDesconto());
                existingEntity.setValorDesconto(jpaEntity.getValorDesconto());
                existingEntity.setDataInicio(jpaEntity.getDataInicio());
                existingEntity.setDataFim(jpaEntity.getDataFim());
                existingEntity.setAtiva(jpaEntity.isAtiva());
                existingEntity.setProdutoIds(jpaEntity.getProdutoIds());
                existingEntity.setDataAtualizacao(jpaEntity.getDataAtualizacao());

                jpaEntity = entityManager.merge(existingEntity);
            }
            entityManager.flush();
            return jpaEntity.toDomain();
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public Optional<Promocao> findById(String id) {
        PromocaoJpaEntity jpaEntity = entityManager.find(PromocaoJpaEntity.class, id);
        return Optional.ofNullable(jpaEntity).map(PromocaoJpaEntity::toDomain);
    }

    @Override
    public List<Promocao> findAll() {
        TypedQuery<PromocaoJpaEntity> query = entityManager.createQuery("SELECT p FROM PromocaoJpaEntity p", PromocaoJpaEntity.class);
        return query.getResultList().stream()
                .map(PromocaoJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Promocao> findActivePromotions(LocalDateTime now) {
        TypedQuery<PromocaoJpaEntity> query = entityManager.createQuery(
                "SELECT p FROM PromocaoJpaEntity p WHERE p.ativa = TRUE AND :now BETWEEN p.dataInicio AND p.dataFim", PromocaoJpaEntity.class);
        query.setParameter("now", now);
        return query.getResultList().stream()
                .map(PromocaoJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Promocao promocao) {
        try {
            PromocaoJpaEntity jpaEntity = entityManager.find(PromocaoJpaEntity.class, promocao.getId());
            if (jpaEntity != null) {
                entityManager.remove(jpaEntity);
            }
            entityManager.flush();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}