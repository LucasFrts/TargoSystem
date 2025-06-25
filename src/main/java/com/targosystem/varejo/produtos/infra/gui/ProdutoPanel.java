// src/main/java/com/targosystem/varejo/produtos/infra/gui/ProdutoPanel.java
package com.targosystem.varejo.produtos.infra.gui;

import com.targosystem.varejo.produtos.application.output.ProdutoOutput;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;

// Este seria um JPanel ou JFrame real, com todos os componentes Swing
public class ProdutoPanel extends JPanel { // Ou JFrame

    // Componentes de UI (apenas para ilustrar, não implementados)
    private JTextField idField;
    private JTextField nomeField;
    private JTextArea descricaoArea;
    private JTextField codigoBarrasField;
    private JTextField categoriaField;
    private JTextField marcaField;
    private JTextField precoField;
    private JButton cadastrarButton;
    private JButton atualizarButton;
    private JButton listarButton;
    private JTable produtosTable;
    private JScrollPane tableScrollPane;

    public ProdutoPanel() {
        // Inicialização dos componentes Swing (apenas stub)
        idField = new JTextField(30);
        idField.setEditable(false); // ID geralmente não é editado manualmente
        nomeField = new JTextField(30);
        descricaoArea = new JTextArea(5, 30);
        codigoBarrasField = new JTextField(20);
        categoriaField = new JTextField(20);
        marcaField = new JTextField(20);
        precoField = new JTextField(10);
        cadastrarButton = new JButton("Cadastrar");
        atualizarButton = new JButton("Atualizar");
        listarButton = new JButton("Listar Produtos");
        produtosTable = new JTable();
        tableScrollPane = new JScrollPane(produtosTable);

        // Layout básico (apenas para ilustração)
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("ID:"));
        add(idField);
        add(new JLabel("Nome:"));
        add(nomeField);
        add(new JLabel("Descrição:"));
        add(descricaoArea);
        add(new JLabel("Código de Barras:"));
        add(codigoBarrasField);
        add(new JLabel("Categoria:"));
        add(categoriaField);
        add(new JLabel("Marca:"));
        add(marcaField);
        add(new JLabel("Preço Sugerido:"));
        add(precoField);
        add(cadastrarButton);
        add(atualizarButton);
        add(listarButton);
        add(tableScrollPane);

        // Adicionar um listener para seleção de linha na tabela (para edição)
        // produtosTable.getSelectionModel().addListSelectionListener(e -> {
        //     if (!e.getValueIsAdjusting() && produtosTable.getSelectedRow() != -1) {
        //         // Obter o ID do produto da linha selecionada e chamar o controller para carregar
        //         String selectedProductId = (String) produtosTable.getModel().getValueAt(produtosTable.getSelectedRow(), 0); // Supondo ID na coluna 0
        //         // Ação de carregar para edição deve ser definida no controller
        //         // Ex: controller.carregarProdutoParaEdicao(selectedProductId);
        //     }
        // });
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
        return precoField.getText();
    }

    public void limparCampos() {
        idField.setText("");
        nomeField.setText("");
        descricaoArea.setText("");
        codigoBarrasField.setText("");
        categoriaField.setText("");
        marcaField.setText("");
        precoField.setText("");
    }

    public void preencherCampos(String id, String nome, String descricao, String codigoBarras, String categoria, String marca, String preco) {
        idField.setText(id);
        nomeField.setText(nome);
        descricaoArea.setText(descricao);
        codigoBarrasField.setText(codigoBarras);
        categoriaField.setText(categoria);
        marcaField.setText(marca);
        precoField.setText(preco);
    }

    public void exibirProdutos(List<ProdutoOutput> produtos) {
        // Lógica para preencher a JTable com os produtos
        // Isso normalmente envolveria um DefaultTableModel e mapeamento dos ProdutoOutput
        // Ex:
        String[] columnNames = {"ID", "Nome", "Código de Barras", "Categoria", "Preço", "Ativo"};
        Object[][] data = new Object[produtos.size()][6];
        for (int i = 0; i < produtos.size(); i++) {
            ProdutoOutput p = produtos.get(i);
            data[i][0] = p.id();
            data[i][1] = p.nome();
            data[i][2] = p.codigoBarras();
            data[i][3] = p.categoriaNome();
            data[i][4] = p.precoSugerido();
            data[i][5] = p.ativo();
        }
        produtosTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }
}