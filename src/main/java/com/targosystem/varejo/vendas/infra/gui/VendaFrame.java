package com.targosystem.varejo.vendas.infra.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.Vector;

public class VendaFrame extends JPanel {

    private JTable vendasTable;
    private DefaultTableModel vendasTableModel;
    private JButton btnAtualizarVendas;
    private JButton btnNovaVenda;
    private JButton btnCancelarVenda;
    private JButton btnAplicarDesconto;

    public VendaFrame() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Tabela de Vendas ---
        vendasTableModel = new DefaultTableModel(new Object[]{"ID", "Cliente ID", "Data Venda", "Total Bruto", "Desconto", "Total Líquido", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna todas as células não editáveis
            }
        };
        vendasTable = new JTable(vendasTableModel);
        vendasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(vendasTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Painel de Botões ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnAtualizarVendas = new JButton("Atualizar Vendas");
        btnNovaVenda = new JButton("Nova Venda");
        btnCancelarVenda = new JButton("Cancelar Venda");
        btnAplicarDesconto = new JButton("Aplicar Desconto");

        buttonPanel.add(btnAtualizarVendas);
        buttonPanel.add(btnNovaVenda);
        buttonPanel.add(btnCancelarVenda);
        buttonPanel.add(btnAplicarDesconto);

        add(buttonPanel, BorderLayout.SOUTH);

        // Desabilita botões que dependem de seleção inicialmente
        btnCancelarVenda.setEnabled(false);
        btnAplicarDesconto.setEnabled(false);
    }

    // --- Getters para componentes ---
    public DefaultTableModel getVendasTableModel() {
        return vendasTableModel;
    }

    public JTable getVendasTable() {
        return vendasTable;
    }

    public JButton getBtnAtualizarVendas() {
        return btnAtualizarVendas;
    }

    public JButton getBtnNovaVenda() {
        return btnNovaVenda;
    }

    public JButton getBtnCancelarVenda() {
        return btnCancelarVenda;
    }

    public JButton getBtnAplicarDesconto() {
        return btnAplicarDesconto;
    }

    public void clearVendasTable() {
        vendasTableModel.setRowCount(0);
    }


    /**
     * Exibe um diálogo para realizar uma nova venda.
     * Para simplificar, este diálogo vai coletar dados para UMA venda com UM item e um DESCONTO.
     * Em um sistema real, este diálogo seria mais complexo para múltiplos itens.
     * @return array de String com os dados da venda ou null se cancelar.
     * [idCliente, idProduto, quantidade, valorDesconto (opcional)]
     */
    public String[] showRealizarVendaDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField txtIdCliente = new JTextField(20);
        JTextField txtIdProduto = new JTextField(20);
        JTextField txtQuantidade = new JTextField(5);
        JTextField txtValorDesconto = new JTextField("0.00", 10);

        panel.add(new JLabel("ID do Cliente:"));
        panel.add(txtIdCliente);
        panel.add(new JLabel("ID do Produto (do item):"));
        panel.add(txtIdProduto);
        panel.add(new JLabel("Quantidade:"));
        panel.add(txtQuantidade);
        panel.add(new JLabel("Valor Desconto (Venda):"));
        panel.add(txtValorDesconto);


        int result = JOptionPane.showConfirmDialog(this, panel,
                "Realizar Nova Venda", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return new String[]{
                    txtIdCliente.getText().trim(),
                    txtIdProduto.getText().trim(),
                    txtQuantidade.getText().trim(),
                    txtValorDesconto.getText().trim()
            };
        }
        return null;
    }

    /**
     * Exibe um diálogo para confirmar e aplicar um desconto a uma venda.
     * @param vendaId ID da venda a ser descontada.
     * @param totalBruto Total bruto da venda para referência.
     * @return array de String com o valor do desconto ou null se cancelar.
     * [valorDesconto]
     */
    public String[] showAplicarDescontoDialog(String vendaId, String totalBruto) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField lblVendaId = new JTextField(vendaId);
        lblVendaId.setEditable(false);
        JTextField lblTotalBruto = new JTextField(totalBruto);
        lblTotalBruto.setEditable(false);
        JTextField txtValorDesconto = new JTextField(10);

        panel.add(new JLabel("ID da Venda:"));
        panel.add(lblVendaId);
        panel.add(new JLabel("Total Bruto da Venda:"));
        panel.add(lblTotalBruto);
        panel.add(new JLabel("Valor do Desconto (ex: 10.50):"));
        panel.add(txtValorDesconto);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Aplicar Desconto na Venda", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return new String[]{txtValorDesconto.getText().trim()};
        }
        return null;
    }
}