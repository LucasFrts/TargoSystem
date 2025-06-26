package com.targosystem.varejo.clientes.infra.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.ParseException;
import javax.swing.text.MaskFormatter;

public class ClienteFrame extends JPanel {

    private JTable clientesTable;
    private DefaultTableModel tableModel;
    private JButton btnNovoCliente;
    private JButton btnEditarCliente;
    private JButton btnExcluirCliente; // Futura implementação
    private JButton btnAtualizarLista;

    // Campos para detalhes do cliente (para formulários de cadastro/edição)
    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtEmail;
    private JFormattedTextField txtCpf;
    private JTextField txtEndereco;
    private JFormattedTextField txtTelefone;

    public ClienteFrame() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Painel Superior: Tabela de Clientes ---
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome Completo", "Email", "CPF", "Telefone", "Endereço"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna as células da tabela não editáveis
            }
        };
        clientesTable = new JTable(tableModel);
        clientesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Apenas um cliente pode ser selecionado
        JScrollPane scrollPane = new JScrollPane(clientesTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Painel Inferior: Botões de Ação ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnNovoCliente = new JButton("Novo Cliente");
        btnEditarCliente = new JButton("Editar Cliente");
        btnExcluirCliente = new JButton("Excluir Cliente"); // Botão para futura implementação
        btnAtualizarLista = new JButton("Atualizar Lista");

        buttonPanel.add(btnNovoCliente);
        buttonPanel.add(btnEditarCliente);
        buttonPanel.add(btnExcluirCliente);
        buttonPanel.add(btnAtualizarLista);

        // Inicialmente, desabilita os botões que dependem de seleção
        btnEditarCliente.setEnabled(false);
        btnExcluirCliente.setEnabled(false);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getClientesTable() {
        return clientesTable;
    }

    public JButton getBtnNovoCliente() {
        return btnNovoCliente;
    }

    public JButton getBtnEditarCliente() {
        return btnEditarCliente;
    }

    public JButton getBtnExcluirCliente() {
        return btnExcluirCliente;
    }

    public JButton getBtnAtualizarLista() {
        return btnAtualizarLista;
    }

    // Método para limpar a tabela
    public void clearTable() {
        tableModel.setRowCount(0);
    }

    /**
     * Exibe um diálogo para cadastro ou edição de cliente.
     * @param isNew true para novo cliente, false para edição.
     * @param clienteId ID do cliente a ser editado (null para novo).
     * @param nome Nome do cliente (pré-preenchimento).
     * @param email Email do cliente (pré-preenchimento).
     * @param cpf CPF do cliente (pré-preenchimento).
     * @param telefone Telefone do cliente (pré-preenchimento).
     * @param endereco Endereço do cliente (pré-preenchimento).
     * @return um array de Strings com os dados do formulário se o usuário confirmar, ou null se cancelar.
     * [id (se edição), nome, email, cpf, telefone, endereco]
     */
    public String[] showClienteDialog(boolean isNew, String clienteId, String nome, String email, String cpf, String telefone, String endereco) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(clienteId);
        txtId.setEditable(false); // ID não é editável

        txtNome = new JTextField(nome, 25);
        txtEmail = new JTextField(email, 25);
        txtEndereco = new JTextField(endereco, 25);

        try {
            MaskFormatter cpfMask = new MaskFormatter("###.###.###-##");
            cpfMask.setPlaceholderCharacter('_');
            txtCpf = new JFormattedTextField(cpfMask);
            txtCpf.setText(cpf);

            MaskFormatter phoneMask = new MaskFormatter("(##)#####-####");
            phoneMask.setPlaceholderCharacter('_');
            txtTelefone = new JFormattedTextField(phoneMask);
            txtTelefone.setText(telefone);

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Erro ao criar máscaras de formato: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            txtCpf = new JFormattedTextField(cpf); // Fallback to plain text field
            txtTelefone = new JFormattedTextField(telefone); // Fallback to plain text field
        }

        int y = 0;
        if (!isNew) {
            gbc.gridx = 0; gbc.gridy = y; panel.add(new JLabel("ID:"), gbc);
            gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtId, gbc);
            y++;
        }

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtNome, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtEmail, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtCpf, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtTelefone, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtEndereco, gbc);
        y++;

        int result = JOptionPane.showConfirmDialog(this, panel,
                (isNew ? "Cadastrar Novo Cliente" : "Editar Cliente"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String idToReturn = isNew ? null : txtId.getText().trim();
            // Remove máscara e caracteres não numéricos do CPF e Telefone antes de retornar
            String cleanedCpf = txtCpf.getText().replaceAll("[^0-9]", "");
            String cleanedTelefone = txtTelefone.getText().replaceAll("[^0-9]", "");

            return new String[]{
                    idToReturn,
                    txtNome.getText().trim(),
                    txtEmail.getText().trim(),
                    cleanedCpf,
                    cleanedTelefone,
                    txtEndereco.getText().trim()
            };
        }
        return null;
    }
}