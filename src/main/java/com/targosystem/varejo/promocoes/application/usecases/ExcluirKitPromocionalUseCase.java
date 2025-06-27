package com.targosystem.varejo.promocoes.application.usecases;

import com.targosystem.varejo.promocoes.domain.model.KitPromocional;
import com.targosystem.varejo.promocoes.domain.repository.KitPromocionalRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Objects;
import java.util.Optional;

public class ExcluirKitPromocionalUseCase {

    private final KitPromocionalRepository kitPromocionalRepository;
    private final EntityManager entityManager;

    public ExcluirKitPromocionalUseCase(KitPromocionalRepository kitPromocionalRepository, EntityManager entityManager) {
        this.kitPromocionalRepository = Objects.requireNonNull(kitPromocionalRepository, "KitPromocionalRepository cannot be null.");
        this.entityManager = Objects.requireNonNull(entityManager, "EntityManager cannot be null.");
    }

    public void execute(String idKit) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean newTransaction = false;

        try {
            if (!transaction.isActive()) {
                transaction.begin();
                newTransaction = true;
            }

            Optional<KitPromocional> kitOptional = kitPromocionalRepository.findById(idKit);
            if (kitOptional.isEmpty()) {
                throw new DomainException("Kit Promocional com ID " + idKit + " não encontrado.");
            }

            kitPromocionalRepository.delete(kitOptional.get());

            if (newTransaction) {
                transaction.commit();
            }
        } catch (DomainException e) {
            if (transaction != null && transaction.isActive() && newTransaction) {
                transaction.rollback();
            }
            throw e;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive() && newTransaction) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DomainException("Erro inesperado ao excluir Kit Promocional: " + e.getMessage(), e);
        }
    }
}