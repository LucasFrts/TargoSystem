package com.targosystem.varejo.produtos.infra.persistence;

import com.targosystem.varejo.produtos.domain.model.Produto;
import com.targosystem.varejo.produtos.domain.model.ProdutoId;
import com.targosystem.varejo.produtos.domain.repository.ProdutoRepository;
import com.targosystem.varejo.produtos.infra.persistence.entity.ProdutoJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProdutoDao implements ProdutoRepository {

    private final EntityManager entityManager;

    public ProdutoDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Produto save(Produto produto) {
        ProdutoJpaEntity jpaEntity = ProdutoJpaEntity.fromDomain(produto);
        try {

            ProdutoJpaEntity existingEntity = entityManager.find(ProdutoJpaEntity.class, jpaEntity.getId());
            if (existingEntity == null) {
                entityManager.persist(jpaEntity);
            } else {
                // Atualizar campos da entidade existente
                existingEntity.setNome(jpaEntity.getNome());
                existingEntity.setDescricao(jpaEntity.getDescricao());
                existingEntity.setPrecoVenda(jpaEntity.getPrecoVenda());
                existingEntity.setCodigoBarras(jpaEntity.getCodigoBarras()); // Atualiza o código de barras
                existingEntity.setStatus(jpaEntity.getStatus());
                existingEntity.setDataAtualizacao(jpaEntity.getDataAtualizacao());
                jpaEntity = entityManager.merge(existingEntity);
            }
            return jpaEntity.toDomain();
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public Optional<Produto> findById(ProdutoId id) {
        try {
            return Optional.ofNullable(entityManager.find(ProdutoJpaEntity.class, id.value()))
                    .map(ProdutoJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Produto> findAll() {
        TypedQuery<ProdutoJpaEntity> query = entityManager.createQuery(
                "SELECT p FROM ProdutoJpaEntity p", ProdutoJpaEntity.class);
        return query.getResultList().stream()
                .map(ProdutoJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Produto> findByCodigoBarras(String codigoBarras) {
        try {
            TypedQuery<ProdutoJpaEntity> query = entityManager.createQuery(
                    "SELECT p FROM ProdutoJpaEntity p WHERE p.codigoBarras = :codigoBarras", ProdutoJpaEntity.class);
            query.setParameter("codigoBarras", codigoBarras);
            // Se getSingleResult lançar NoResultException, o catch abaixo lida
            return Optional.of(query.getSingleResult()).map(ProdutoJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByCodigoBarras(String codigoBarras) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(p) FROM ProdutoJpaEntity p WHERE p.codigoBarras = :codigoBarras", Long.class);
            query.setParameter("codigoBarras", codigoBarras);

            Long count = query.getSingleResult();

            return count > 0;
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public void delete(ProdutoId id) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin(); // Inicia a transação para operação de exclusão

            ProdutoJpaEntity entity = entityManager.find(ProdutoJpaEntity.class, id.value());
            if (entity != null) {
                entityManager.remove(entity);
            }

            transaction.commit(); // Comita a transação
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback(); // Faz rollback em caso de erro
            }
            throw e; // Relança a exceção
        }
    }
}