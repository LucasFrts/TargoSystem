package com.targosystem.varejo.estoque.infra.persistence;

import com.targosystem.varejo.estoque.domain.model.MovimentacaoEstoque;
import com.targosystem.varejo.estoque.domain.repository.MovimentacaoEstoqueRepository;
import com.targosystem.varejo.estoque.infra.persistence.entity.MovimentacaoEstoqueJpaEntity;
import com.targosystem.varejo.estoque.infra.persistence.entity.ItemMovimentacaoEstoqueJpaEntity; // Importar
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import java.util.Optional;

public class MovimentacaoEstoqueDao implements MovimentacaoEstoqueRepository {

    private final EntityManager entityManager;

    public MovimentacaoEstoqueDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public MovimentacaoEstoque save(MovimentacaoEstoque movimentacaoEstoque) {
        MovimentacaoEstoqueJpaEntity jpaEntity = MovimentacaoEstoqueJpaEntity.fromDomain(movimentacaoEstoque);
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            MovimentacaoEstoqueJpaEntity existingEntity = entityManager.find(MovimentacaoEstoqueJpaEntity.class, jpaEntity.getId());
            if (existingEntity == null) {
                entityManager.persist(jpaEntity);
            } else {
                // Atualiza campos
                existingEntity.setTipo(jpaEntity.getTipo());
                existingEntity.setDataHora(jpaEntity.getDataHora());
                existingEntity.setMotivo(jpaEntity.getMotivo());
                existingEntity.setLocalOrigemId(jpaEntity.getLocalOrigemId());
                existingEntity.setLocalDestinoId(jpaEntity.getLocalDestinoId());

                // Atualiza a coleção de itens
                existingEntity.getItens().clear(); // Limpa itens antigos (orphanRemoval=true garante a exclusão no BD)
                for (ItemMovimentacaoEstoqueJpaEntity item : jpaEntity.getItens()) {
                    item.setMovimentacao(existingEntity); // Garante a ligação bidirecional
                    existingEntity.getItens().add(item);
                }
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
    public Optional<MovimentacaoEstoque> findById(String id) {
        try {
            // Fetch join para trazer os itens da movimentação ansiosamente
            return entityManager.createQuery(
                            "SELECT m FROM MovimentacaoEstoqueJpaEntity m LEFT JOIN FETCH m.itens WHERE m.id = :id",
                            MovimentacaoEstoqueJpaEntity.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst() // Usar findFirst para evitar duplicação no caso de múltiplos itens
                    .map(MovimentacaoEstoqueJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}