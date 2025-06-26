package com.targosystem.varejo.estoque.infra.persistence;

import com.targosystem.varejo.estoque.domain.model.Estoque;
import com.targosystem.varejo.estoque.domain.repository.EstoqueRepository;
import com.targosystem.varejo.estoque.infra.persistence.entity.EstoqueJpaEntity;
import com.targosystem.varejo.estoque.infra.persistence.entity.ItemEstoqueJpaEntity;
import com.targosystem.varejo.estoque.infra.persistence.entity.MovimentacaoEstoqueJpaEntity;
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
                // Se for uma nova entidade de estoque, persistir diretamente.
                entityManager.persist(jpaEntity);
            } else {
                // Se já existir, atualizamos seus campos e coleções.
                existingEntity.setProdutoId(jpaEntity.getProdutoId()); // Atualiza produtoId se ele puder ser alterado

                // === ATUALIZAÇÃO DA COLEÇÃO DE ITENS DE ESTOQUE ===
                // 1. Limpa a coleção existente na entidade gerenciada.
                // Isso, combinado com orphanRemoval=true, fará com que o JPA remova os itens antigos do banco.
                existingEntity.getItensEstoque().clear();
                // 2. Adiciona todos os novos itens (do domínio para a JPA) à coleção.
                // É crucial que cada item na nova lista tenha sua referência ao pai (estoque) setada.
                for (ItemEstoqueJpaEntity item : jpaEntity.getItensEstoque()) {
                    item.setEstoque(existingEntity); // Garante a ligação bidirecional
                    existingEntity.getItensEstoque().add(item);
                }

                // === ATUALIZAÇÃO DA COLEÇÃO DE MOVIMENTAÇÕES DE ESTOQUE ===
                existingEntity.getMovimentacoes().clear();
                for (MovimentacaoEstoqueJpaEntity mov : jpaEntity.getMovimentacoes()) {
                    mov.setEstoque(existingEntity); // Garante a ligação bidirecional
                    existingEntity.getMovimentacoes().add(mov);
                }

                // O merge propaga as mudanças para as coleções gerenciadas
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
            // Usa fetch join para trazer itensEstoque e movimentacoes ansiosamente
            // Isso evita N+1 problems ao carregar o agregado completo.
            TypedQuery<EstoqueJpaEntity> query = entityManager.createQuery(
                    "SELECT e FROM EstoqueJpaEntity e LEFT JOIN FETCH e.itensEstoque LEFT JOIN FETCH e.movimentacoes WHERE e.id = :id", EstoqueJpaEntity.class);
            query.setParameter("id", id);
            // Garante que a lista não seja duplicada no caso de múltiplos itens/movimentações (resultando em múltiplas linhas)
            return query.getResultStream().findFirst().map(EstoqueJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Estoque> findByProdutoId(String produtoId) {
        try {
            // Similar ao findById, usa fetch join
            TypedQuery<EstoqueJpaEntity> query = entityManager.createQuery(
                    "SELECT e FROM EstoqueJpaEntity e LEFT JOIN FETCH e.itensEstoque LEFT JOIN FETCH e.movimentacoes WHERE e.produtoId = :produtoId", EstoqueJpaEntity.class);
            query.setParameter("produtoId", produtoId);
            // Garante que a lista não seja duplicada no caso de múltiplos itens/movimentações
            return query.getResultStream().findFirst().map(EstoqueJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}