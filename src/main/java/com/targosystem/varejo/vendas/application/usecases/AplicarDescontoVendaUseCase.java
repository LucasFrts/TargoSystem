package com.targosystem.varejo.vendas.application.usecases;

import com.targosystem.varejo.vendas.application.input.AplicarDescontoVendaInput;
import com.targosystem.varejo.vendas.application.output.VendaOutput;
import com.targosystem.varejo.vendas.domain.model.Venda;
import com.targosystem.varejo.vendas.domain.model.VendaId;
import com.targosystem.varejo.vendas.domain.repository.VendaRepository;
import com.targosystem.varejo.vendas.domain.service.ValidadorDesconto;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AplicarDescontoVendaUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AplicarDescontoVendaUseCase.class);

    private final VendaRepository vendaRepository;
    private final ValidadorDesconto validadorDesconto;
    private final EventPublisher eventPublisher;

    public AplicarDescontoVendaUseCase(VendaRepository vendaRepository, ValidadorDesconto validadorDesconto, EventPublisher eventPublisher) {
        this.vendaRepository = Objects.requireNonNull(vendaRepository, "VendaRepository cannot be null.");
        this.validadorDesconto = Objects.requireNonNull(validadorDesconto, "ValidadorDesconto cannot be null.");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public VendaOutput execute(AplicarDescontoVendaInput input) {
        logger.info("Tentando aplicar desconto de {} à venda ID: {}", input.valorDesconto(), input.idVenda());

        VendaId vendaId = new VendaId(input.idVenda());
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new DomainException("Venda não encontrada com ID: " + input.idVenda()));
        
        validadorDesconto.validar(venda, input.valorDesconto());

        venda.aplicarDesconto(input.valorDesconto());

        Venda vendaSalva = vendaRepository.save(venda);
        logger.info("Desconto aplicado com sucesso à venda ID: {}. Novo valor final: {}", vendaSalva.getId().value(), vendaSalva.getValorFinal());

        return VendaOutput.from(vendaSalva);
    }
}