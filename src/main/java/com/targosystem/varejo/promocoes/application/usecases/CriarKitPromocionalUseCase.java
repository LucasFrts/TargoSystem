package com.targosystem.varejo.promocoes.application.usecases;

import com.targosystem.varejo.promocoes.application.input.CriarKitPromocionalInput;
import com.targosystem.varejo.promocoes.application.output.KitPromocionalOutput;
import com.targosystem.varejo.promocoes.domain.model.KitPromocional;
import com.targosystem.varejo.promocoes.domain.model.ItemKit;
import com.targosystem.varejo.promocoes.domain.repository.KitPromocionalRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CriarKitPromocionalUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CriarKitPromocionalUseCase.class);

    private final KitPromocionalRepository kitPromocionalRepository;
    private final EventPublisher eventPublisher;
    private final EntityManager entityManager;
    // Removido: private final ProdutoService produtoService;

    public CriarKitPromocionalUseCase(KitPromocionalRepository kitPromocionalRepository, EventPublisher eventPublisher, EntityManager entityManager) {
        this.kitPromocionalRepository = Objects.requireNonNull(kitPromocionalRepository, "KitPromocionalRepository cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null");
        this.entityManager = Objects.requireNonNull(entityManager, "EntityManager cannot be null");
        // Removido: this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService cannot be null");
    }

    public KitPromocionalOutput execute(CriarKitPromocionalInput input) {
        logger.info("Attempting to create new promotional kit: {}", input.nome());

        EntityTransaction transaction = entityManager.getTransaction();
        boolean newTransaction = false;

        try {
            if (!transaction.isActive()) {
                transaction.begin();
                newTransaction = true;
            }

            // Validações de input
            if (input.nome() == null || input.nome().isBlank()) {
                throw new DomainException("Nome do kit promocional não pode ser vazio.");
            }
            if (input.precoFixoKit() == null || input.precoFixoKit().compareTo(BigDecimal.ZERO) < 0) {
                throw new DomainException("Preço fixo do kit não pode ser nulo ou negativo.");
            }
            if (input.itens() == null || input.itens().isEmpty()) {
                throw new DomainException("Um kit promocional deve conter pelo menos um item.");
            }

            // Mapear ItemKitInput para ItemKit de domínio
            List<ItemKit> itensDominio = input.itens().stream()
                    .map(itemInput -> new ItemKit(itemInput.produtoId(), itemInput.quantidade()))
                    .collect(Collectors.toList());

            // Usando o construtor do domínio que aceita todos os campos para reconstruir
            KitPromocional novoKit = new KitPromocional(
                    UUID.randomUUID().toString(), // Gerar ID único para o novo kit
                    input.nome(),
                    input.descricao(),
                    input.precoFixoKit(),
                    itensDominio,
                    LocalDateTime.now(), // Data de criação
                    LocalDateTime.now()  // Data de atualização
            );

            KitPromocional kitSalvo = kitPromocionalRepository.save(novoKit);
            logger.info("Promotional kit '{}' (ID: {}) created successfully.", kitSalvo.getNome(), kitSalvo.getId());

            if (newTransaction) {
                transaction.commit();
            }

            // Agora chamamos o 'from' que NAO precisa do ProdutoService
            return KitPromocionalOutput.from(kitSalvo);
        } catch (DomainException e) {
            if (transaction != null && transaction.isActive() && newTransaction) {
                transaction.rollback();
            }
            logger.error("Domain error during kit creation: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive() && newTransaction) {
                transaction.rollback();
            }
            logger.error("Unexpected error during kit creation: {}", e.getMessage(), e);
            throw new DomainException("Erro inesperado ao criar kit promocional: " + e.getMessage(), e);
        }
    }
}