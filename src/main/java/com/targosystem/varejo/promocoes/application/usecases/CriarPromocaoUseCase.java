package com.targosystem.varejo.promocoes.application.usecases;

import com.targosystem.varejo.promocoes.application.input.CriarPromocaoInput;
import com.targosystem.varejo.promocoes.application.output.PromocaoOutput;
import com.targosystem.varejo.promocoes.domain.model.Promocao;
import com.targosystem.varejo.promocoes.domain.repository.PromocaoRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CriarPromocaoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CriarPromocaoUseCase.class);

    private final PromocaoRepository promocaoRepository;
    private final EventPublisher eventPublisher;
    private final EntityManager entityManager;

    public CriarPromocaoUseCase(PromocaoRepository promocaoRepository, EventPublisher eventPublisher, EntityManager entityManager) {
        this.promocaoRepository = Objects.requireNonNull(promocaoRepository, "PromocaoRepository cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null");
        this.entityManager = Objects.requireNonNull(entityManager, "EntityManager cannot be null");
    }

    public PromocaoOutput execute(CriarPromocaoInput input) {
        logger.info("Attempting to create new promotion: {}", input.nome());

        EntityTransaction transaction = entityManager.getTransaction();
        boolean newTransaction = false;

        try {
            if (!transaction.isActive()) {
                transaction.begin();
                newTransaction = true;
            }

            // Validações de input (além das validações de domínio no construtor)
            if (input.nome() == null || input.nome().isBlank()) {
                throw new DomainException("Nome da promoção não pode ser vazio.");
            }
            if (input.valorDesconto() == null || input.valorDesconto().compareTo(BigDecimal.ZERO) < 0) {
                throw new DomainException("Valor de desconto não pode ser nulo ou negativo.");
            }
            if (input.dataInicio() == null || input.dataFim() == null) {
                throw new DomainException("Datas de início e fim da promoção são obrigatórias.");
            }
            if (input.dataFim().isBefore(input.dataInicio())) {
                throw new DomainException("Data de fim da promoção deve ser posterior ou igual à data de início.");
            }
            if (input.tipoDesconto() == null) { // Verificado o próprio enum, não o name()
                throw new DomainException("Tipo de desconto é obrigatório.");
            }
            // Não precisa mais de try-catch para valueOf se o input já é TipoDesconto

            // NOVO: Validação para lista de produtos (opcionalmente)
            if (input.produtoIds() != null && input.produtoIds().isEmpty()) {
                // Se a lista foi fornecida mas está vazia, pode ser um erro ou intenção.
                // Dependendo da regra de negócio, você pode permitir ou lançar uma exceção.
                // Por enquanto, vamos considerar ok, mas você pode adicionar uma regra.
                logger.warn("Promotion '{}' created with empty product list.", input.nome());
            }

            Promocao novaPromocao = new Promocao(
                    input.nome(),
                    input.tipoDesconto(),
                    input.valorDesconto(),
                    input.dataInicio(),
                    input.dataFim(),
                    input.produtoIds()
            );

            Promocao promocaoSalva = promocaoRepository.save(novaPromocao);
            logger.info("Promotion '{}' (ID: {}) created successfully.", promocaoSalva.getNome(), promocaoSalva.getId());

            if (newTransaction) {
                transaction.commit();
            }

            return PromocaoOutput.from(promocaoSalva);

        } catch (DomainException e) {
            if (transaction != null && transaction.isActive() && newTransaction) {
                transaction.rollback();
            }
            logger.error("Domain error during promotion creation: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive() && newTransaction) {
                transaction.rollback();
            }
            logger.error("Unexpected error during promotion creation: {}", e.getMessage(), e);
            throw new DomainException("Erro inesperado ao criar promoção: " + e.getMessage(), e);
        }
    }
}