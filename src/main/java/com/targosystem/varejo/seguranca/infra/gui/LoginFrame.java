package com.targosystem.varejo.seguranca.infra.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginFrame extends JPanel { // Extende JPanel para ser adicionado ao CardLayout

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setLayout(new GridBagLayout()); // Usar GridBagLayout para um layout flexível e centralizado
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Margem interna para os componentes

        // Título da Tela de Login
        JLabel titleLabel = new JLabel("Targo System - Acesso ao Sistema");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80)); // Cor escura para o título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa duas colunas
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // Campo de Usuário
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reseta para uma coluna
        gbc.anchor = GridBagConstraints.EAST; // Alinha o rótulo à direita
        JLabel userLabel = new JLabel("Usuário:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST; // Alinha o campo à esquerda
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        add(usernameField, gbc);

        // Campo de Senha
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passLabel = new JLabel("Senha:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        add(passwordField, gbc);

        // Botão de Login
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Ocupa duas colunas novamente
        gbc.fill = GridBagConstraints.HORIZONTAL; // Preenche horizontalmente
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Entrar");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setBackground(new Color(52, 152, 219)); // Cor azul para o botão
        loginButton.setForeground(Color.WHITE); // Texto branco
        loginButton.setFocusPainted(false); // Remove a borda de foco ao clicar
        add(loginButton, gbc);

        // Configurações de aparência do painel
        setBackground(new Color(236, 240, 241)); // Cor de fundo suave
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Margem externa
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public void addLoginActionListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}