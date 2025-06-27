package com.targosystem.varejo.promocoes.application.query;

import com.targosystem.varejo.promocoes.application.output.KitPromocionalOutput;
import com.targosystem.varejo.promocoes.domain.repository.KitPromocionalRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListarTodosKitsQuery { // Renomeado de ListarKitsPromocionaisQuery

    private static final Logger logger = LoggerFactory.getLogger(ListarTodosKitsQuery.class);

    private final KitPromocionalRepository kitPromocionalRepository;

    public ListarTodosKitsQuery(KitPromocionalRepository kitPromocionalRepository) {
        this.kitPromocionalRepository = Objects.requireNonNull(kitPromocionalRepository, "KitPromocionalRepository cannot be null");
    }

    public List<KitPromocionalOutput> execute() {
        logger.debug("Executing ListarTodosKitsQuery to fetch all promotional kits.");
        try {
            return kitPromocionalRepository.findAll().stream()
                    .map(KitPromocionalOutput::from) // Assumindo que KitPromocionalOutput tem um método from(KitPromocional)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving all promotional kits: {}", e.getMessage(), e);
            // Poderia lançar uma DomainException ou uma exceção mais específica de infraestrutura
            throw new RuntimeException("Falha ao listar todos os kits promocionais.", e);
        }
    }
}