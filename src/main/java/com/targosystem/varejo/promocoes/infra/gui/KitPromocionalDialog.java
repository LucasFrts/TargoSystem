package com.targosystem.varejo.promocoes.infra.gui;

import com.targosystem.varejo.promocoes.application.input.CriarKitPromocionalInput;
import com.targosystem.varejo.promocoes.application.input.KitItemInput;
import com.targosystem.varejo.promocoes.application.output.KitItemOutput;
import com.targosystem.varejo.promocoes.application.output.KitPromocionalOutput;
import com.targosystem.varejo.produtos.application.output.ProdutoOutput;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class KitPromocionalDialog extends JDialog {

    private JTextField idField;
    private JTextField nomeField;
    private JTextArea descricaoArea;
    private JTextField precoFixoKitField;
    private JList<ProdutoOutput> produtosDisponiveisList;
    private DefaultListModel<ProdutoOutput> produtosDisponiveisModel;
    private JList<ProdutoOutput> produtosSelecionadosList;
    private DefaultListModel<ProdutoOutput> produtosSelecionadosModel;
    private JButton addProdutoButton;
    private JButton removeProdutoButton;
    private JButton saveButton;
    private JButton cancelButton;

    private boolean saved = false;
    private String originalKitId = null;

    public KitPromocionalDialog(Frame owner, List<ProdutoOutput> allProdutos) {
        super(owner, "Novo Kit Promocional", true);
        setupUI(allProdutos, owner);
    }

    public KitPromocionalDialog(Frame owner, KitPromocionalOutput kit, List<ProdutoOutput> allProdutos) {
        super(owner, "Editar Kit Promocional", true);
        this.originalKitId = kit.id();
        setupUI(allProdutos, owner);
        loadKitData(kit);
    }

    private void setupUI(List<ProdutoOutput> allProdutos, Frame owner) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // ID
        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        idField = new JTextField(20);
        idField.setEditable(false);
        add(idField, gbc);

        // Nome
        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        nomeField = new JTextField(20);
        add(nomeField, gbc);

        // Descrição
        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1;
        descricaoArea = new JTextArea(3, 20);
        descricaoArea.setLineWrap(true);
        descricaoArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descricaoArea);
        add(descScrollPane, gbc);

        // Preço Fixo do Kit
        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Preço Fixo do Kit:"), gbc);
        gbc.gridx = 1;
        precoFixoKitField = new JTextField(10);
        add(precoFixoKitField, gbc);

        // Produtos
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.weightx = 0.5; gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        add(new JLabel("Produtos Disponíveis:"), gbc);
        gbc.gridx = 2; add(new JLabel("Produtos no Kit:"), gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weighty = 1.0;
        produtosDisponiveisModel = new DefaultListModel<>();
        for (ProdutoOutput p : allProdutos) {
            produtosDisponiveisModel.addElement(p);
        }
        produtosDisponiveisList = new JList<>(produtosDisponiveisModel);
        // Exemplo de como renderizar ProdutoOutput na JList (se ProdutoOutput não tiver toString adequado)
        produtosDisponiveisList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProdutoOutput produto) {
                    setText(produto.nome() + " (ID: " + produto.id() + ")");
                }
                return this;
            }
        });
        produtosDisponiveisList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(new JScrollPane(produtosDisponiveisList), gbc);

        gbc.gridx = 2;
        produtosSelecionadosModel = new DefaultListModel<>();
        produtosSelecionadosList = new JList<>(produtosSelecionadosModel);
        // Exemplo de como renderizar ProdutoOutput na JList (se ProdutoOutput não tiver toString adequado)
        produtosSelecionadosList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProdutoOutput produto) {
                    setText(produto.nome() + " (ID: " + produto.id() + ")");
                }
                return this;
            }
        });
        produtosSelecionadosList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(new JScrollPane(produtosSelecionadosList), gbc);

        gbc.gridx = 1; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JPanel arrowButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints arrowGbc = new GridBagConstraints();
        arrowGbc.insets = new Insets(5, 2, 5, 2);
        arrowGbc.gridx = 0; arrowGbc.gridy = 0;
        addProdutoButton = new JButton(">");
        arrowButtonPanel.add(addProdutoButton, arrowGbc);
        arrowGbc.gridy = 1;
        removeProdutoButton = new JButton("<");
        arrowButtonPanel.add(removeProdutoButton, arrowGbc);
        add(arrowButtonPanel, gbc);

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Salvar");
        cancelButton = new JButton("Cancelar");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        add(buttonPanel, gbc);

        // Listeners
        addProdutoButton.addActionListener(e -> addSelectedProducts());
        removeProdutoButton.addActionListener(e -> removeSelectedProducts());
        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> onCancel());

        pack();
        setLocationRelativeTo(owner);
    }

    private void addSelectedProducts() {
        List<ProdutoOutput> selected = produtosDisponiveisList.getSelectedValuesList();
        for (ProdutoOutput p : selected) {
            // Verifica se o produto já está na lista para evitar duplicatas visuais (compara por ID)
            boolean found = false;
            for(int i = 0; i < produtosSelecionadosModel.size(); i++) {
                if(produtosSelecionadosModel.getElementAt(i).id().equals(p.id())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                produtosSelecionadosModel.addElement(p);
            }
        }
    }

    private void removeSelectedProducts() {
        List<ProdutoOutput> selected = produtosSelecionadosList.getSelectedValuesList();
        for (ProdutoOutput p : selected) {
            produtosSelecionadosModel.removeElement(p);
        }
    }

    private void loadKitData(KitPromocionalOutput kit) {
        idField.setText(kit.id());
        nomeField.setText(kit.nome());
        descricaoArea.setText(kit.descricao());
        precoFixoKitField.setText(kit.precoFixoKit().toPlainString());

        // Carrega produtos do kit existente para a lista de selecionados
        produtosSelecionadosModel.clear();
        for (KitItemOutput item : kit.itens()) {
            // Reconstroi um ProdutoOutput a partir do ItemKitOutput para exibir na lista.
            // Para isso, assumimos que ProdutoOutput tem um construtor que aceita ID, nome, etc.
            // O ProdutoOutput retornado aqui terá o nome que veio do KitPromocionalOutput já enriquecido.
            produtosSelecionadosModel.addElement(
                    new ProdutoOutput(item.produtoId(), item.nomeProduto(), null, null, null, null, null, false, null, null)
            );
        }
    }

    private void onSave() {
        if (nomeField.getText().trim().isEmpty() || precoFixoKitField.getText().trim().isEmpty() || produtosSelecionadosModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome, preço fixo e pelo menos um produto são obrigatórios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            new BigDecimal(precoFixoKitField.getText().replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço fixo do kit inválido. Use um número válido.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        saved = true;
        setVisible(false);
        dispose();
    }

    private void onCancel() {
        saved = false;
        setVisible(false);
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public CriarKitPromocionalInput getCriarKitPromocionalInput() {
        if (!saved) return null;

        List<KitItemInput> itens = new ArrayList<>();
        for (int i = 0; i < produtosSelecionadosModel.size(); i++) {
            ProdutoOutput p = produtosSelecionadosModel.getElementAt(i);
            // IMPORTANTE: Aqui você pode adicionar um diálogo para perguntar a quantidade,
            // ou assumir 1 como padrão se não for implementada a seleção de quantidade por item.
            itens.add(new KitItemInput(p.id(), 1)); // Assumindo quantidade 1 para cada item no kit
        }

        return new CriarKitPromocionalInput(
                nomeField.getText().trim(),
                descricaoArea.getText().trim(),
                new BigDecimal(precoFixoKitField.getText().replace(",", ".")),
                itens
        );
    }

    // Para Atualizar Kit Promocional, você precisaria de um input similar
    // AtualizarKitPromocionalInput e um método getAtualizarKitPromocionalInput()
    // Mas para o escopo inicial, vamos focar na criação.
}