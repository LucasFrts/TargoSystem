package com.targosystem.varejo.vendas.application.query;

import com.targosystem.varejo.vendas.application.output.VendaOutput;
import com.targosystem.varejo.vendas.domain.model.VendaId;
import com.targosystem.varejo.vendas.domain.repository.VendaRepository;
import com.targosystem.varejo.shared.domain.DomainException;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsultarVendaPorIdQuery {

    private static final Logger logger = LoggerFactory.getLogger(ConsultarVendaPorIdQuery.class);

    private final VendaRepository vendaRepository;

    public ConsultarVendaPorIdQuery(VendaRepository vendaRepository) {
        this.vendaRepository = Objects.requireNonNull(vendaRepository, "VendaRepository cannot be null.");
    }

    public VendaOutput execute(String idVenda) {
        logger.info("Consultando venda por ID: {}", idVenda);
        VendaId vendaId = new VendaId(idVenda);
        return vendaRepository.findById(vendaId)
                .map(VendaOutput::from)
                .orElseThrow(() -> new DomainException("Venda não encontrada com ID: " + idVenda));
    }
}