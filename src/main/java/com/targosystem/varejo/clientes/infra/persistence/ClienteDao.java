package com.targosystem.varejo.clientes.infra.persistence;

import com.targosystem.varejo.clientes.domain.model.Cliente;
import com.targosystem.varejo.clientes.domain.model.ClienteId;
import com.targosystem.varejo.clientes.domain.repository.ClienteRepository;
import com.targosystem.varejo.clientes.infra.persistence.entity.ClienteJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClienteDao implements ClienteRepository {

    private final EntityManager entityManager;

    public ClienteDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Cliente save(Cliente cliente) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            ClienteJpaEntity entity;
            if (cliente.getId() == null || entityManager.find(ClienteJpaEntity.class, cliente.getId().value()) == null) {
                // Nova cliente
                entity = ClienteJpaEntity.fromDomain(cliente);
                entityManager.persist(entity);
                entityManager.flush(); // Garante que o ID gerado esteja disponível
            } else {
                // Cliente existente
                entity = ClienteJpaEntity.fromDomain(cliente);
                entity = entityManager.merge(entity);
            }
            transaction.commit();
            return entity.toDomain();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<Cliente> findById(ClienteId id) {
        try {
            return Optional.ofNullable(entityManager.find(ClienteJpaEntity.class, id.value()))
                    .map(ClienteJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Cliente> findByCpf(String cpf) {
        try {
            TypedQuery<ClienteJpaEntity> query = entityManager.createQuery(
                    "SELECT c FROM ClienteJpaEntity c WHERE c.cpf = :cpf", ClienteJpaEntity.class);
            query.setParameter("cpf", cpf);
            return Optional.of(query.getSingleResult()).map(ClienteJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByCpf(String cpf) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            if (!transaction.isActive()) {
                transaction.begin();
            }
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(c) FROM ClienteJpaEntity c WHERE c.cpf = :cpf", Long.class);
            query.setParameter("cpf", cpf);
            Long count = query.getSingleResult();
            if (transaction.isActive() && !transaction.getRollbackOnly()) {
                // transaction.commit(); // Opcional para transações de leitura
            }
            return count > 0;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public List<Cliente> findAll() {
        TypedQuery<ClienteJpaEntity> query = entityManager.createQuery(
                "SELECT c FROM ClienteJpaEntity c", ClienteJpaEntity.class);
        return query.getResultList().stream()
                .map(ClienteJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(ClienteId id) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            ClienteJpaEntity entity = entityManager.find(ClienteJpaEntity.class, id.value());
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
}