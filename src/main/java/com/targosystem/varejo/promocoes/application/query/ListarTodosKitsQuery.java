package com.targosystem.varejo.promocoes.application.query;

import com.targosystem.varejo.promocoes.application.output.KitPromocionalOutput;
import com.targosystem.varejo.promocoes.domain.repository.KitPromocionalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListarTodosKitsQuery {

    private static final Logger logger = LoggerFactory.getLogger(ListarTodosKitsQuery.class);

    private final KitPromocionalRepository kitPromocionalRepository;

    public ListarTodosKitsQuery(KitPromocionalRepository kitPromocionalRepository) {
        this.kitPromocionalRepository = Objects.requireNonNull(kitPromocionalRepository, "KitPromocionalRepository cannot be null");
    }

    public List<KitPromocionalOutput> execute() {
        logger.debug("Executing ListarTodosKitsQuery to fetch all promotional kits.");
        try {
            return kitPromocionalRepository.findAll().stream()
                    .map(KitPromocionalOutput::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving all promotional kits: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao listar todos os kits promocionais.", e);
        }
    }
}