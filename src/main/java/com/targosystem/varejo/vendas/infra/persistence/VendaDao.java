package com.targosystem.varejo.vendas.infra.persistence;

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
    private final ClienteDao clienteDao; // Dependência do ClienteDao para buscar ClienteJpaEntity

    public VendaDao(EntityManager entityManager, ClienteDao clienteDao) {
        this.entityManager = entityManager;
        this.clienteDao = clienteDao; // Injetar ClienteDao
    }

    @Override
    public Venda save(Venda venda) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            // Ao salvar uma venda, precisamos garantir que o ClienteJpaEntity existe
            // Se o Cliente não for novo, podemos buscá-lo. Se for novo, ele já deve ter sido persistido antes.
            // Para simplicidade, vamos buscar o ClienteJpaEntity aqui.
            // Em cenários mais complexos, o Cliente já viria anexado ou seria gerenciado por um serviço transacional.
            ClienteJpaEntity clienteJpaEntity = entityManager.find(
                    ClienteJpaEntity.class, venda.getCliente().getId().value());

            if (clienteJpaEntity == null) {
                // Isso indica um problema de integridade referencial ou que o cliente não foi salvo antes.
                // Dependendo da sua regra de negócio, pode-se tentar salvar o cliente aqui ou lançar exceção.
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
            // Usar LEFT JOIN FETCH para carregar os itens e o cliente na mesma consulta,
            // evitando problemas de N+1 e LazyInitializationException
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