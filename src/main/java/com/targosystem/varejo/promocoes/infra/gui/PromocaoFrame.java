package com.targosystem.varejo.promocoes.infra.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PromocaoFrame extends JPanel {

    private JTable promocoesTable;
    private DefaultTableModel tableModel;
    private JButton btnNovaPromocao;
    private JButton btnEditarPromocao;
    private JButton btnListarPromocoes; // Para testes, listar promoções ativas

    public PromocaoFrame() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabela de Promoções
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Tipo", "Valor/Percentual", "Ativa", "Início", "Fim"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna as células não editáveis
            }
        };
        promocoesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(promocoesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnNovaPromocao = new JButton("Nova Promoção");
        btnEditarPromocao = new JButton("Editar Promoção");
        btnListarPromocoes = new JButton("Listar Promoções Ativas"); // Botão para chamar a listagem

        buttonPanel.add(btnNovaPromocao);
        buttonPanel.add(btnEditarPromocao);
        buttonPanel.add(btnListarPromocoes);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
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

    public JTable getPromocoesTable() {
        return promocoesTable;
    }

    // Método para limpar a tabela
    public void clearTable() {
        tableModel.setRowCount(0);
    }
}