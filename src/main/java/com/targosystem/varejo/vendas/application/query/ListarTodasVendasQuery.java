package com.targosystem.varejo.vendas.application.query;

import com.targosystem.varejo.vendas.application.output.VendaOutput;
import com.targosystem.varejo.vendas.domain.repository.VendaRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListarTodasVendasQuery {

    private static final Logger logger = LoggerFactory.getLogger(ListarTodasVendasQuery.class);

    private final VendaRepository vendaRepository;

    public ListarTodasVendasQuery(VendaRepository vendaRepository) {
        this.vendaRepository = Objects.requireNonNull(vendaRepository, "VendaRepository cannot be null.");
    }

    public List<VendaOutput> execute() {
        logger.info("Listando todas as vendas.");
        return vendaRepository.findAll().stream()
                .map(VendaOutput::from)
                .collect(Collectors.toList());
    }
}