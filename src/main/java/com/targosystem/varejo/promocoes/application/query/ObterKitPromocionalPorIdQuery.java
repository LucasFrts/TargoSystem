package com.targosystem.varejo.promocoes.application.query;

import com.targosystem.varejo.promocoes.application.output.KitPromocionalOutput;
import com.targosystem.varejo.promocoes.domain.repository.KitPromocionalRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ObterKitPromocionalPorIdQuery {

    private static final Logger logger = LoggerFactory.getLogger(ObterKitPromocionalPorIdQuery.class);

    private final KitPromocionalRepository kitPromocionalRepository;

    public ObterKitPromocionalPorIdQuery(KitPromocionalRepository kitPromocionalRepository) {
        this.kitPromocionalRepository = Objects.requireNonNull(kitPromocionalRepository, "KitPromocionalRepository cannot be null");
    }

    public KitPromocionalOutput execute(String id) {
        logger.debug("Executing ObterKitPromocionalPorIdQuery for ID: {}", id);
        return kitPromocionalRepository.findById(id)
                .map(KitPromocionalOutput::from)
                .orElseThrow(() -> {
                    logger.warn("Kit Promocional with ID {} not found.", id);
                    return new DomainException("Kit Promocional não encontrado com ID: " + id);
                });
    }
}