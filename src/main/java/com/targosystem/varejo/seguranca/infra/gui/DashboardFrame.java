package com.targosystem.varejo.seguranca.infra.gui;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JPanel {

    public DashboardFrame() {
        setLayout(new GridBagLayout()); // Para centralizar o conteúdo
        setBackground(new Color(245, 245, 245)); // Um cinza claro para o fundo

        JLabel welcomeLabel = new JLabel("Bem-vindo ao Targo System!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(70, 130, 180)); // Azul aço

        JLabel instructionLabel = new JLabel("Use o menu superior para navegar entre os módulos.");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        instructionLabel.setForeground(new Color(105, 105, 105)); // Cinza escuro

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 10, 0); // Margem inferior
        add(welcomeLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0); // Sem margem inferior
        add(instructionLabel, gbc);
    }
}