package com.targosystem.varejo.produtos.infra.gui;

import com.targosystem.varejo.produtos.application.output.ProdutoOutput;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProdutoPanel extends JPanel {

    private JTextField idField;
    private JTextField nomeField;
    private JTextArea descricaoArea;
    private JTextField codigoBarrasField;
    private JComboBox<String> categoriaComboBox; // Alterado de JTextField para JComboBox
    private JTextField marcaField; // Voltando para JTextField
    private JFormattedTextField precoField;
    private JButton cadastrarButton;
    private JButton atualizarButton;
    private JButton listarButton;
    private JTable produtosTable;
    private JScrollPane tableScrollPane;

    // Constantes para limites de caracteres (ajuste conforme seu modelo de dados/DB)
    private static final int MAX_ID_LENGTH = 36; // UUID padrão
    private static final int MAX_NOME_LENGTH = 100;
    private static final int MAX_DESCRICAO_LENGTH = 500;
    private static final int MAX_CODIGO_BARRAS_LENGTH = 50;
    private static final int MAX_CATEGORIA_LENGTH = 50; // Ainda útil para o DocumentFilter do editor do JComboBox
    private static final int MAX_MARCA_LENGTH = 50;

    public ProdutoPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // --- Inicialização dos componentes Swing ---
        idField = new JTextField(20);
        idField.setEditable(false);
        ((AbstractDocument) idField.getDocument()).setDocumentFilter(new LengthDocumentFilter(MAX_ID_LENGTH));

        nomeField = new JTextField(30);
        ((AbstractDocument) nomeField.getDocument()).setDocumentFilter(new LengthDocumentFilter(MAX_NOME_LENGTH));

        descricaoArea = new JTextArea(4, 30);
        descricaoArea.setLineWrap(true);
        descricaoArea.setWrapStyleWord(true);
        JScrollPane descricaoScrollPane = new JScrollPane(descricaoArea);
        ((AbstractDocument) descricaoArea.getDocument()).setDocumentFilter(new LengthDocumentFilter(MAX_DESCRICAO_LENGTH));

        codigoBarrasField = new JTextField(25);
        ((AbstractDocument) codigoBarrasField.getDocument()).setDocumentFilter(new LengthDocumentFilter(MAX_CODIGO_BARRAS_LENGTH));

        // INÍCIO DA ALTERAÇÃO PARA JComboBox DA CATEGORIA
        categoriaComboBox = new JComboBox<>();
        categoriaComboBox.setEditable(true); // Permite digitar um novo valor

        // Adiciona um DocumentFilter ao editor do JComboBox para limitar o tamanho da string digitada
        JTextField categoriaEditor = (JTextField) categoriaComboBox.getEditor().getEditorComponent();
        ((AbstractDocument) categoriaEditor.getDocument()).setDocumentFilter(new LengthDocumentFilter(MAX_CATEGORIA_LENGTH));
        // FIM DA ALTERAÇÃO PARA JComboBox DA CATEGORIA

        // Marca volta a ser um JTextField
        marcaField = new JTextField(20);
        ((AbstractDocument) marcaField.getDocument()).setDocumentFilter(new LengthDocumentFilter(MAX_MARCA_LENGTH));

        // Configuração para preço
        NumberFormat priceFormat = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        priceFormat.setMinimumFractionDigits(2);
        priceFormat.setMaximumFractionDigits(2);
        priceFormat.setGroupingUsed(true);

        NumberFormatter priceFormatter = new NumberFormatter(priceFormat);
        priceFormatter.setValueClass(Double.class);
        priceFormatter.setAllowsInvalid(false);
        priceFormatter.setCommitsOnValidEdit(true);
        precoField = new JFormattedTextField(priceFormatter);
        precoField.setColumns(10);
        precoField.setValue(0.00);

        cadastrarButton = new JButton("Cadastrar");
        atualizarButton = new JButton("Atualizar");
        listarButton = new JButton("Listar Produtos");

        produtosTable = new JTable();
        tableScrollPane = new JScrollPane(produtosTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 300));

        // --- Adicionar componentes ao GridBagLayout ---

        // Linha 0: ID
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("ID:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.1;
        add(idField, gbc);

        // Linha 1: Nome
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.1;
        add(nomeField, gbc);

        // Linha 2: Descrição
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        add(descricaoScrollPane, gbc);

        // Linha 3: Código de Barras
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        add(new JLabel("Código de Barras:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.1;
        add(codigoBarrasField, gbc);

        // Linha 4: Categoria (agora JComboBox)
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Categoria:"), gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.1;
        add(categoriaComboBox, gbc); // Adiciona o JComboBox de Categoria

        // Linha 5: Marca (voltando para JTextField)
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Marca:"), gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.1;
        add(marcaField, gbc); // Adiciona o JTextField de Marca

        // Linha 6: Preço Sugerido
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Preço Sugerido:"), gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.1;
        add(precoField, gbc);

        // Painel para os botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(cadastrarButton);
        buttonPanel.add(atualizarButton);
        buttonPanel.add(listarButton);

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(buttonPanel, gbc);

        // Tabela de produtos
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(tableScrollPane, gbc);

        // Adicionar um listener para seleção de linha na tabela (para edição)
        produtosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && produtosTable.getSelectedRow() != -1) {
                int selectedRow = produtosTable.getSelectedRow();
                preencherCampos(
                        (String) produtosTable.getModel().getValueAt(selectedRow, 0), // ID
                        (String) produtosTable.getModel().getValueAt(selectedRow, 1), // Nome
                        (String) produtosTable.getModel().getValueAt(selectedRow, 7), // Descrição
                        (String) produtosTable.getModel().getValueAt(selectedRow, 2), // Código de Barras
                        (String) produtosTable.getModel().getValueAt(selectedRow, 3), // Categoria
                        (String) produtosTable.getModel().getValueAt(selectedRow, 6), // Marca
                        String.valueOf(produtosTable.getModel().getValueAt(selectedRow, 4)) // Preço
                );
            }
        });
    }

    // Métodos para o Controller interagir com a View
    public void setCadastrarButtonAction(ActionListener listener) {
        cadastrarButton.addActionListener(listener);
    }

    public void setAtualizarButtonAction(ActionListener listener) {
        atualizarButton.addActionListener(listener);
    }

    public void setListarButtonAction(ActionListener listener) {
        listarButton.addActionListener(listener);
    }

    public String getProdutoId() {
        return idField.getText();
    }

    public String getProdutoNome() {
        return nomeField.getText();
    }

    public String getProdutoDescricao() {
        return descricaoArea.getText();
    }

    public String getProdutoCodigoBarras() {
        return codigoBarrasField.getText();
    }

    // MÉTODO getProdutoCategoria() - AGORA DO JComboBox
    public String getProdutoCategoria() {
        Object selected = categoriaComboBox.getSelectedItem();
        return (selected != null) ? selected.toString() : "";
    }

    // MÉTODO getProdutoMarca() - VOLTOU A SER DO JTextField
    public String getProdutoMarca() {
        return marcaField.getText();
    }

    public String getProdutoPreco() {
        return precoField.getText().replace(",", ".");
    }

    public Double getProdutoPrecoAsDouble() {
        Object value = precoField.getValue();
        if (value instanceof Double) {
            return (Double) value;
        }
        try {
            return Double.parseDouble(precoField.getText().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Preço inválido. Não foi possível converter para número: " + precoField.getText(), e);
        }
    }

    public void limparCampos() {
        idField.setText("");
        nomeField.setText("");
        descricaoArea.setText("");
        codigoBarrasField.setText("");
        categoriaComboBox.setSelectedItem(""); // Limpa o JComboBox de Categoria
        marcaField.setText(""); // Limpa o JTextField de Marca
        precoField.setValue(0.00);
    }

    public void preencherCampos(String id, String nome, String descricao, String codigoBarras, String categoria, String marca, String preco) {
        idField.setText(id);
        nomeField.setText(nome);
        descricaoArea.setText(descricao);
        codigoBarrasField.setText(codigoBarras);

        // Preenche o JComboBox de Categoria
        categoriaComboBox.setSelectedItem(categoria);

        // Preenche o JTextField de Marca
        marcaField.setText(marca);

        try {
            precoField.setValue(Double.parseDouble(preco.replace(",", ".")));
        } catch (NumberFormatException e) {
            precoField.setValue(0.00);
        }
    }

    // NOVO MÉTODO: Para o Controller popular o JComboBox de categorias
    public void setCategorias(List<String> categorias) {
        categoriaComboBox.removeAllItems();
        for (String categoria : categorias) {
            categoriaComboBox.addItem(categoria);
        }
        // Opcional: Selecionar o primeiro item ou deixar vazio
        if (!categorias.isEmpty()) {
            categoriaComboBox.setSelectedIndex(0);
        } else {
            categoriaComboBox.setSelectedItem(""); // Garante que o campo esteja vazio se não houver categorias
        }
    }

    public void exibirProdutos(List<ProdutoOutput> produtos) {
        String[] columnNames = {"ID", "Nome", "Código de Barras", "Categoria", "Preço", "Ativo", "Marca", "Descrição"};
        Object[][] data = new Object[produtos.size()][columnNames.length];

        for (int i = 0; i < produtos.size(); i++) {
            ProdutoOutput p = produtos.get(i);
            data[i][0] = p.id();
            data[i][1] = p.nome();
            data[i][2] = p.codigoBarras();
            data[i][3] = p.categoriaNome();
            data[i][4] = p.precoSugerido();
            data[i][5] = p.ativo();
            data[i][6] = p.marca();
            data[i][7] = p.descricao();
        }
        produtosTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        // Esconder colunas
        if (produtosTable.getColumnModel().getColumnCount() > 0) { // ID
            produtosTable.getColumnModel().getColumn(0).setMinWidth(0);
            produtosTable.getColumnModel().getColumn(0).setMaxWidth(0);
            produtosTable.getColumnModel().getColumn(0).setWidth(0);
        }
        // Colunas de Marca e Descrição ainda podem ser escondidas se desejar, mas
        // para demonstração, deixarei visíveis no exemplo, como estavam antes da última alteração.
        // Se desejar escondê-las novamente, descomente as linhas abaixo.
        // if (produtosTable.getColumnModel().getColumnCount() > 6) { // Marca
        //     produtosTable.getColumnModel().getColumn(6).setMinWidth(0);
        //     produtosTable.getColumnModel().getColumn(6).setMaxWidth(0);
        //     produtosTable.getColumnModel().getColumn(6).setWidth(0);
        // }
        if (produtosTable.getColumnModel().getColumnCount() > 7) { // Descrição
            produtosTable.getColumnModel().getColumn(7).setMinWidth(0);
            produtosTable.getColumnModel().getColumn(7).setMaxWidth(0);
            produtosTable.getColumnModel().getColumn(7).setWidth(0);
        }

        // Ajustar largura preferencial das colunas visíveis
        produtosTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Nome
        produtosTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Código de Barras
        produtosTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Categoria (agora visível)
        produtosTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Preço
        produtosTable.getColumnModel().getColumn(5).setPreferredWidth(50);  // Ativo
        produtosTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Marca (voltando a ter largura)
    }

    private static class LengthDocumentFilter extends DocumentFilter {
        private int maxLength;

        public LengthDocumentFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (fb.getDocument().getLength() + string.length() <= maxLength) {
                super.insertString(fb, offset, string, attr);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (fb.getDocument().getLength() - length + text.length() <= maxLength) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
}