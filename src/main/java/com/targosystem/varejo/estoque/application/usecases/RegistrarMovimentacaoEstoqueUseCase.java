package com.targosystem.varejo.estoque.application.usecases;

import com.targosystem.varejo.estoque.application.input.ItemMovimentacaoInput;
import com.targosystem.varejo.estoque.application.input.RegistrarMovimentacaoEstoqueInput;
import com.targosystem.varejo.estoque.application.output.MovimentacaoEstoqueOutput;
import com.targosystem.varejo.estoque.domain.model.*;
import com.targosystem.varejo.estoque.domain.repository.EstoqueRepository;
import com.targosystem.varejo.estoque.domain.repository.LocalEstoqueRepository;
import com.targosystem.varejo.estoque.domain.repository.MovimentacaoEstoqueRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class RegistrarMovimentacaoEstoqueUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RegistrarMovimentacaoEstoqueUseCase.class);

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final EstoqueRepository estoqueRepository;
    private final LocalEstoqueRepository localEstoqueRepository;
    private final EventPublisher eventPublisher;

    public RegistrarMovimentacaoEstoqueUseCase(
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
            EstoqueRepository estoqueRepository,
            LocalEstoqueRepository localEstoqueRepository,
            EventPublisher eventPublisher) {
        this.movimentacaoEstoqueRepository = Objects.requireNonNull(movimentacaoEstoqueRepository, "MovimentacaoEstoqueRepository cannot be null.");
        this.estoqueRepository = Objects.requireNonNull(estoqueRepository, "EstoqueRepository cannot be null.");
        this.localEstoqueRepository = Objects.requireNonNull(localEstoqueRepository, "LocalEstoqueRepository cannot be null.");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public MovimentacaoEstoqueOutput execute(RegistrarMovimentacaoEstoqueInput input) {
        logger.info("Iniciando registro de movimentação. Tipo: {}, Origem: {}, Destino: {}, Itens: {}",
                input.tipoMovimentacao(), input.localOrigemId(), input.localDestinoId(), input.itens().size());

        LocalEstoque localOrigem = localEstoqueRepository.findById(input.localOrigemId())
                .orElseThrow(() -> new DomainException("Local de origem não encontrado: " + input.localOrigemId()));
        LocalEstoque localDestino = localEstoqueRepository.findById(input.localDestinoId())
                .orElseThrow(() -> new DomainException("Local de destino não encontrado: " + input.localDestinoId()));

        List<ItemMovimentacaoEstoque> domainItens = input.itens().stream()
                .map(itemInput -> new ItemMovimentacaoEstoque(itemInput.produtoId(), itemInput.quantidade()))
                .collect(Collectors.toList());

        MovimentacaoEstoque novaMovimentacao = new MovimentacaoEstoque(
                input.tipoMovimentacao(),
                localOrigem.getId(),
                localDestino.getId(),
                input.motivo(),
                domainItens
        );

        processarMovimentacao(novaMovimentacao, localOrigem, localDestino, input.itens());

        MovimentacaoEstoque movimentacaoSalva = movimentacaoEstoqueRepository.save(novaMovimentacao);
        logger.info("Movimentação de estoque registrada com sucesso. ID: {}", movimentacaoSalva.getId());

        return MovimentacaoEstoqueOutput.fromDomain(movimentacaoSalva);
    }

    private void processarMovimentacao(MovimentacaoEstoque movimentacao, LocalEstoque localOrigem, LocalEstoque localDestino,
                                       List<ItemMovimentacaoInput> inputItems) {
        switch (movimentacao.getTipo()) {
            case ENTRADA:
                // Validações do tipo ENTRADA
                if (localOrigem.getTipo() != TipoLocal.FORNECEDOR) {
                    throw new DomainException("Para ENTRADA, o local de origem deve ser um FORNECEDOR.");
                }
                if (localDestino.getTipo() != TipoLocal.INTERNO) {
                    throw new DomainException("Para ENTRADA, o local de destino deve ser INTERNO.");
                }
                for (ItemMovimentacaoInput itemInput : inputItems) {
                    if (itemInput.dataFabricacaoLote() == null || itemInput.dataValidadeLote() == null) {
                        throw new DomainException("Para movimentação de ENTRADA, data de fabricação e validade do lote são obrigatórias.");
                    }
                    Lote loteEntrada = new Lote(itemInput.numeroLote(), itemInput.dataFabricacaoLote(), itemInput.dataValidadeLote());
                    processarEntradaItem(itemInput.produtoId(), itemInput.quantidade(), localDestino,
                            loteEntrada, itemInput.corredor(), itemInput.prateleira(), itemInput.nivel());
                }
                break;
            case SAIDA:
                // Validações do tipo SAIDA
                if (localOrigem.getTipo() != TipoLocal.INTERNO) {
                    throw new DomainException("Para SAIDA, o local de origem deve ser INTERNO.");
                }
                if (localDestino.getTipo() != TipoLocal.FORNECEDOR && localDestino.getTipo() != TipoLocal.CLIENTE) {
                    throw new DomainException("Para SAIDA, o local de destino deve ser um FORNECEDOR ou CLIENTE.");
                }
                for (ItemMovimentacaoInput itemInput : inputItems) {
                    processarSaidaItem(itemInput.produtoId(), itemInput.quantidade(), localOrigem);
                }
                break;
            case TRANSFERENCIA:
                // Validações do tipo TRANSFERENCIA
                if (localOrigem.getTipo() != TipoLocal.INTERNO || localDestino.getTipo() != TipoLocal.INTERNO) {
                    throw new DomainException("Para TRANSFERENCIA, ambos os locais (origem e destino) devem ser INTERNOS.");
                }
                if (localOrigem.equals(localDestino)) {
                    throw new DomainException("Não é possível transferir para o mesmo local de estoque.");
                }
                for (ItemMovimentacaoInput itemInput : inputItems) {
                    // 1. Processar Saída da origem
                    Optional<Estoque> estoqueOrigemOpt = estoqueRepository.findByProdutoIdAndLocalEstoqueId(itemInput.produtoId(), localOrigem.getId());
                    if (estoqueOrigemOpt.isEmpty()) {
                        throw new DomainException("Estoque do produto " + itemInput.produtoId() + " não encontrado no local de origem " + localOrigem.getNome() + " para transferência.");
                    }
                    Estoque estoqueOrigem = estoqueOrigemOpt.get();

                    // CORREÇÃO AQUI: Acessar o Lote através dos itensEstoque
                    Optional<Lote> loteSaidaOpt = estoqueOrigem.getItensEstoque().stream() // MUDANÇA AQUI: getItensEstoque()
                            .map(ItemEstoque::getLote) // Mapear para o Lote de cada ItemEstoque
                            .findFirst();

                    logger.info("veio a seguinte quantidade {}", estoqueOrigem.getItensEstoque().size());

                    if (loteSaidaOpt.isEmpty()) {
                        // Se não encontrar o lote específico (se fornecido) ou nenhum lote para o produto.
                        // Poderíamos ter uma lógica mais sofisticada aqui para pegar o "melhor" lote
                        // se o numeroLote do input for nulo, ou buscar por outros critérios.
                        // Por enquanto, é um erro se não encontrar nenhum lote.
                        throw new DomainException("Lote do produto " + itemInput.produtoId() + " não encontrado no estoque de origem para transferência. Verifique o número do lote ou se o estoque possui o item.");
                    }
                    Lote loteSaida = loteSaidaOpt.get();

                    // Se a saída for por lote, a `processarSaidaItem` precisaria de uma assinatura diferente
                    // ou lógica interna para remover do lote correto. Por ora, mantemos a saída total.
                    processarSaidaItem(itemInput.produtoId(), itemInput.quantidade(), localOrigem);

                    // 2. Processar Entrada no destino - USANDO AS DATAS DO LOTE RETIRADO DA ORIGEM
                    Lote loteEntradaDestino = new Lote(
                            loteSaida.getNumeroLote(),
                            loteSaida.getDataFabricacao(),
                            loteSaida.getDataValidade()
                    );

                    processarEntradaItem(itemInput.produtoId(), itemInput.quantidade(), localDestino,
                            loteEntradaDestino, itemInput.corredor(), itemInput.prateleira(), itemInput.nivel());
                }
                break;
            default:
                throw new IllegalArgumentException("Tipo de movimentação desconhecido: " + movimentacao.getTipo());
        }
    }

    private void processarEntradaItem(String produtoId, int quantidade, LocalEstoque localDestino,
                                      Lote lote, String corredor, String prateleira, String nivel) {
        Estoque estoqueDestino = estoqueRepository.findByProdutoIdAndLocalEstoqueId(produtoId, localDestino.getId())
                .orElseGet(() -> {
                    logger.info("Estoque para produto {} no local {} não encontrado. Criando novo.", produtoId, localDestino.getNome());
                    return new Estoque(produtoId, localDestino);
                });

        estoqueDestino.adicionarQuantidadeComLote(quantidade, lote);
        // Adicionar lógica para localização, se o Estoque tiver isso
        // O método adicionarQuantidadeComLote deve ser capaz de lidar com a localização,
        // ou precisamos de um método separado ou modificação em adicionarQuantidadeComLote.
        // Pelo seu Estoque.java, a LocalizacaoArmazenamento é um parâmetro.
        // Vamos usar o método adicionarItens que aceita localização.

        // CORREÇÃO: Usar o método adicionarItens que aceita LocalizacaoArmazenamento
        estoqueDestino.adicionarItens(quantidade, lote, new LocalizacaoArmazenamento(corredor, prateleira, nivel), "Movimentação de Entrada/Transferência");

        estoqueRepository.save(estoqueDestino);
        logger.info("Adicionada entrada de {} unidades do produto {} no local {}. Lote: {}, Corredor: {}, Prateleira: {}, Nível: {}. Saldo: {}",
                quantidade, produtoId, localDestino.getNome(), lote.getNumeroLote(), corredor, prateleira, nivel, estoqueDestino.getQuantidadeTotalDisponivel());
    }

    private void processarSaidaItem(String produtoId, int quantidade, LocalEstoque localOrigem) {
        Estoque estoqueOrigem = estoqueRepository.findByProdutoIdAndLocalEstoqueId(produtoId, localOrigem.getId())
                .orElseThrow(() -> new DomainException("Estoque do produto " + produtoId + " não encontrado no local " + localOrigem.getNome()));

        estoqueOrigem.removerQuantidadeTotal(quantidade); // Assume remoção da quantidade total, não de um lote específico
        estoqueRepository.save(estoqueOrigem);
        logger.info("Registrada saída de {} unidades do produto {} no local {}. Saldo: {}", quantidade, produtoId, localOrigem.getNome(), estoqueOrigem.getQuantidadeTotalDisponivel());
    }
}