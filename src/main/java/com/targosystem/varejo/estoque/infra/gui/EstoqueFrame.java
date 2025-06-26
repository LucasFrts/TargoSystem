package com.targosystem.varejo.estoque.infra.gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class EstoqueFrame extends JPanel {

    // Componentes para consulta
    private JTextField txtProdutoIdConsulta;
    private JButton btnConsultarEstoque;
    private JTextArea txtAreaResultadoConsulta;

    // Componentes para registro de movimentação
    private JTextField txtProdutoIdMovimentacao;
    private JTextField txtQuantidadeMovimentacao;
    private JComboBox<String> cmbTipoMovimentacao; // "ENTRADA" ou "SAIDA"
    private JTextField txtLocalOrigem;
    private JTextField txtLocalDestino;
    private JButton btnRegistrarMovimentacao;

    public EstoqueFrame() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Painel Superior: Consulta de Estoque ---
        JPanel consultaPanel = new JPanel(new GridBagLayout());
        consultaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Consulta de Estoque", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblProdutoIdConsulta = new JLabel("ID do Produto:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        consultaPanel.add(lblProdutoIdConsulta, gbc);

        txtProdutoIdConsulta = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        consultaPanel.add(txtProdutoIdConsulta, gbc);

        btnConsultarEstoque = new JButton("Consultar");
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        consultaPanel.add(btnConsultarEstoque, gbc);

        txtAreaResultadoConsulta = new JTextArea(5, 40);
        txtAreaResultadoConsulta.setEditable(false);
        JScrollPane scrollConsulta = new JScrollPane(txtAreaResultadoConsulta);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0; // Permite que a área de texto se expanda verticalmente
        gbc.fill = GridBagConstraints.BOTH;
        consultaPanel.add(scrollConsulta, gbc);

        add(consultaPanel, BorderLayout.NORTH);

        // --- Painel Central: Registro de Movimentação ---
        JPanel movimentacaoPanel = new JPanel(new GridBagLayout());
        movimentacaoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Registrar Movimentação de Estoque", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));
        GridBagConstraints gbcMov = new GridBagConstraints();
        gbcMov.insets = new Insets(5, 5, 5, 5);
        gbcMov.fill = GridBagConstraints.HORIZONTAL;

        // Linha 1: ID do Produto
        JLabel lblProdutoIdMov = new JLabel("ID do Produto:");
        gbcMov.gridx = 0; gbcMov.gridy = 0; gbcMov.anchor = GridBagConstraints.WEST; movimentacaoPanel.add(lblProdutoIdMov, gbcMov);
        txtProdutoIdMovimentacao = new JTextField(20);
        gbcMov.gridx = 1; gbcMov.gridy = 0; gbcMov.gridwidth = 2; gbcMov.weightx = 1.0; movimentacaoPanel.add(txtProdutoIdMovimentacao, gbcMov);

        // Linha 2: Quantidade
        JLabel lblQuantidadeMov = new JLabel("Quantidade:");
        gbcMov.gridx = 0; gbcMov.gridy = 1; gbcMov.gridwidth = 1; movimentacaoPanel.add(lblQuantidadeMov, gbcMov);
        txtQuantidadeMovimentacao = new JTextField(10);
        gbcMov.gridx = 1; gbcMov.gridy = 1; gbcMov.weightx = 0.5; movimentacaoPanel.add(txtQuantidadeMovimentacao, gbcMov);

        // Linha 2 (cont.): Tipo de Movimentação
        JLabel lblTipoMov = new JLabel("Tipo:");
        gbcMov.gridx = 2; gbcMov.gridy = 1; gbcMov.weightx = 0; movimentacaoPanel.add(lblTipoMov, gbcMov);
        cmbTipoMovimentacao = new JComboBox<>(new String[]{"ENTRADA", "SAIDA", "TRANSFERENCIA"});
        gbcMov.gridx = 3; gbcMov.gridy = 1; gbcMov.weightx = 0.5; movimentacaoPanel.add(cmbTipoMovimentacao, gbcMov);


        // Linha 3: Local Origem
        JLabel lblLocalOrigem = new JLabel("Local Origem:");
        gbcMov.gridx = 0; gbcMov.gridy = 2; gbcMov.gridwidth = 1; movimentacaoPanel.add(lblLocalOrigem, gbcMov);
        txtLocalOrigem = new JTextField(15);
        gbcMov.gridx = 1; gbcMov.gridy = 2; gbcMov.gridwidth = 3; gbcMov.weightx = 1.0; movimentacaoPanel.add(txtLocalOrigem, gbcMov);

        // Linha 4: Local Destino
        JLabel lblLocalDestino = new JLabel("Local Destino:");
        gbcMov.gridx = 0; gbcMov.gridy = 3; gbcMov.gridwidth = 1; movimentacaoPanel.add(lblLocalDestino, gbcMov);
        txtLocalDestino = new JTextField(15);
        gbcMov.gridx = 1; gbcMov.gridy = 3; gbcMov.gridwidth = 3; gbcMov.weightx = 1.0; movimentacaoPanel.add(txtLocalDestino, gbcMov);

        // Habilitar/Desabilitar Local Destino com base no Tipo de Movimentação
        cmbTipoMovimentacao.addActionListener(e -> {
            boolean isTransferencia = "TRANSFERENCIA".equals(cmbTipoMovimentacao.getSelectedItem());
            txtLocalOrigem.setEnabled(isTransferencia); // Origem só relevante para transferência
            txtLocalDestino.setEnabled(isTransferencia);
            if (!isTransferencia) {
                txtLocalOrigem.setText(""); // Limpa se não for transferência
                txtLocalDestino.setText(""); // Limpa se não for transferência
            }
        });
        // Estado inicial
        txtLocalOrigem.setEnabled(false); // Desabilitado por padrão
        txtLocalDestino.setEnabled(false); // Desabilitado por padrão


        // Linha 5: Botão Registrar
        btnRegistrarMovimentacao = new JButton("Registrar Movimentação");
        gbcMov.gridx = 0; gbcMov.gridy = 4; gbcMov.gridwidth = 4; gbcMov.fill = GridBagConstraints.NONE;
        gbcMov.anchor = GridBagConstraints.CENTER;
        movimentacaoPanel.add(btnRegistrarMovimentacao, gbcMov);

        add(movimentacaoPanel, BorderLayout.CENTER);
    }

    // Getters para os componentes da GUI
    public JTextField getTxtProdutoIdConsulta() { return txtProdutoIdConsulta; }
    public JButton getBtnConsultarEstoque() { return btnConsultarEstoque; }
    public JTextArea getTxtAreaResultadoConsulta() { return txtAreaResultadoConsulta; }

    public JTextField getTxtProdutoIdMovimentacao() { return txtProdutoIdMovimentacao; }
    public JTextField getTxtQuantidadeMovimentacao() { return txtQuantidadeMovimentacao; }
    public JComboBox<String> getCmbTipoMovimentacao() { return cmbTipoMovimentacao; }
    public JTextField getTxtLocalOrigem() { return txtLocalOrigem; }
    public JTextField getTxtLocalDestino() { return txtLocalDestino; }
    public JButton getBtnRegistrarMovimentacao() { return btnRegistrarMovimentacao; }

    // Método para limpar os campos de movimentação
    public void clearMovimentacaoFields() {
        txtProdutoIdMovimentacao.setText("");
        txtQuantidadeMovimentacao.setText("");
        cmbTipoMovimentacao.setSelectedIndex(0); // Volta para ENTRADA
        txtLocalOrigem.setText("");
        txtLocalDestino.setText("");
    }
}