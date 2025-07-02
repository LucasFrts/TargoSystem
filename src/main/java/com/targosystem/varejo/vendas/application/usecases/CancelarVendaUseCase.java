package com.targosystem.varejo.vendas.application.usecases;

import com.targosystem.varejo.vendas.application.input.CancelarVendaInput;
import com.targosystem.varejo.vendas.application.output.VendaOutput;
import com.targosystem.varejo.vendas.domain.model.Venda;
import com.targosystem.varejo.vendas.domain.model.VendaId;
import com.targosystem.varejo.vendas.domain.repository.VendaRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

import com.targosystem.varejo.vendas.infra.integration.EstoqueEventProducer;
import com.targosystem.varejo.vendas.infra.integration.ProdutoParaEstoqueInfo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CancelarVendaUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CancelarVendaUseCase.class);

    private final VendaRepository vendaRepository;
    private final EstoqueEventProducer estoqueEventProducer;
    private final EventPublisher eventPublisher;

    public CancelarVendaUseCase(VendaRepository vendaRepository, EstoqueEventProducer estoqueEventProducer, EventPublisher eventPublisher) {
        this.vendaRepository = Objects.requireNonNull(vendaRepository, "VendaRepository cannot be null.");
        this.estoqueEventProducer = Objects.requireNonNull(estoqueEventProducer, "EstoqueEventProducer cannot be null.");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public VendaOutput execute(CancelarVendaInput input) {
        logger.info("Iniciando CancelarVendaUseCase para venda ID: {}", input.idVenda());

        VendaId vendaId = new VendaId(input.idVenda());
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new DomainException("Venda não encontrada com ID: " + input.idVenda()));

        String statusAnterior = venda.getStatus();
        venda.cancelarVenda(); // Lógica de domínio para cancelar a venda

        Venda vendaSalva = vendaRepository.save(venda);
        logger.info("Venda ID: {} cancelada com sucesso. Status: {}", vendaSalva.getId().value(), vendaSalva.getStatus());

        if (statusAnterior.equals("CONCLUIDA")) {
            List<ProdutoParaEstoqueInfo> produtosParaReposicao = vendaSalva.getItens().stream()
                    .map(item -> new ProdutoParaEstoqueInfo(item.getIdProduto().value(), item.getQuantidade()))
                    .collect(Collectors.toList());

            estoqueEventProducer.sendEstoqueReposicaoEvent(vendaSalva.getId().value(), produtosParaReposicao);
            logger.info("Evento de reposição de estoque enviado para venda ID: {}", vendaSalva.getId().value());
        }

        // eventPublisher.publish(new VendaCanceladaEvent(vendaSalva.getId().value()));

        logger.info("CancelarVendaUseCase concluído para venda ID: {}", vendaSalva.getId().value());
        return VendaOutput.from(vendaSalva);
    }
}