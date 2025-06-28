package com.targosystem.varejo.estoque.infra.gui;

import com.targosystem.varejo.estoque.application.output.ProdutoOutput; // Supondo que você tem este Output (do módulo de estoque)
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class SelectProductModal extends JDialog {

    private JTextField txtSearchTerm;
    private JButton btnSearch;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton btnAddSelected;
    private JButton btnCancel;

    // Classe interna para encapsular as informações do produto selecionado neste modal
    public static class SelectedProductInfo {
        public final String productId;
        public final String productName;
        public final int quantity; // Quantidade a ser usada para a movimentação
        public final long availableQuantity; // Quantidade disponível no estoque para o produto

        public SelectedProductInfo(String productId, String productName, int quantity, long availableQuantity) {
            this.productId = Objects.requireNonNull(productId, "Product ID cannot be null.");
            this.productName = Objects.requireNonNull(productName, "Product Name cannot be null.");
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive.");
            }
            this.quantity = quantity;
            this.availableQuantity = availableQuantity;
        }
    }

    private SelectedProductInfo selectedResult; // Armazena o resultado da seleção

    // Callback para buscar produtos (vindo do Controller)
    // O callback agora espera retornar a lista de ProdutoOutput do módulo de ESTOQUE, que contém a quantidade total.
    private java.util.function.Function<String, List<ProdutoOutput>> searchProductsCallback;

    public SelectProductModal(Frame owner, java.util.function.Function<String, List<ProdutoOutput>> searchProductsCallback) {
        super(owner, "Selecionar Produto para Movimentação", true); // true para modal
        this.searchProductsCallback = Objects.requireNonNull(searchProductsCallback, "Search Products Callback cannot be null.");
        initComponents();
        setupListeners();
        setSize(700, 500);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10)); // Adiciona espaçamento

        // Painel de Busca
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearchTerm = new JTextField(30);
        btnSearch = new JButton("Buscar");
        searchPanel.add(new JLabel("Buscar Produto (ID ou Nome):"));
        searchPanel.add(txtSearchTerm);
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);

        // Tabela de Produtos
        // Colunas: ID, Nome, Descrição, Qtd. Disponível (Estoque Total)
        String[] columnNames = {"ID", "Nome", "Descrição", "Qtd. Disponível"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Nenhuma célula editável na tabela de busca
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite selecionar uma linha
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAddSelected = new JButton("Adicionar Selecionado");
        btnCancel = new JButton("Cancelar");
        buttonPanel.add(btnAddSelected);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnSearch.addActionListener(e -> performSearch());
        txtSearchTerm.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });

        btnAddSelected.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    String productId = (String) productTable.getValueAt(selectedRow, 0);
                    String productName = (String) productTable.getValueAt(selectedRow, 1);
                    // Obter a quantidade disponível diretamente da tabela
                    long availableQuantity = (long) productTable.getValueAt(selectedRow, 3);


                    String quantityStr = JOptionPane.showInputDialog(this, "Informe a quantidade para " + productName + " (ID: " + productId + ")\nQuantidade Disponível: " + availableQuantity + ":", "Quantidade", JOptionPane.QUESTION_MESSAGE);
                    if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                        int quantity = Integer.parseInt(quantityStr.trim());
                        if (quantity > 0) {
                            // Removida a validação de TipoMovimentacao aqui.
                            // A validação completa do estoque para movimentações de SAIDA/TRANSFERENCIA deve ocorrer no EstoqueService
                            // e no EstoqueController após o SelectProductModal retornar.

                            // Armazena o resultado e fecha o modal
                            selectedResult = new SelectedProductInfo(productId, productName, quantity, availableQuantity);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "A quantidade deve ser um número inteiro positivo.", "Erro de Quantidade", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        // Se o usuário clicou em Cancelar no input dialog ou deixou vazio, não faz nada e mantém o modal de seleção aberto
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Quantidade inválida. Por favor, insira um número inteiro.", "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao selecionar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecione um produto na tabela.", "Nenhum Produto Selecionado", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> {
            selectedResult = null; // Indica cancelamento
            dispose(); // Fecha o modal
        });
    }

    private void performSearch() {
        String searchTerm = txtSearchTerm.getText().trim();
        tableModel.setRowCount(0); // Limpa resultados anteriores

        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um termo para buscar (ID ou Nome).", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Chama o callback que o controller forneceu, esperando a ProdutoOutput do estoque
        List<ProdutoOutput> results = searchProductsCallback.apply(searchTerm);
        if (results != null && !results.isEmpty()) {
            for (ProdutoOutput produto : results) {
                tableModel.addRow(new Object[]{
                        produto.id(),
                        produto.nome(),
                        produto.descricao(),
                        produto.quantidadeDisponivel()
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nenhum produto encontrado para o termo: " + searchTerm, "Sem Resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Retorna o resultado da seleção.
     * @return SelectedProductInfo contendo o ID, Nome e Quantidade selecionados, ou null se cancelado.
     */
    public SelectedProductInfo getSelectedProductInfo() {
        return selectedResult;
    }
}