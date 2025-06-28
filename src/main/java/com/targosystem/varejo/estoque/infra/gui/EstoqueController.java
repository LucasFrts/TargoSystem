package com.targosystem.varejo.estoque.infra.gui;

import com.targosystem.varejo.estoque.application.EstoqueService;
import com.targosystem.varejo.estoque.application.input.RegistrarMovimentacaoEstoqueInput;
import com.targosystem.varejo.estoque.application.input.ItemMovimentacaoInput;
import com.targosystem.varejo.estoque.application.output.EstoqueOutput;
import com.targosystem.varejo.estoque.application.output.ItemMovimentacaoOutput;
import com.targosystem.varejo.estoque.application.output.LocalEstoqueOutput;
import com.targosystem.varejo.estoque.application.output.MovimentacaoEstoqueOutput;
import com.targosystem.varejo.estoque.application.output.ItemEstoqueOutput;
import com.targosystem.varejo.estoque.application.output.ProdutoOutput;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.estoque.domain.model.TipoMovimentacao;
import com.targosystem.varejo.estoque.domain.model.TipoLocal;
import com.targosystem.varejo.produtos.application.ProdutoService; // Importar o ProdutoService do módulo de produtos

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class EstoqueController {

    private static final Logger logger = LoggerFactory.getLogger(EstoqueController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final EstoqueService estoqueService;
    private final ProdutoService produtoService;
    private final EstoqueFrame estoqueFrame;

    // Lista que conterá os itens de movimentação com TODOS os detalhes (lote, localização)
    private List<ItemMovimentacaoInput> produtosSelecionadosParaMovimentacao = new ArrayList<>();


    public EstoqueController(EstoqueService estoqueService, EstoqueFrame estoqueFrame, ProdutoService produtoService) {
        this.estoqueService = Objects.requireNonNull(estoqueService, "EstoqueService cannot be null.");
        this.estoqueFrame = Objects.requireNonNull(estoqueFrame, "EstoqueFrame cannot be null.");
        this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService cannot be null.");

        // Configuração dos listeners
        this.estoqueFrame.getBtnConsultarEstoque().addActionListener(e -> consultarEstoque());
        this.estoqueFrame.getBtnRegistrarMovimentacao().addActionListener(e -> registrarMovimentacao());
        this.estoqueFrame.getCmbTipoMovimentacao().addActionListener(e -> updateMovimentacaoFields());
        this.estoqueFrame.getBtnAdicionarProdutoMovimentacao().addActionListener(e -> abrirModalSelecaoProduto());
        this.estoqueFrame.getBtnRemoverItemMovimentacao().addActionListener(e -> removerItemDaTabelaMovimentacao());
        this.estoqueFrame.getTabbedPaneEstoques().addChangeListener(e -> carregarItensEstoqueParaAbaAtual());

        updateMovimentacaoFields();
        carregarTodasAbasDeEstoque();
    }

    public void consultarEstoque() {
        String produtoId = estoqueFrame.getTxtProdutoIdConsulta().getText().trim();
        estoqueFrame.getTxtAreaResultadoConsulta().setText("");

        if (produtoId.isEmpty()) {
            JOptionPane.showMessageDialog(estoqueFrame, "Por favor, insira o ID do produto para consultar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            logger.info("Consultando estoque total para Produto ID: {}", produtoId);
            EstoqueOutput estoque = estoqueService.consultarEstoqueTotalPorProdutoId(produtoId);

            if (estoque != null && Objects.equals(estoque.produtoId(), produtoId)) {
                com.targosystem.varejo.produtos.application.output.ProdutoOutput produtoOriginal = produtoService.obterProdutoPorId(produtoId);
                String nomeProduto = (produtoOriginal != null) ? produtoOriginal.nome() : "Produto Desconhecido";

                String result = String.format("Estoque do Produto '%s' (ID: %s):\n" +
                                "Quantidade Total Disponível: %d\n" +
                                "Em Local: %s (%s)",
                        nomeProduto,
                        estoque.produtoId(),
                        estoque.quantidadeTotalDisponivel(),
                        estoque.nomeLocalEstoque(),
                        estoque.localEstoqueId()
                );

                if (estoque.itensEstoque() != null && !estoque.itensEstoque().isEmpty()) {
                    result += "\n\nItens em Estoque:";
                    for (ItemEstoqueOutput item : estoque.itensEstoque()) {
                        result += String.format("\n- Lote: %s, Qtd: %d, Data Fab.: %s, Data Val.: %s, Corredor: %s, Prateleira: %s, Nível: %s",
                                item.numeroLote() != null ? item.numeroLote() : "N/A",
                                item.quantidade(),
                                item.dataFabricacaoLote() != null ? item.dataFabricacaoLote().format(DATE_FORMATTER) : "N/A",
                                item.dataValidadeLote() != null ? item.dataValidadeLote().format(DATE_FORMATTER) : "N/A",
                                item.corredorLocalizacao() != null ? item.corredorLocalizacao() : "N/A",
                                item.prateleiraLocalizacao() != null ? item.prateleiraLocalizacao() : "N/A",
                                item.nivelLocalizacao() != null ? item.nivelLocalizacao() : "N/A");
                    }
                }

                estoqueFrame.getTxtAreaResultadoConsulta().setText(result);
                logger.info("Consulta de estoque para produto {} concluída com sucesso. Qtd: {}", produtoId, estoque.quantidadeTotalDisponivel());
            } else {
                estoqueFrame.getTxtAreaResultadoConsulta().setText("Estoque não encontrado para o Produto ID: " + produtoId + " ou quantidade zero.");
                logger.warn("Estoque não encontrado para Produto ID: {}", produtoId);
            }
        } catch (DomainException e) {
            logger.error("Erro de domínio ao consultar estoque: {}", e.getMessage());
            JOptionPane.showMessageDialog(estoqueFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao consultar estoque para Produto ID: {}", produtoId, e);
            JOptionPane.showMessageDialog(estoqueFrame, "Erro inesperado ao consultar estoque.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void registrarMovimentacao() {
        TipoMovimentacao tipoMovimentacao = (TipoMovimentacao) estoqueFrame.getCmbTipoMovimentacao().getSelectedItem();
        String motivo = estoqueFrame.getTxtMotivoMovimentacao().getText().trim();

        if (tipoMovimentacao == null) {
            JOptionPane.showMessageDialog(estoqueFrame, "Tipo de Movimentação é obrigatório.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (produtosSelecionadosParaMovimentacao.isEmpty()) {
            JOptionPane.showMessageDialog(estoqueFrame, "É necessário adicionar pelo menos um produto para a movimentação.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String localOrigemId = null;
        String localDestinoId = null;
        LocalEstoqueOutput selectedOrigem = (LocalEstoqueOutput) estoqueFrame.getCmbLocalOrigemMovimentacao().getSelectedItem();
        LocalEstoqueOutput selectedDestino = (LocalEstoqueOutput) estoqueFrame.getCmbLocalDestinoMovimentacao().getSelectedItem();

        if (selectedOrigem != null) {
            localOrigemId = selectedOrigem.id();
        }
        if (selectedDestino != null) {
            localDestinoId = selectedDestino.id();
        }

        if (localOrigemId == null) {
            JOptionPane.showMessageDialog(estoqueFrame, "Local de Origem é obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (localDestinoId == null) {
            JOptionPane.showMessageDialog(estoqueFrame, "Local de Destino é obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tipoMovimentacao == TipoMovimentacao.TRANSFERENCIA && Objects.equals(localOrigemId, localDestinoId)) {
            JOptionPane.showMessageDialog(estoqueFrame, "Para TRANSFERÊNCIA, o Local de Origem não pode ser igual ao Local de Destino.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (motivo.isEmpty()) {
            switch (tipoMovimentacao) {
                case ENTRADA: motivo = "Entrada de mercadoria."; break;
                case SAIDA: motivo = "Saída de mercadoria."; break;
                case TRANSFERENCIA: motivo = "Transferência entre locais."; break;
            }
        }

        RegistrarMovimentacaoEstoqueInput input = new RegistrarMovimentacaoEstoqueInput(
                tipoMovimentacao,
                localOrigemId,
                localDestinoId,
                produtosSelecionadosParaMovimentacao,
                motivo
        );

        try {
            logger.info("Registrando movimentação de estoque. Tipo: {}, Origem: {}, Destino: {}, Itens: {}",
                    tipoMovimentacao, localOrigemId, localDestinoId, produtosSelecionadosParaMovimentacao.size());

            MovimentacaoEstoqueOutput movimentacaoRegistrada = estoqueService.registrarMovimentacao(input);

            StringBuilder successMessage = new StringBuilder("Movimentação registrada com sucesso:\n");
            successMessage.append(String.format("ID da Movimentação: %s\n", movimentacaoRegistrada.id()));
            successMessage.append(String.format("Tipo: %s\n", movimentacaoRegistrada.tipo()));
            successMessage.append(String.format("Local de Origem: %s\n", movimentacaoRegistrada.localOrigemId()));
            successMessage.append(String.format("Local de Destino: %s\n", movimentacaoRegistrada.localDestinoId()));
            successMessage.append(String.format("Motivo: %s\n", movimentacaoRegistrada.motivo()));
            successMessage.append("\nItens Movimentados:\n");

            String lastProdutoId = null;
            for (ItemMovimentacaoOutput item : movimentacaoRegistrada.itens()) {
                com.targosystem.varejo.produtos.application.output.ProdutoOutput produtoOriginal = produtoService.obterProdutoPorId(item.produtoId());
                String nomeProduto = (produtoOriginal != null) ? produtoOriginal.nome() : "Produto Desconhecido";
                // Ajustado para não tentar acessar campos de lote/localização que não estão em ItemMovimentacaoOutput
                successMessage.append(String.format("- Produto: %s (ID: %s), Quantidade: %d\n",
                        nomeProduto,
                        item.produtoId(),
                        item.quantidade()
                ));
                lastProdutoId = item.produtoId();
            }

            EstoqueOutput estoqueAtualizado = null;
            if (lastProdutoId != null) {
                estoqueAtualizado = estoqueService.consultarEstoqueTotalPorProdutoId(lastProdutoId);
                successMessage.append(String.format("\nNova Quantidade Total Disponível do Último Produto Movimentado (%s): %d",
                        lastProdutoId, estoqueAtualizado != null ? estoqueAtualizado.quantidadeTotalDisponivel() : 0));
            }

            JOptionPane.showMessageDialog(estoqueFrame, successMessage.toString(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            logger.info("Movimentação de estoque registrada com sucesso. Tipo: {}. Nova Qtd Total: {}",
                    tipoMovimentacao, estoqueAtualizado != null ? estoqueAtualizado.quantidadeTotalDisponivel() : 0);

            estoqueFrame.clearMovimentacaoFields();
            produtosSelecionadosParaMovimentacao.clear(); // Limpa a lista interna
            updateMovimentacaoFields();
            carregarTodasAbasDeEstoque();
        } catch (DomainException e) {
            logger.error("Erro de domínio ao registrar movimentação: {}", e.getMessage());
            JOptionPane.showMessageDialog(estoqueFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao registrar movimentação.", e);
            JOptionPane.showMessageDialog(estoqueFrame, "Erro inesperado ao registrar movimentação.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMovimentacaoFields() {
        TipoMovimentacao selectedType = (TipoMovimentacao) estoqueFrame.getCmbTipoMovimentacao().getSelectedItem();

        if (selectedType == null) {
            selectedType = TipoMovimentacao.ENTRADA; // Fallback
        }

        estoqueFrame.getModelItensMovimentacao().setRowCount(0); // Limpa a tabela visível
        produtosSelecionadosParaMovimentacao.clear(); // Limpa a lista interna de itens

        estoqueFrame.updateMovimentacaoFieldsVisibility(selectedType);
        popularLocalEstoqueComboBoxes(selectedType);
    }

    private void popularLocalEstoqueComboBoxes(TipoMovimentacao tipoMovimentacao) {
        estoqueFrame.getCmbLocalOrigemMovimentacao().removeAllItems();
        estoqueFrame.getCmbLocalDestinoMovimentacao().removeAllItems();

        if (tipoMovimentacao == null) {
            logger.warn("Tipo de movimentação é null ao tentar popular comboboxes de local de estoque. Abortando.");
            return;
        }

        List<LocalEstoqueOutput> locaisOrigem = new ArrayList<>();
        List<LocalEstoqueOutput> locaisDestino = new ArrayList<>();

        try {
            if (tipoMovimentacao == TipoMovimentacao.ENTRADA) {
                locaisOrigem = estoqueService.listarLocaisEstoquePorTipo(TipoLocal.FORNECEDOR);
                locaisDestino = estoqueService.listarLocaisEstoquePorTipo(TipoLocal.INTERNO);
                logger.debug("Populando comboboxes para ENTRADA: {} locais de origem, {} locais de destino.", locaisOrigem.size(), locaisDestino.size());
            } else if (tipoMovimentacao == TipoMovimentacao.SAIDA) {
                locaisOrigem = estoqueService.listarLocaisEstoquePorTipo(TipoLocal.INTERNO);
                locaisDestino = estoqueService.listarLocaisEstoquePorTipo(TipoLocal.CLIENTE);
                logger.debug("Populando comboboxes para SAIDA: {} locais de origem, {} locais de destino.", locaisOrigem.size(), locaisDestino.size());
            } else if (tipoMovimentacao == TipoMovimentacao.TRANSFERENCIA) {
                locaisOrigem = estoqueService.listarLocaisEstoquePorTipo(TipoLocal.INTERNO);
                locaisDestino = estoqueService.listarLocaisEstoquePorTipo(TipoLocal.INTERNO);
                logger.debug("Populando comboboxes para TRANSFERENCIA: {} locais de origem, {} locais de destino.", locaisOrigem.size(), locaisDestino.size());
            }

            estoqueFrame.popularComboBoxLocalEstoque(estoqueFrame.getCmbLocalOrigemMovimentacao(), locaisOrigem);
            estoqueFrame.popularComboBoxLocalEstoque(estoqueFrame.getCmbLocalDestinoMovimentacao(), locaisDestino);

            if (!locaisOrigem.isEmpty()) {
                estoqueFrame.getCmbLocalOrigemMovimentacao().setSelectedIndex(0);
            } else {
                estoqueFrame.getCmbLocalOrigemMovimentacao().setSelectedItem(null);
            }
            if (!locaisDestino.isEmpty()) {
                estoqueFrame.getCmbLocalDestinoMovimentacao().setSelectedIndex(0);
            } else {
                estoqueFrame.getCmbLocalDestinoMovimentacao().setSelectedItem(null);
            }

        } catch (Exception e) {
            logger.error("Erro ao popular comboboxes de LocalEstoque: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(estoqueFrame, "Erro ao carregar locais de estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirModalSelecaoProduto() {
        Frame ownerFrame = (Frame) SwingUtilities.getWindowAncestor(estoqueFrame);
        TipoMovimentacao currentMovimentationType = (TipoMovimentacao) estoqueFrame.getCmbTipoMovimentacao().getSelectedItem();

        // 1. Abre o modal para selecionar PRODUTO e QUANTIDADE (SelectProductModal)
        SelectProductModal selectProductModal = new SelectProductModal(ownerFrame, searchTerm -> {
            try {
                List<com.targosystem.varejo.produtos.application.output.ProdutoOutput> productsFromProductModule = produtoService.buscarProdutosAtivosPorNomeOuCodigo(searchTerm);

                List<ProdutoOutput> productsForModal = new ArrayList<>();
                for(com.targosystem.varejo.produtos.application.output.ProdutoOutput p : productsFromProductModule) {
                    EstoqueOutput stock = estoqueService.consultarEstoqueTotalPorProdutoId(p.id());
                    productsForModal.add(new ProdutoOutput(
                            p.id(),
                            p.nome(),
                            p.descricao(),
                            p.precoSugerido(),
                            p.codigoBarras(),
                            p.categoriaNome(),
                            p.marca(),
                            p.ativo(),
                            stock != null ? stock.quantidadeTotalDisponivel() : 0L
                    ));
                }
                return productsForModal; // Can be empty, not null
            } catch (Exception e) {
                logger.error("Error searching products in modal: {}", e.getMessage());
                JOptionPane.showMessageDialog(ownerFrame, "Error searching products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return new ArrayList<>();
            }
        });
        selectProductModal.setVisible(true);

        SelectProductModal.SelectedProductInfo selectedProductInfo = selectProductModal.getSelectedProductInfo();

        if (selectedProductInfo != null) {
            // 2. Se um produto foi selecionado, abre o SEGUNDO modal para detalhes de LOTE e LOCALIZAÇÃO (EstoqueFrame.showProductSelectionDialog)
            // PASSANDO AS INFORMAÇÕES JÁ SELECIONADAS DO PRIMEIRO MODAL
            EstoqueFrame.ProductInputData productInputData = estoqueFrame.showProductSelectionDialog(
                    ownerFrame,
                    currentMovimentationType,
                    selectedProductInfo.productId,
                    selectedProductInfo.productName,
                    selectedProductInfo.quantity
            );

            if (productInputData != null) {
                // Verifica se o produto já foi adicionado para evitar duplicidade na tabela
                boolean alreadyAdded = produtosSelecionadosParaMovimentacao.stream()
                        .anyMatch(item -> item.produtoId().equals(selectedProductInfo.productId)); // A comparação deve ser apenas pelo ID do produto, pois lote/localização podem ser diferentes

                if (!alreadyAdded) {
                    // Cria o ItemMovimentacaoInput completo com todos os detalhes
                    ItemMovimentacaoInput itemMovimentacao = new ItemMovimentacaoInput(
                            productInputData.produtoId,
                            productInputData.quantidade,
                            productInputData.numeroLote,
                            productInputData.dataFabricacaoLote,
                            productInputData.dataValidadeLote,
                            productInputData.corredor,
                            productInputData.prateleira,
                            productInputData.nivel
                    );
                    produtosSelecionadosParaMovimentacao.add(itemMovimentacao);

                    // Adiciona o item à tabela visível no EstoqueFrame
                    estoqueFrame.adicionarItemMovimentacao(productInputData);

                    logger.info("Produto {} (Qtd: {}) com detalhes de lote/localização adicionado à movimentação.",
                            productInputData.produtoNome, productInputData.quantidade);
                } else {
                    JOptionPane.showMessageDialog(estoqueFrame, "Produto " + selectedProductInfo.productName + " (ID: " + selectedProductInfo.productId + ") já adicionado. Remova-o na tela principal e adicione novamente com os novos detalhes se desejar alterar.", "Atenção", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                logger.info("Seleção de detalhes de lote/localização cancelada para o produto {}.", selectedProductInfo.productName);
            }
        } else {
            logger.info("Seleção de produto no modal principal cancelada.");
        }
    }


    private void removerItemDaTabelaMovimentacao() {
        int selectedRow = estoqueFrame.getTblItensMovimentacao().getSelectedRow();
        if (selectedRow >= 0) {
            estoqueFrame.getModelItensMovimentacao().removeRow(selectedRow);

            if (selectedRow < produtosSelecionadosParaMovimentacao.size()) {
                produtosSelecionadosParaMovimentacao.remove(selectedRow);
            }
            logger.info("Item removido da movimentação. Linha: {}", selectedRow);
        } else {
            JOptionPane.showMessageDialog(estoqueFrame, "Por favor, selecione um item para remover.", "Nenhum Item Selecionado", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void carregarTodasAbasDeEstoque() {
        try {
            List<LocalEstoqueOutput> locaisInternos = estoqueService.listarLocaisEstoquePorTipo(TipoLocal.INTERNO);
            Map<String, List<ItemEstoqueOutput>> itensPorLocal = new HashMap<>();

            for (LocalEstoqueOutput local : locaisInternos) {
                List<ItemEstoqueOutput> itens = estoqueService.consultarItensEstoquePorLocalId(local.id());
                itensPorLocal.put(local.id(), itens);
            }
            estoqueFrame.atualizarAbasDeEstoque(locaisInternos, itensPorLocal);
            logger.info("Abas de estoque carregadas com sucesso para {} locais internos.", locaisInternos.size());

            if (!locaisInternos.isEmpty()) {
                carregarItensEstoqueParaAbaAtual();
            }

        } catch (Exception e) {
            logger.error("Erro ao carregar abas de estoque: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(estoqueFrame, "Erro ao carregar abas de estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarItensEstoqueParaAbaAtual() {
        int selectedTabIndex = estoqueFrame.getTabbedPaneEstoques().getSelectedIndex();
        if (selectedTabIndex != -1) {
            Component selectedComponent = estoqueFrame.getTabbedPaneEstoques().getComponentAt(selectedTabIndex);
            if (selectedComponent instanceof JPanel) {
                JPanel panelLocalEstoque = (JPanel) selectedComponent;
                JScrollPane scrollPane = (JScrollPane) panelLocalEstoque.getComponent(0);
                JTable table = (JTable) scrollPane.getViewport().getView();
                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                tableModel.setRowCount(0);

                String tabTitle = estoqueFrame.getTabbedPaneEstoques().getTitleAt(selectedTabIndex);
                try {
                    List<LocalEstoqueOutput> locaisInternos = estoqueService.listarLocaisEstoquePorTipo(TipoLocal.INTERNO);
                    String currentLocalEstoqueId = null;
                    for (LocalEstoqueOutput local : locaisInternos) {
                        if (local.nome().equals(tabTitle)) {
                            currentLocalEstoqueId = local.id();
                            break;
                        }
                    }

                    if (currentLocalEstoqueId != null) {
                        List<ItemEstoqueOutput> itens = estoqueService.consultarItensEstoquePorLocalId(currentLocalEstoqueId);
                        for (ItemEstoqueOutput item : itens) {
                            com.targosystem.varejo.produtos.application.output.ProdutoOutput produto = produtoService.obterProdutoPorId(item.produtoId());
                            String produtoNome = (produto != null) ? produto.nome() : "Produto Desconhecido";
                            tableModel.addRow(new Object[]{
                                    item.produtoId(),
                                    produtoNome,
                                    item.numeroLote() != null ? item.numeroLote() : "N/A",
                                    item.quantidade(),
                                    item.dataFabricacaoLote() != null ? item.dataFabricacaoLote().format(DATE_FORMATTER) : "N/A",
                                    item.dataValidadeLote() != null ? item.dataValidadeLote().format(DATE_FORMATTER) : "N/A",
                                    item.corredorLocalizacao() != null ? item.corredorLocalizacao() : "N/A",
                                    item.prateleiraLocalizacao() != null ? item.prateleiraLocalizacao() : "N/A",
                                    item.nivelLocalizacao() != null ? item.nivelLocalizacao() : "N/A"
                            });
                        }
                    }
                } catch (Exception e) {
                    logger.error("Erro ao carregar itens da aba selecionada: {}", e.getMessage(), e);
                    JOptionPane.showMessageDialog(estoqueFrame, "Erro ao carregar itens da aba: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}