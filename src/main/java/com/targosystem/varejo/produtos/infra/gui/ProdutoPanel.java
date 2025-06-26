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

// Este seria um JPanel ou JFrame real, com todos os componentes Swing
public class ProdutoPanel extends JPanel {

    private JTextField idField;
    private JTextField nomeField;
    private JTextArea descricaoArea;
    private JTextField codigoBarrasField;
    private JTextField categoriaField;
    private JTextField marcaField;
    private JFormattedTextField precoField; // Usar JFormattedTextField para preço
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
    private static final int MAX_CATEGORIA_LENGTH = 50;
    private static final int MAX_MARCA_LENGTH = 50;

    public ProdutoPanel() {
        // Configura o layout principal como GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Margem entre os componentes

        // --- Inicialização dos componentes Swing ---
        idField = new JTextField(20); // Tamanho preferencial de colunas
        idField.setEditable(false);
        ((AbstractDocument) idField.getDocument()).setDocumentFilter(new LengthDocumentFilter(MAX_ID_LENGTH)); // Limita ID

        nomeField = new JTextField(30);
        ((AbstractDocument) nomeField.getDocument()).setDocumentFilter(new LengthDocumentFilter(MAX_NOME_LENGTH)); // Limita nome

        descricaoArea = new JTextArea(4, 30); // 4 linhas, 30 colunas
        descricaoArea.setLineWrap(true); // Quebra de linha automática
        descricaoArea.setWrapStyleWord(true); // Quebra por palavra
        JScrollPane descricaoScrollPane = new JScrollPane(descricaoArea); // Adiciona scroll para a área de texto
        ((AbstractDocument) descricaoArea.getDocument()).setDocumentFilter(new LengthDocumentFilter(MAX_DESCRICAO_LENGTH)); // Limita descrição

        codigoBarrasField = new JTextField(25);
        ((AbstractDocument) codigoBarrasField.getDocument()).setDocumentFilter(new LengthDocumentFilter(MAX_CODIGO_BARRAS_LENGTH)); // Limita código de barras

        categoriaField = new JTextField(20);
        ((AbstractDocument) categoriaField.getDocument()).setDocumentFilter(new LengthDocumentFilter(MAX_CATEGORIA_LENGTH)); // Limita categoria

        marcaField = new JTextField(20);
        ((AbstractDocument) marcaField.getDocument()).setDocumentFilter(new LengthDocumentFilter(MAX_MARCA_LENGTH)); // Limita marca

        // Configuração para preço (apenas números e formato monetário)
        NumberFormat priceFormat = NumberFormat.getNumberInstance();
        priceFormat.setMinimumFractionDigits(2);
        priceFormat.setMaximumFractionDigits(2);
        NumberFormatter priceFormatter = new NumberFormatter(priceFormat);
        priceFormatter.setValueClass(Double.class);
        priceFormatter.setAllowsInvalid(false); // Não permite caracteres inválidos
        priceFormatter.setCommitsOnValidEdit(true); // Atualiza o valor ao digitar
        precoField = new JFormattedTextField(priceFormatter);
        precoField.setColumns(10); // Tamanho preferencial para o campo de preço
        precoField.setValue(0.00); // Valor inicial

        cadastrarButton = new JButton("Cadastrar");
        atualizarButton = new JButton("Atualizar");
        listarButton = new JButton("Listar Produtos");

        produtosTable = new JTable();
        tableScrollPane = new JScrollPane(produtosTable);
        // Definir um tamanho preferencial para a JTable/JScrollPane
        tableScrollPane.setPreferredSize(new Dimension(800, 300));

        // --- Adicionar componentes ao GridBagLayout ---

        // Linha 0: ID
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST; // Alinha label à direita
        gbc.fill = GridBagConstraints.NONE;   // Label não se estende
        gbc.weightx = 0;                      // Label não ganha espaço extra
        add(new JLabel("ID:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST; // Alinha campo à esquerda
        gbc.fill = GridBagConstraints.HORIZONTAL; // Preenche horizontalmente na sua célula
        gbc.weightx = 0.1; // Permite uma pequena expansão, mas menos que 0.5
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
        gbc.weightx = 0.1; // Menor peso para evitar esticar demais
        add(nomeField, gbc);

        // Linha 2: Descrição
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHEAST; // Alinha label no topo-direita
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH; // Preenche em ambas as direções
        gbc.weightx = 1.0; // A descrição pode e deve se expandir mais horizontalmente
        gbc.weighty = 0.1; // Permite que a JTextArea expanda verticalmente (um pouco)
        add(descricaoScrollPane, gbc); // Adiciona o scroll pane com a área de texto

        // Linha 3: Código de Barras
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0; // Reset weighty
        add(new JLabel("Código de Barras:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.1; // Menor peso
        add(codigoBarrasField, gbc);

        // Linha 4: Categoria
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Categoria:"), gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.1; // Menor peso
        add(categoriaField, gbc);

        // Linha 5: Marca
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Marca:"), gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.1; // Menor peso
        add(marcaField, gbc);

        // Linha 6: Preço Sugerido
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(new JLabel("Preço Sugerido:"), gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.1; // Menor peso
        add(precoField, gbc);

        // Painel para os botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Centraliza botões com espaçamento
        buttonPanel.add(cadastrarButton);
        buttonPanel.add(atualizarButton);
        buttonPanel.add(listarButton);

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2; // Ocupa duas colunas
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE; // Não preenche, deixa o FlowLayout cuidar
        gbc.weightx = 0; // Não se expande horizontalmente
        add(buttonPanel, gbc);

        // Tabela de produtos (ocupa o restante da largura e altura)
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2; // Ocupa duas colunas
        gbc.fill = GridBagConstraints.BOTH; // Preenche em ambas as direções
        gbc.weightx = 1.0; // Expansível horizontalmente
        gbc.weighty = 1.0; // Expansível verticalmente
        add(tableScrollPane, gbc);

        // Adicionar um listener para seleção de linha na tabela (para edição)
        produtosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && produtosTable.getSelectedRow() != -1) {
                int selectedRow = produtosTable.getSelectedRow();
                // Verifique se os índices de coluna estão corretos com base no seu DefaultTableModel
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

    public String getProdutoCategoria() {
        return categoriaField.getText();
    }

    public String getProdutoMarca() {
        return marcaField.getText();
    }

    public String getProdutoPreco() {
        // Retorna o valor formatado como String. O Controller deve fazer o parse para Double.
        return precoField.getText().replace(",", "."); // Garante que vírgulas sejam trocadas por pontos para o parse
    }

    public void limparCampos() {
        idField.setText("");
        nomeField.setText("");
        descricaoArea.setText("");
        codigoBarrasField.setText("");
        categoriaField.setText("");
        marcaField.setText("");
        precoField.setValue(0.00); // Reseta o valor do preço
    }

    public void preencherCampos(String id, String nome, String descricao, String codigoBarras, String categoria, String marca, String preco) {
        idField.setText(id);
        nomeField.setText(nome);
        descricaoArea.setText(descricao);
        codigoBarrasField.setText(codigoBarras);
        categoriaField.setText(categoria);
        marcaField.setText(marca);
        try {
            // Tenta converter o preço para Double para definir no JFormattedTextField
            precoField.setValue(Double.parseDouble(preco.replace(",", ".")));
        } catch (NumberFormatException e) {
            precoField.setValue(0.00); // Define 0.00 se o valor for inválido
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
                return false; // Torna as células da tabela não editáveis diretamente
            }
        });

        // Esconder colunas que podem ser muito longas ou não essenciais na tabela,
        // mas que ainda são úteis para preencher os campos de edição ao selecionar uma linha.
        if (produtosTable.getColumnModel().getColumnCount() > 0) { // ID
            produtosTable.getColumnModel().getColumn(0).setMinWidth(0);
            produtosTable.getColumnModel().getColumn(0).setMaxWidth(0);
            produtosTable.getColumnModel().getColumn(0).setWidth(0);
        }
        if (produtosTable.getColumnModel().getColumnCount() > 6) { // Marca
            produtosTable.getColumnModel().getColumn(6).setMinWidth(0);
            produtosTable.getColumnModel().getColumn(6).setMaxWidth(0);
            produtosTable.getColumnModel().getColumn(6).setWidth(0);
        }
        if (produtosTable.getColumnModel().getColumnCount() > 7) { // Descrição
            produtosTable.getColumnModel().getColumn(7).setMinWidth(0);
            produtosTable.getColumnModel().getColumn(7).setMaxWidth(0);
            produtosTable.getColumnModel().getColumn(7).setWidth(0);
        }

        // Ajustar largura preferencial das colunas visíveis
        produtosTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Nome
        produtosTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Código de Barras
        produtosTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Categoria
        produtosTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Preço
        produtosTable.getColumnModel().getColumn(5).setPreferredWidth(50);  // Ativo
    }

    // Classe interna para limitar o comprimento do texto em JTextField e JTextArea
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
                Toolkit.getDefaultToolkit().beep(); // Emite um som de erro
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