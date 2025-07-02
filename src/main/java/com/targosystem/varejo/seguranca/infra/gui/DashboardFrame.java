package com.targosystem.varejo.seguranca.infra.gui;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JPanel {

    public DashboardFrame() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));

        JLabel welcomeLabel = new JLabel("Bem-vindo ao Targo System!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(70, 130, 180));

        JLabel instructionLabel = new JLabel("Use o menu superior para navegar entre os módulos.");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        instructionLabel.setForeground(new Color(105, 105, 105));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 10, 0);
        add(welcomeLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(instructionLabel, gbc);
    }
}