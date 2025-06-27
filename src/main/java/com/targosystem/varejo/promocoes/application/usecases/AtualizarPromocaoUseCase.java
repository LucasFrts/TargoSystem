package com.targosystem.varejo.promocoes.application.usecases;

import com.targosystem.varejo.promocoes.application.input.AtualizarPromocaoInput;
import com.targosystem.varejo.promocoes.application.output.PromocaoOutput;
import com.targosystem.varejo.promocoes.domain.model.Promocao;
import com.targosystem.varejo.promocoes.domain.model.TipoDesconto;
import com.targosystem.varejo.promocoes.domain.repository.PromocaoRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtualizarPromocaoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AtualizarPromocaoUseCase.class);

    private final PromocaoRepository promocaoRepository;
    private final EventPublisher eventPublisher;
    private final EntityManager entityManager;

    public AtualizarPromocaoUseCase(PromocaoRepository promocaoRepository, EventPublisher eventPublisher, EntityManager entityManager) {
        this.promocaoRepository = Objects.requireNonNull(promocaoRepository, "PromocaoRepository cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null");
        this.entityManager = Objects.requireNonNull(entityManager, "EntityManager cannot be null");
    }

    public PromocaoOutput execute(AtualizarPromocaoInput input) {
        logger.info("Attempting to update promotion with ID: {}", input.promocaoId());

        EntityTransaction transaction = entityManager.getTransaction();
        boolean newTransaction = false;

        try {
            if (!transaction.isActive()) {
                transaction.begin();
                newTransaction = true;
            }

            Promocao promocao = promocaoRepository.findById(input.promocaoId())
                    .orElseThrow(() -> new DomainException("Promoção não encontrada com ID: " + input.promocaoId()));

            input.nome().ifPresent(nome -> {
                if (nome.isBlank()) throw new DomainException("Nome da promoção não pode ser vazio.");
                promocao.setNome(nome);
            });
            input.tipoDesconto().ifPresent(promocao::setTipoDesconto);
            input.valorDesconto().ifPresent(valor -> {
                if (valor.compareTo(BigDecimal.ZERO) < 0) throw new DomainException("Valor de desconto não pode ser negativo.");
                promocao.setValorDesconto(valor);
            });
            input.dataInicio().ifPresent(dataInicio -> {
                if (dataInicio.isAfter(promocao.getDataFim())) throw new DomainException("Data de início não pode ser posterior à data de fim.");
                promocao.setDataInicio(dataInicio);
            });
            input.dataFim().ifPresent(dataFim -> {
                if (dataFim.isBefore(promocao.getDataInicio())) throw new DomainException("Data de fim não pode ser anterior à data de início.");
                promocao.setDataFim(dataFim);
            });
            input.ativa().ifPresent(promocao::setAtiva);
            // NOVO: Atualiza a lista de produtoIds se presente no input
            input.produtoIds().ifPresent(promocao::setProdutoIds);


            Promocao promocaoAtualizada = promocaoRepository.save(promocao);
            logger.info("Promotion '{}' (ID: {}) updated successfully.", promocaoAtualizada.getNome(), promocaoAtualizada.getId());

            if (newTransaction) {
                transaction.commit();
            }

            return PromocaoOutput.from(promocaoAtualizada);

        } catch (DomainException e) {
            if (transaction != null && transaction.isActive() && newTransaction) {
                transaction.rollback();
            }
            logger.error("Domain error during promotion update: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive() && newTransaction) {
                transaction.rollback();
            }
            logger.error("Unexpected error during promotion update: {}", e.getMessage(), e);
            throw new DomainException("Erro inesperado ao atualizar promoção: " + e.getMessage(), e);
        }
    }
}