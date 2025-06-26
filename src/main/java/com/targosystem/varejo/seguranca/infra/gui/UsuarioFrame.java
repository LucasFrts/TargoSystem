package com.targosystem.varejo.seguranca.infra.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UsuarioFrame extends JPanel {

    private JTextField usernameField;
    private JTextField nomeCompletoField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox activeCheckBox;
    private JList<String> papeisList;
    private DefaultListModel<String> papeisListModel;
    private JButton criarUsuarioButton;
    private JButton limparCamposButton;

    // Construtor que recebe a lista de papéis disponíveis
    public UsuarioFrame(List<String> papeisDisponiveis) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Cadastro de Usuário");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // Campos de entrada
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Nome de Usuário:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(25);
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        nomeCompletoField = new JTextField(25);
        add(nomeCompletoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        emailField = new JTextField(25);
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(25);
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Ativo:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        activeCheckBox = new JCheckBox();
        activeCheckBox.setSelected(true);
        add(activeCheckBox, gbc);

        // Seleção de Papéis
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.NORTHEAST; // Alinhar o rótulo ao topo da lista
        add(new JLabel("Papéis (Ctrl+Click para multi-seleção):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH; // Preenche verticalmente
        gbc.weighty = 1.0; // Permite que a lista se expanda verticalmente

        papeisListModel = new DefaultListModel<>();
        papeisList = new JList<>(papeisListModel);
        papeisList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Permite múltiplos
        JScrollPane scrollPane = new JScrollPane(papeisList);
        scrollPane.setPreferredSize(new Dimension(200, 100)); // Tamanho preferencial para a lista
        add(scrollPane, gbc);

        gbc.gridwidth = 2; // Botões ocupam 2 colunas
        gbc.weighty = 0; // Reset weighty
        gbc.fill = GridBagConstraints.NONE; // Não preenche
        gbc.anchor = GridBagConstraints.CENTER; // Centraliza

        // Botões de ação
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); // Layout para os botões
        criarUsuarioButton = new JButton("Criar Usuário");
        limparCamposButton = new JButton("Limpar Campos");

        criarUsuarioButton.setFont(new Font("Arial", Font.BOLD, 16));
        limparCamposButton.setFont(new Font("Arial", Font.PLAIN, 16));

        criarUsuarioButton.setBackground(new Color(46, 204, 113)); // Verde para criar
        criarUsuarioButton.setForeground(Color.WHITE);
        limparCamposButton.setBackground(new Color(189, 195, 199)); // Cinza para limpar
        limparCamposButton.setForeground(Color.BLACK);

        buttonPanel.add(criarUsuarioButton);
        buttonPanel.add(limparCamposButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // Preencher a lista de papéis (no construtor ou via método setter)
        setPapeisDisponiveis(papeisDisponiveis);
    }

    public void setPapeisDisponiveis(List<String> papeis) {
        papeisListModel.clear();
        if (papeis != null) {
            for (String papel : papeis) {
                papeisListModel.addElement(papel);
            }
        }
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getNomeCompleto() {
        return nomeCompletoField.getText();
    }

    public String getEmail() {
        return emailField.getText();
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public boolean isActive() {
        return activeCheckBox.isSelected();
    }

    public List<String> getSelectedPapeis() {
        return papeisList.getSelectedValuesList();
    }

    public void addCriarUsuarioListener(ActionListener listener) {
        criarUsuarioButton.addActionListener(listener);
    }

    public void addLimparCamposListener(ActionListener listener) {
        limparCamposButton.addActionListener(listener);
    }

    public void clearFields() {
        usernameField.setText("");
        nomeCompletoField.setText("");
        emailField.setText("");
        passwordField.setText("");
        activeCheckBox.setSelected(true);
        papeisList.clearSelection();
    }
}