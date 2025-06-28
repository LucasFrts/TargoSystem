package com.targosystem.varejo.estoque.infra.persistence;

import com.targosystem.varejo.estoque.domain.model.ItemEstoque;
import com.targosystem.varejo.estoque.domain.repository.ItemEstoqueRepository;
import com.targosystem.varejo.estoque.infra.persistence.entity.ItemEstoqueJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemEstoqueDao implements ItemEstoqueRepository {

    private final EntityManager entityManager;

    public ItemEstoqueDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ItemEstoque save(ItemEstoque item) {
        ItemEstoqueJpaEntity jpaEntity = ItemEstoqueJpaEntity.fromDomain(item);
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            ItemEstoqueJpaEntity existingEntity = entityManager.find(ItemEstoqueJpaEntity.class, jpaEntity.getId());
            if (existingEntity == null) {
                // Se não existe, persiste.
                // ATENÇÃO: A referência ao EstoqueJpaEntity pai (jpaEntity.getEstoque()) precisa ser gerenciada.
                // Se o ItemEstoque estiver sendo salvo de forma isolada, você precisa garantir que o 'estoque' já exista no EM.
                // No contexto atual, ItemEstoque é parte de Estoque, então ele é salvo via EstoqueJpaEntity.save().
                // Este 'save' aqui seria para operações diretas no ItemEstoque.
                // Para uma lógica mais robusta, considere como ItemEstoque é criado/atualizado.
                // Por agora, vamos assumir que 'item.getEstoqueId()' referencia um EstoqueJpaEntity existente.
                // Para isso, precisamos carregar a referência:
                // EstoqueJpaEntity estoqueParent = entityManager.getReference(EstoqueJpaEntity.class, item.getEstoqueId());
                // jpaEntity.setEstoque(estoqueParent);
                entityManager.persist(jpaEntity);
            } else {
                // Atualiza os campos do existente
                existingEntity.setProdutoId(jpaEntity.getProdutoId());
                existingEntity.setQuantidade(jpaEntity.getQuantidade());
                existingEntity.setLote(jpaEntity.getLote());
                existingEntity.setLocalizacao(jpaEntity.getLocalizacao());
                existingEntity.setLocalEstoqueId(jpaEntity.getLocalEstoqueId());
                // existingEntity.setEstoque(jpaEntity.getEstoque()); // Cuidado ao atualizar a referência ManyToOne
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
    public Optional<ItemEstoque> findById(String id) {
        try {
            // Inclua fetch join para EstoqueJpaEntity se você precisar acessar os dados do estoque pai no toDomain
            TypedQuery<ItemEstoqueJpaEntity> query = entityManager.createQuery(
                    "SELECT ie FROM ItemEstoqueJpaEntity ie WHERE ie.id = :id", ItemEstoqueJpaEntity.class);
            query.setParameter("id", id);
            return query.getResultStream().findFirst().map(ItemEstoqueJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ItemEstoque> findAll() {
        TypedQuery<ItemEstoqueJpaEntity> query = entityManager.createQuery(
                "SELECT ie FROM ItemEstoqueJpaEntity ie", ItemEstoqueJpaEntity.class);
        return query.getResultStream()
                .map(ItemEstoqueJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            ItemEstoqueJpaEntity entity = entityManager.find(ItemEstoqueJpaEntity.class, id);
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

    @Override
    public List<ItemEstoque> findByLocalEstoqueId(String localEstoqueId) {
        TypedQuery<ItemEstoqueJpaEntity> query = entityManager.createQuery(
                "SELECT ie FROM ItemEstoqueJpaEntity ie WHERE ie.localEstoqueId = :localEstoqueId", ItemEstoqueJpaEntity.class);
        query.setParameter("localEstoqueId", localEstoqueId);
        return query.getResultStream()
                .map(ItemEstoqueJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemEstoque> findByProdutoIdAndLocalEstoqueId(String produtoId, String localEstoqueId) {
        TypedQuery<ItemEstoqueJpaEntity> query = entityManager.createQuery(
                "SELECT ie FROM ItemEstoqueJpaEntity ie WHERE ie.produtoId = :produtoId AND ie.localEstoqueId = :localEstoqueId", ItemEstoqueJpaEntity.class);
        query.setParameter("produtoId", produtoId);
        query.setParameter("localEstoqueId", localEstoqueId);
        return query.getResultStream()
                .map(ItemEstoqueJpaEntity::toDomain)
                .collect(Collectors.toList());
    }
}