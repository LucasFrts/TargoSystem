package com.targosystem.varejo.estoque.application.usecases;

import com.targosystem.varejo.estoque.application.input.LoteInput;
import com.targosystem.varejo.estoque.application.input.LocalizacaoArmazenamentoInput;
import com.targosystem.varejo.estoque.application.input.RegistrarEntradaEstoqueInput;
import com.targosystem.varejo.estoque.application.output.EstoqueOutput;
import com.targosystem.varejo.estoque.domain.model.Estoque;
import com.targosystem.varejo.estoque.domain.model.Lote;
import com.targosystem.varejo.estoque.domain.model.LocalizacaoArmazenamento;
import com.targosystem.varejo.estoque.domain.repository.EstoqueRepository;
import com.targosystem.varejo.shared.infra.EventPublisher;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrarEntradaEstoqueUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RegistrarEntradaEstoqueUseCase.class);

    private final EstoqueRepository estoqueRepository;
    private final EventPublisher eventPublisher;

    public RegistrarEntradaEstoqueUseCase(EstoqueRepository estoqueRepository, EventPublisher eventPublisher) {
        this.estoqueRepository = Objects.requireNonNull(estoqueRepository, "EstoqueRepository cannot be null.");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public EstoqueOutput execute(RegistrarEntradaEstoqueInput input) {
        logger.info("Registrando entrada de {} itens para produto {} com lote {} na localização {}.",
                input.quantidade(), input.produtoId(), input.lote().numeroLote(), input.localizacao().corredor());

        Estoque estoque = estoqueRepository.findByProdutoId(input.produtoId())
                .orElseGet(() -> {
                    logger.info("Estoque para produto {} não encontrado. Criando novo estoque.", input.produtoId());
                    return new Estoque(input.produtoId());
                });

        Lote lote = new Lote(input.lote().numeroLote(), input.lote().dataFabricacao(), input.lote().dataValidade());
        LocalizacaoArmazenamento localizacao = new LocalizacaoArmazenamento(input.localizacao().corredor(), input.localizacao().prateleira(), input.localizacao().nivel());

        estoque.adicionarItensComLoteELocalizacao(input.quantidade(), lote, localizacao, input.motivo());

        Estoque estoqueSalvo = estoqueRepository.save(estoque);
        logger.info("Entrada de estoque para produto {} registrada com sucesso. ID do estoque: {}. Nova quantidade total: {}",
                estoqueSalvo.getProdutoId(), estoqueSalvo.getId(), estoqueSalvo.getQuantidadeTotalDisponivel());

        // Opcional: Publicar evento EstoqueAtualizadoEvent ou EntradaEstoqueRegistradaEvent
        // eventPublisher.publish(new EstoqueAtualizadoEvent(estoqueSalvo.getId(), estoqueSalvo.getProdutoId(), estoqueSalvo.getQuantidadeTotalDisponivel()));

        return EstoqueOutput.fromDomain(estoqueSalvo);
    }
}