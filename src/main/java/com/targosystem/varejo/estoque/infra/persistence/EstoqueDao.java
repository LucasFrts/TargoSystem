package com.targosystem.varejo.estoque.infra.persistence;

import com.targosystem.varejo.estoque.domain.model.Estoque;
import com.targosystem.varejo.estoque.domain.repository.EstoqueRepository;
import com.targosystem.varejo.estoque.infra.persistence.entity.EstoqueJpaEntity;
import com.targosystem.varejo.estoque.infra.persistence.entity.ItemEstoqueJpaEntity;
// REMOVIDA: import com.targosystem.varejo.estoque.infra.persistence.entity.MovimentacaoEstoqueJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

public class EstoqueDao implements EstoqueRepository {

    private final EntityManager entityManager;

    public EstoqueDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Estoque save(Estoque estoque) {
        EstoqueJpaEntity jpaEntity = EstoqueJpaEntity.fromDomain(estoque);
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            EstoqueJpaEntity existingEntity = entityManager.find(EstoqueJpaEntity.class, jpaEntity.getId());
            if (existingEntity == null) {
                entityManager.persist(jpaEntity);
            } else {
                existingEntity.setProdutoId(jpaEntity.getProdutoId());
                existingEntity.setLocalEstoque(jpaEntity.getLocalEstoque()); // ATUALIZA O LOCAL

                // === ATUALIZAÇÃO DA COLEÇÃO DE ITENS DE ESTOQUE ===
                existingEntity.getItensEstoque().clear();
                for (ItemEstoqueJpaEntity item : jpaEntity.getItensEstoque()) {
                    item.setEstoque(existingEntity); // Garante a ligação bidirecional
                    item.setLocalEstoqueId(existingEntity.getLocalEstoque().getId()); // Garante o ID do local no item
                    existingEntity.getItensEstoque().add(item);
                }

                // REMOVIDO: Movimentações não são mais salvas via Estoque
                // existingEntity.getMovimentacoes().clear();
                // for (MovimentacaoEstoqueJpaEntity mov : jpaEntity.getMovimentacoes()) {
                //     mov.setEstoque(existingEntity);
                //     existingEntity.getMovimentacoes().add(mov);
                // }

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
    public Optional<Estoque> findById(String id) {
        try {
            TypedQuery<EstoqueJpaEntity> query = entityManager.createQuery(
                    "SELECT e FROM EstoqueJpaEntity e LEFT JOIN FETCH e.itensEstoque LEFT JOIN FETCH e.localEstoque WHERE e.id = :id", EstoqueJpaEntity.class);
            query.setParameter("id", id);
            return query.getResultStream().findFirst().map(EstoqueJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Estoque> findByProdutoIdAndLocalEstoqueId(String produtoId, String localEstoqueId) {
        try {
            TypedQuery<EstoqueJpaEntity> query = entityManager.createQuery(
                    "SELECT e FROM EstoqueJpaEntity e LEFT JOIN FETCH e.itensEstoque LEFT JOIN FETCH e.localEstoque WHERE e.produtoId = :produtoId AND e.localEstoque.id = :localEstoqueId", EstoqueJpaEntity.class);
            query.setParameter("produtoId", produtoId);
            query.setParameter("localEstoqueId", localEstoqueId);
            return query.getResultStream().findFirst().map(EstoqueJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Estoque> findByProdutoId(String produtoId) {
        try {
            TypedQuery<EstoqueJpaEntity> query = entityManager.createQuery(
                    "SELECT e FROM EstoqueJpaEntity e LEFT JOIN FETCH e.itensEstoque WHERE e.produtoId = :produtoId", EstoqueJpaEntity.class); // FIXED LINE
            query.setParameter("produtoId", produtoId);
            return query.getResultStream().findFirst().map(EstoqueJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}