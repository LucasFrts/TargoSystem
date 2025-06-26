package com.targosystem.varejo.fornecedores.application.usecases;

import com.targosystem.varejo.fornecedores.application.input.AvaliarEntregaFornecedorInput;
import com.targosystem.varejo.fornecedores.application.output.EntregaFornecedorOutput;
import com.targosystem.varejo.fornecedores.domain.model.EntregaFornecedor;
import com.targosystem.varejo.fornecedores.domain.repository.EntregaFornecedorRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AvaliarEntregaFornecedorUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AvaliarEntregaFornecedorUseCase.class);

    private final EntregaFornecedorRepository entregaRepository;
    private final EventPublisher eventPublisher;

    public AvaliarEntregaFornecedorUseCase(EntregaFornecedorRepository entregaRepository, EventPublisher eventPublisher) {
        this.entregaRepository = Objects.requireNonNull(entregaRepository, "EntregaFornecedorRepository cannot be null.");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public EntregaFornecedorOutput execute(AvaliarEntregaFornecedorInput input) {
        logger.info("Tentando avaliar entrega ID: {} com nota {}", input.entregaId(), input.nota());

        EntregaFornecedor entrega = entregaRepository.findById(input.entregaId())
                .orElseThrow(() -> new DomainException("Entrega não encontrada com ID: " + input.entregaId()));

        entrega.avaliar(input.nota(), input.comentario());

        EntregaFornecedor entregaSalva = entregaRepository.save(entrega);
        logger.info("Entrega ID {} avaliada com sucesso. Nota: {}", entregaSalva.getId(), entregaSalva.getAvaliacaoNota());

        return EntregaFornecedorOutput.fromDomain(entregaSalva);
    }
}