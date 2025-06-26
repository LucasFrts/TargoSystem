package com.targosystem.varejo.estoque.application.usecases;

import com.targosystem.varejo.estoque.application.input.LoteInput;
import com.targosystem.varejo.estoque.application.input.LocalizacaoArmazenamentoInput;
import com.targosystem.varejo.estoque.application.input.RegistrarMovimentacaoEstoqueInput;
import com.targosystem.varejo.estoque.application.output.EstoqueOutput;
import com.targosystem.varejo.estoque.domain.model.Estoque;
import com.targosystem.varejo.estoque.domain.model.Lote; // Importar Lote
import com.targosystem.varejo.estoque.domain.model.LocalizacaoArmazenamento; // Importar LocalizacaoArmazenamento
import com.targosystem.varejo.estoque.domain.model.TipoMovimentacao;
import com.targosystem.varejo.estoque.domain.repository.EstoqueRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrarMovimentacaoEstoqueUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RegistrarMovimentacaoEstoqueUseCase.class);

    private final EstoqueRepository estoqueRepository;
    private final EventPublisher eventPublisher;

    public RegistrarMovimentacaoEstoqueUseCase(EstoqueRepository estoqueRepository, EventPublisher eventPublisher) {
        this.estoqueRepository = Objects.requireNonNull(estoqueRepository, "EstoqueRepository cannot be null.");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public EstoqueOutput execute(RegistrarMovimentacaoEstoqueInput input) {
        logger.info("Registrando movimentação de estoque para produto {}. Tipo: {}, Quantidade: {}",
                input.produtoId(), input.tipoMovimentacao(), input.quantidade());

        Estoque estoque = estoqueRepository.findByProdutoId(input.produtoId())
                .orElseGet(() -> {
                    logger.info("Estoque para produto {} não encontrado. Criando novo estoque.", input.produtoId());
                    return new Estoque(input.produtoId());
                });

        if (input.tipoMovimentacao() == TipoMovimentacao.ENTRADA) {
            // Validações para entrada com lote e localização
            if (input.lote() == null) {
                throw new IllegalArgumentException("Lote é obrigatório para movimentações de ENTRADA.");
            }
            if (input.localizacao() == null) {
                throw new IllegalArgumentException("Localização de armazenamento é obrigatória para movimentações de ENTRADA.");
            }

            // Converte os DTOs de input para os objetos de valor do domínio
            Lote lote = new Lote(input.lote().numeroLote(), input.lote().dataFabricacao(), input.lote().dataValidade());
            LocalizacaoArmazenamento localizacao = new LocalizacaoArmazenamento(input.localizacao().corredor(), input.localizacao().prateleira(), input.localizacao().nivel());

            // Chama o método correto na entidade Estoque
            estoque.adicionarItensComLoteELocalizacao(input.quantidade(), lote, localizacao, input.motivo());
            logger.info("Adicionados {} itens ao estoque do produto {}. Nova quantidade: {}",
                    input.quantidade(), input.produtoId(), estoque.getQuantidadeTotalDisponivel()); // CORRIGIDO AQUI!
        } else if (input.tipoMovimentacao() == TipoMovimentacao.SAIDA) {
            try {
                // Para saídas, continuamos usando removerItens
                estoque.removerItens(input.quantidade(), input.motivo());
                logger.info("Removidos {} itens do estoque do produto {}. Nova quantidade: {}",
                        input.quantidade(), input.produtoId(), estoque.getQuantidadeTotalDisponivel()); // CORRIGIDO AQUI!
            } catch (DomainException e) {
                logger.warn("Erro ao remover itens do estoque para produto {}: {}", input.produtoId(), e.getMessage());
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Tipo de movimentação desconhecido: " + input.tipoMovimentacao());
        }

        Estoque estoqueSalvo = estoqueRepository.save(estoque);
        logger.info("Movimentação de estoque para produto {} registrada com sucesso. ID do estoque: {}", estoqueSalvo.getProdutoId(), estoqueSalvo.getId());

        // Opcional: Publicar eventos (Ex: EstoqueAtualizadoEvent)
        // eventPublisher.publish(new EstoqueAtualizadoEvent(estoqueSalvo.getId(), estoqueSalvo.getProdutoId(), estoqueSalvo.getQuantidadeTotalDisponivel())); // CORRIGIDO AQUI!

        return EstoqueOutput.fromDomain(estoqueSalvo);
    }
}