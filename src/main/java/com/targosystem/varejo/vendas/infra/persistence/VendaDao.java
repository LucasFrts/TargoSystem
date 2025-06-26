package com.targosystem.varejo.vendas.infra.persistence;

import com.targosystem.varejo.clientes.domain.repository.ClienteRepository;
import com.targosystem.varejo.vendas.domain.model.Venda;
import com.targosystem.varejo.vendas.domain.model.VendaId;
import com.targosystem.varejo.vendas.domain.repository.VendaRepository;
import com.targosystem.varejo.vendas.infra.persistence.entity.VendaJpaEntity;
import com.targosystem.varejo.clientes.domain.model.ClienteId; // Importe ClienteId
import com.targosystem.varejo.clientes.infra.persistence.ClienteDao; // Importe ClienteDao
import com.targosystem.varejo.clientes.infra.persistence.entity.ClienteJpaEntity; // Importe ClienteJpaEntity
import com.targosystem.varejo.shared.domain.DomainException; // Para lançar exceções se cliente não for encontrado

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VendaDao implements VendaRepository {

    private final EntityManager entityManager;
    private final ClienteRepository clienteDao;

    public VendaDao(EntityManager entityManager, ClienteRepository clienteDao) {
        this.entityManager = entityManager;
        this.clienteDao = clienteDao;
    }

    @Override
    public Venda save(Venda venda) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            ClienteJpaEntity clienteJpaEntity = entityManager.find(
                    ClienteJpaEntity.class, venda.getCliente().getId().value());

            if (clienteJpaEntity == null) {
                throw new DomainException("Cliente com ID " + venda.getCliente().getId().value() + " não encontrado para salvar a venda.");
            }

            VendaJpaEntity entity;
            if (venda.getId() == null || entityManager.find(VendaJpaEntity.class, venda.getId().value()) == null) {
                // Nova venda
                entity = VendaJpaEntity.fromDomain(venda, clienteJpaEntity); // Passar o ClienteJpaEntity
                entityManager.persist(entity);
                entityManager.flush();
            } else {
                // Venda existente
                entity = VendaJpaEntity.fromDomain(venda, clienteJpaEntity); // Passar o ClienteJpaEntity
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
    public Optional<Venda> findById(VendaId id) {
        try {
            TypedQuery<VendaJpaEntity> query = entityManager.createQuery(
                    "SELECT v FROM VendaJpaEntity v LEFT JOIN FETCH v.itens WHERE v.id = :id", VendaJpaEntity.class);
            query.setParameter("id", id.value());
            VendaJpaEntity entity = query.getSingleResult();
            return Optional.of(entity.toDomain());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Venda> findAll() {
        TypedQuery<VendaJpaEntity> query = entityManager.createQuery(
                "SELECT v FROM VendaJpaEntity v LEFT JOIN FETCH v.itens", VendaJpaEntity.class); // Carregar itens também
        return query.getResultList().stream()
                .map(VendaJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

}