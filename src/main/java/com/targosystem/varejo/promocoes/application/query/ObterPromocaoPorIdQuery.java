package com.targosystem.varejo.promocoes.application.query;

import com.targosystem.varejo.promocoes.application.output.PromocaoOutput;
import com.targosystem.varejo.promocoes.domain.repository.PromocaoRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ObterPromocaoPorIdQuery {

    private static final Logger logger = LoggerFactory.getLogger(ObterPromocaoPorIdQuery.class);

    private final PromocaoRepository promocaoRepository;

    public ObterPromocaoPorIdQuery(PromocaoRepository promocaoRepository) {
        this.promocaoRepository = Objects.requireNonNull(promocaoRepository, "PromocaoRepository cannot be null");
    }

    public PromocaoOutput execute(String id) {
        logger.debug("Executing ObterPromocaoPorIdQuery for ID: {}", id);
        return promocaoRepository.findById(id)
                .map(PromocaoOutput::from)
                .orElseThrow(() -> {
                    logger.warn("Promotion with ID {} not found.", id);
                    return new DomainException("Promoção não encontrada com ID: " + id);
                });
    }
}