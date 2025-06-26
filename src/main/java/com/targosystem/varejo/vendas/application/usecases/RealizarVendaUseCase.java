package com.targosystem.varejo.vendas.application.usecases;

import com.targosystem.varejo.vendas.application.input.RealizarVendaInput;
import com.targosystem.varejo.vendas.application.output.VendaOutput;
import com.targosystem.varejo.vendas.domain.model.ItemVenda;
import com.targosystem.varejo.vendas.domain.model.Venda;
import com.targosystem.varejo.vendas.domain.model.VendaId;
import com.targosystem.varejo.vendas.domain.repository.VendaRepository;
import com.targosystem.varejo.vendas.domain.service.GestorPoliticaPreco;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

// Integration with Estoque
import com.targosystem.varejo.vendas.infra.integration.EstoqueEventProducer;
// CORRECTED IMPORT: Use ProdutoParaEstoqueInfo instead of ProdutoVendidoInfo
import com.targosystem.varejo.vendas.infra.integration.ProdutoParaEstoqueInfo;

// Imports from Bounded Contexts
import com.targosystem.varejo.clientes.domain.model.ClienteId;
import com.targosystem.varejo.clientes.domain.repository.ClienteRepository;
import com.targosystem.varejo.produtos.domain.model.ProdutoId;
import com.targosystem.varejo.produtos.domain.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RealizarVendaUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RealizarVendaUseCase.class);

    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final GestorPoliticaPreco gestorPoliticaPreco;
    private final EstoqueEventProducer estoqueEventProducer;
    private final EventPublisher eventPublisher;

    public RealizarVendaUseCase(VendaRepository vendaRepository,
                                ClienteRepository clienteRepository,
                                ProdutoRepository produtoRepository,
                                GestorPoliticaPreco gestorPoliticaPreco,
                                EstoqueEventProducer estoqueEventProducer,
                                EventPublisher eventPublisher) {
        this.vendaRepository = Objects.requireNonNull(vendaRepository, "VendaRepository cannot be null.");
        this.clienteRepository = Objects.requireNonNull(clienteRepository, "ClienteRepository cannot be null.");
        this.produtoRepository = Objects.requireNonNull(produtoRepository, "ProdutoRepository cannot be null.");
        this.gestorPoliticaPreco = Objects.requireNonNull(gestorPoliticaPreco, "GestorPoliticaPreco cannot be null.");
        this.estoqueEventProducer = Objects.requireNonNull(estoqueEventProducer, "EstoqueEventProducer cannot be null.");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public VendaOutput execute(RealizarVendaInput input) {
        logger.info("Iniciando RealizarVendaUseCase para cliente ID: {}", input.idCliente());

        com.targosystem.varejo.clientes.domain.model.Cliente clienteDoBCClientes =
                clienteRepository.findById(ClienteId.from(input.idCliente()))
                        .orElseThrow(() -> new DomainException("Cliente com ID " + input.idCliente() + " não encontrado."));

        List<ItemVenda> itensVenda = input.itens().stream()
                .map(itemInput -> {
                    com.targosystem.varejo.produtos.domain.model.Produto produtoDoBCProdutos =
                            produtoRepository.findById(ProdutoId.from(itemInput.idProduto()))
                                    .orElseThrow(() -> new DomainException("Produto com ID " + itemInput.idProduto() + " não encontrado."));

                    // Access the value of ProdutoId using .value()
                    BigDecimal precoUnitario = gestorPoliticaPreco.getPrecoUnitario(produtoDoBCProdutos.getId().value());

                    return new ItemVenda(
                            new com.targosystem.varejo.vendas.domain.model.ItemVendaId(UUID.randomUUID().toString()),
                            produtoDoBCProdutos.getId(),
                            produtoDoBCProdutos.getNome(),
                            itemInput.quantidade(),
                            precoUnitario,
                            precoUnitario.multiply(new BigDecimal(itemInput.quantidade()))
                    );
                })
                .collect(Collectors.toList());

        Venda venda = new Venda(
                clienteDoBCClientes,
                itensVenda,
                input.valorDesconto() != null ? input.valorDesconto() : BigDecimal.ZERO
        );

        venda.concluirVenda();

        Venda vendaSalva = vendaRepository.save(venda);
        logger.info("Venda ID: {} salva com sucesso. Status: {}", vendaSalva.getId().value(), vendaSalva.getStatus());

        // CORRECTED USAGE: Now using ProdutoParaEstoqueInfo
        List<ProdutoParaEstoqueInfo> produtosParaBaixa = itensVenda.stream()
                .map(item -> new ProdutoParaEstoqueInfo(item.getIdProduto().value(), item.getQuantidade()))
                .collect(Collectors.toList());

        estoqueEventProducer.sendEstoqueBaixaEvent(vendaSalva.getId().value(), produtosParaBaixa);
        logger.info("Evento de baixa de estoque enviado para venda ID: {}", vendaSalva.getId().value());

        logger.info("RealizarVendaUseCase concluído para venda ID: {}", vendaSalva.getId().value());
        return VendaOutput.from(vendaSalva);
    }
}