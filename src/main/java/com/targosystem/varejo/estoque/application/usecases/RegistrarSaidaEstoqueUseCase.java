package com.targosystem.varejo.estoque.application.usecases;

import com.targosystem.varejo.estoque.application.input.RegistrarSaidaEstoqueInput;
import com.targosystem.varejo.estoque.application.output.EstoqueOutput;
import com.targosystem.varejo.estoque.domain.model.Estoque;
import com.targosystem.varejo.estoque.domain.repository.EstoqueRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrarSaidaEstoqueUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RegistrarSaidaEstoqueUseCase.class);

    private final EstoqueRepository estoqueRepository;
    private final EventPublisher eventPublisher;

    public RegistrarSaidaEstoqueUseCase(EstoqueRepository estoqueRepository, EventPublisher eventPublisher) {
        this.estoqueRepository = Objects.requireNonNull(estoqueRepository, "EstoqueRepository cannot be null.");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public EstoqueOutput execute(RegistrarSaidaEstoqueInput input) {
        logger.info("Registrando saída de {} itens para produto {}.", input.quantidade(), input.produtoId());

        Estoque estoque = estoqueRepository.findByProdutoId(input.produtoId())
                .orElseThrow(() -> new DomainException("Estoque não encontrado para o produto com ID: " + input.produtoId()));

        try {
            estoque.removerItens(input.quantidade(), input.motivo());
        } catch (DomainException e) {
            logger.warn("Erro ao remover itens do estoque para produto {}: {}", input.produtoId(), e.getMessage());
            throw e;
        }

        Estoque estoqueSalvo = estoqueRepository.save(estoque);
        logger.info("Saída de estoque para produto {} registrada com sucesso. ID do estoque: {}. Nova quantidade total: {}",
                estoqueSalvo.getProdutoId(), estoqueSalvo.getId(), estoqueSalvo.getQuantidadeTotalDisponivel());

        // Opcional: Publicar evento EstoqueAtualizadoEvent ou SaidaEstoqueRegistradaEvent
        // eventPublisher.publish(new EstoqueAtualizadoEvent(estoqueSalvo.getId(), estoqueSalvo.getProdutoId(), estoqueSalvo.getQuantidadeTotalDisponivel()));

        return EstoqueOutput.fromDomain(estoqueSalvo);
    }
}