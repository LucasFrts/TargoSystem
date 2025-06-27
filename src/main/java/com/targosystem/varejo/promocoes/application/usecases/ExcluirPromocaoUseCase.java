package com.targosystem.varejo.promocoes.application.usecases;

import com.targosystem.varejo.promocoes.domain.model.Promocao;
import com.targosystem.varejo.promocoes.domain.repository.PromocaoRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Objects;
import java.util.Optional;

public class ExcluirPromocaoUseCase {

    private final PromocaoRepository promocaoRepository;
    private final EntityManager entityManager;

    public ExcluirPromocaoUseCase(PromocaoRepository promocaoRepository, EntityManager entityManager) {
        this.promocaoRepository = Objects.requireNonNull(promocaoRepository, "PromocaoRepository cannot be null.");
        this.entityManager = Objects.requireNonNull(entityManager, "EntityManager cannot be null.");
    }

    public void execute(String idPromocao) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean newTransaction = false;

        try {
            if (!transaction.isActive()) {
                transaction.begin();
                newTransaction = true;
            }

            Optional<Promocao> promocaoOptional = promocaoRepository.findById(idPromocao);
            if (promocaoOptional.isEmpty()) {
                throw new DomainException("Promoção com ID " + idPromocao + " não encontrada.");
            }

            promocaoRepository.delete(promocaoOptional.get());

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
            throw new DomainException("Erro inesperado ao excluir promoção: " + e.getMessage(), e);
        }
    }
}