package com.targosystem.varejo.fornecedores.infra.persistence;

import com.targosystem.varejo.fornecedores.domain.model.EntregaFornecedor;
import com.targosystem.varejo.fornecedores.domain.model.FornecedorId;
import com.targosystem.varejo.fornecedores.domain.repository.EntregaFornecedorRepository;
import com.targosystem.varejo.fornecedores.infra.persistence.entity.EntregaFornecedorJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntregaFornecedorDao implements EntregaFornecedorRepository {

    private final EntityManager entityManager;

    public EntregaFornecedorDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public EntregaFornecedor save(EntregaFornecedor entrega) {
        EntregaFornecedorJpaEntity jpaEntity = EntregaFornecedorJpaEntity.fromDomain(entrega);
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            EntregaFornecedorJpaEntity existingEntity = entityManager.find(EntregaFornecedorJpaEntity.class, jpaEntity.getId());
            if (existingEntity == null) {
                entityManager.persist(jpaEntity);
            } else {
                // Atualiza campos da entidade existente
                existingEntity.setFornecedorId(jpaEntity.getFornecedorId());
                existingEntity.setNumeroPedidoCompra(jpaEntity.getNumeroPedidoCompra());
                existingEntity.setDataPrevistaEntrega(jpaEntity.getDataPrevistaEntrega());
                existingEntity.setDataRealizacaoEntrega(jpaEntity.getDataRealizacaoEntrega());
                existingEntity.setStatus(jpaEntity.getStatus());
                existingEntity.setQuantidadeItens(jpaEntity.getQuantidadeItens());
                existingEntity.setObservacoes(jpaEntity.getObservacoes());
                existingEntity.setAvaliacaoNota(jpaEntity.getAvaliacaoNota());
                existingEntity.setAvaliacaoComentario(jpaEntity.getAvaliacaoComentario());
                existingEntity.setDataAtualizacao(jpaEntity.getDataAtualizacao()); // Atualiza data de atualização

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
    public Optional<EntregaFornecedor> findById(String id) {
        try {
            return Optional.ofNullable(entityManager.find(EntregaFornecedorJpaEntity.class, id))
                    .map(EntregaFornecedorJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<EntregaFornecedor> findByFornecedorId(FornecedorId fornecedorId) {
        TypedQuery<EntregaFornecedorJpaEntity> query = entityManager.createQuery(
                "SELECT e FROM EntregaFornecedorJpaEntity e WHERE e.fornecedorId = :fornecedorId ORDER BY e.dataPrevistaEntrega DESC", EntregaFornecedorJpaEntity.class);
        query.setParameter("fornecedorId", fornecedorId.value()); // Usa o valor do FornecedorId
        return query.getResultList().stream()
                .map(EntregaFornecedorJpaEntity::toDomain)
                .collect(Collectors.toList());
    }
}