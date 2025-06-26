package com.targosystem.varejo.fornecedores.infra.persistence;

import com.targosystem.varejo.fornecedores.domain.model.Fornecedor;
import com.targosystem.varejo.fornecedores.domain.model.FornecedorId; // NOVO
import com.targosystem.varejo.fornecedores.domain.repository.FornecedorRepository;
import com.targosystem.varejo.fornecedores.infra.persistence.entity.FornecedorJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FornecedorDao implements FornecedorRepository {

    private final EntityManager entityManager;

    public FornecedorDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Fornecedor save(Fornecedor fornecedor) {
        FornecedorJpaEntity jpaEntity = FornecedorJpaEntity.fromDomain(fornecedor);
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            FornecedorJpaEntity existingEntity = entityManager.find(FornecedorJpaEntity.class, jpaEntity.getId());
            if (existingEntity == null) {
                entityManager.persist(jpaEntity);
            } else {
                existingEntity.setNome(jpaEntity.getNome());
                existingEntity.setCnpj(jpaEntity.getCnpj());
                existingEntity.setEmailContato(jpaEntity.getEmailContato()); // NOVO
                existingEntity.setTelefoneContato(jpaEntity.getTelefoneContato()); // NOVO
                existingEntity.setLogradouro(jpaEntity.getLogradouro());
                existingEntity.setNumero(jpaEntity.getNumero());
                existingEntity.setComplemento(jpaEntity.getComplemento());
                existingEntity.setBairro(jpaEntity.getBairro());
                existingEntity.setCidade(jpaEntity.getCidade());
                existingEntity.setEstado(jpaEntity.getEstado());
                existingEntity.setCep(jpaEntity.getCep());
                existingEntity.setAtivo(jpaEntity.isAtivo());
                existingEntity.setDataCriacao(jpaEntity.getDataCriacao());
                existingEntity.setDataAtualizacao(jpaEntity.getDataAtualizacao());

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
    public Optional<Fornecedor> findById(FornecedorId id) { // Recebe FornecedorId
        try {
            return Optional.ofNullable(entityManager.find(FornecedorJpaEntity.class, id.value())) // Usa .value()
                    .map(FornecedorJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Fornecedor> findByCnpj(String cnpj) {
        try {
            TypedQuery<FornecedorJpaEntity> query = entityManager.createQuery(
                    "SELECT f FROM FornecedorJpaEntity f WHERE f.cnpj = :cnpj", FornecedorJpaEntity.class);
            query.setParameter("cnpj", cnpj);
            return Optional.of(query.getSingleResult()).map(FornecedorJpaEntity::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Fornecedor> findAll() {
        TypedQuery<FornecedorJpaEntity> query = entityManager.createQuery(
                "SELECT f FROM FornecedorJpaEntity f", FornecedorJpaEntity.class);
        return query.getResultList().stream()
                .map(FornecedorJpaEntity::toDomain)
                .collect(Collectors.toList());
    }
}