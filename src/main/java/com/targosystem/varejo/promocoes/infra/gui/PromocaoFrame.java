package com.targosystem.varejo.promocoes.infra.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PromocaoFrame extends JPanel {

    private JTabbedPane tabbedPane;

    private JTable promocoesTable;
    private DefaultTableModel promocoesTableModel;
    private JButton btnNovaPromocao;
    private JButton btnEditarPromocao;
    private JButton btnListarPromocoes;
    private JButton btnExcluirPromocao;

    // Componentes da aba de Kits Promocionais
    private JTable kitsTable;
    private DefaultTableModel kitsTableModel;
    private JButton btnNovoKit;
    private JButton btnEditarKit;
    private JButton btnListarKits;
    private JButton btnExcluirKit;

    public PromocaoFrame() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabbedPane = new JTabbedPane();

        // --- Aba de Promoções ---
        JPanel promocoesPanel = new JPanel(new BorderLayout(10, 10));
        promocoesTableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Tipo", "Valor", "Ativa", "Início", "Fim"}, 0) { // Adicionado "Kit ID"
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        promocoesTable = new JTable(promocoesTableModel);
        JScrollPane promocoesScrollPane = new JScrollPane(promocoesTable);
        promocoesPanel.add(promocoesScrollPane, BorderLayout.CENTER); // Tabela ocupa o centro

        JPanel promocoesButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnNovaPromocao = new JButton("Nova Promoção");
        btnEditarPromocao = new JButton("Editar Promoção");
        btnListarPromocoes = new JButton("Listar Ativas");
        btnExcluirPromocao = new JButton("Excluir Promoção");
        promocoesButtonPanel.add(btnNovaPromocao);
        promocoesButtonPanel.add(btnEditarPromocao);
        promocoesButtonPanel.add(btnListarPromocoes);
        promocoesButtonPanel.add(btnExcluirPromocao);
        promocoesPanel.add(promocoesButtonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Promoções", promocoesPanel);

        // --- Aba de Kits Promocionais (sem alterações significativas aqui) ---
        JPanel kitsPanel = new JPanel(new BorderLayout(10, 10));
        kitsTableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Descrição", "Preço Fixo", "Itens (IDs)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        kitsTable = new JTable(kitsTableModel);
        JScrollPane kitsScrollPane = new JScrollPane(kitsTable);
        kitsPanel.add(kitsScrollPane, BorderLayout.CENTER);

        JPanel kitsButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnNovoKit = new JButton("Novo Kit");
        btnEditarKit = new JButton("Editar Kit");
        btnListarKits = new JButton("Listar Todos Kits");
        btnExcluirKit = new JButton("Excluir Kit");
        kitsButtonPanel.add(btnNovoKit);
        kitsButtonPanel.add(btnEditarKit);
        kitsButtonPanel.add(btnListarKits);
        kitsButtonPanel.add(btnExcluirKit);
        kitsPanel.add(kitsButtonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Kits Promocionais", kitsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Configuração inicial de botões de edição/exclusão
        btnEditarPromocao.setEnabled(false);
        btnExcluirPromocao.setEnabled(false);
        btnEditarKit.setEnabled(false);
        btnExcluirKit.setEnabled(false);

        // Adicionar Listeners de Seleção de Tabela
        promocoesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = promocoesTable.getSelectedRow() != -1;
                btnEditarPromocao.setEnabled(hasSelection);
                btnExcluirPromocao.setEnabled(hasSelection);
            }
        });

        kitsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = kitsTable.getSelectedRow() != -1;
                btnEditarKit.setEnabled(hasSelection);
                btnExcluirKit.setEnabled(hasSelection);
            }
        });

    }

    // --- Métodos de acesso para Promoções (mantidos) ---
    public DefaultTableModel getPromocoesTableModel() {
        return promocoesTableModel;
    }

    public JButton getBtnNovaPromocao() {
        return btnNovaPromocao;
    }

    public JButton getBtnEditarPromocao() {
        return btnEditarPromocao;
    }

    public JButton getBtnListarPromocoes() {
        return btnListarPromocoes;
    }

    public JButton getBtnExcluirPromocao() {
        return btnExcluirPromocao;
    }

    public JTable getPromocoesTable() {
        return promocoesTable;
    }

    public void clearPromocoesTable() {
        promocoesTableModel.setRowCount(0);
    }

    // --- Métodos de acesso para Kits Promocionais (mantidos) ---
    public DefaultTableModel getKitsTableModel() {
        return kitsTableModel;
    }

    public JButton getBtnNovoKit() {
        return btnNovoKit;
    }

    public JButton getBtnEditarKit() {
        return btnEditarKit;
    }

    public JButton getBtnListarKits() {
        return btnListarKits;
    }

    public JButton getBtnExcluirKit() {
        return btnExcluirKit;
    }

    public JTable getKitsTable() {
        return kitsTable;
    }

    public void clearKitsTable() {
        kitsTableModel.setRowCount(0);
    }
}