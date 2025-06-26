package com.targosystem.varejo.seguranca.infra.gui;

import com.targosystem.varejo.seguranca.application.SegurancaService;
import com.targosystem.varejo.seguranca.application.input.LoginInput;
import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;
import com.targosystem.varejo.shared.domain.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final SegurancaService segurancaService;
    private final LoginFrame loginPanel;
    private final MainApplicationFrame mainFrame; // Referência à MainApplicationFrame

    public LoginController(SegurancaService segurancaService, LoginFrame loginPanel, MainApplicationFrame mainFrame) {
        this.segurancaService = segurancaService;
        this.loginPanel = loginPanel;
        this.mainFrame = mainFrame;
        setupListeners();
    }

    private void setupListeners() {
        loginPanel.addLoginActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = loginPanel.getUsername();
        String password = new String(loginPanel.getPassword()); // Cuidado com segurança em produção

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginPanel, "Usuário e senha não podem ser vazios.", "Erro de Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LoginInput input = new LoginInput(username, password);
            UsuarioOutput loggedInUser = segurancaService.loginUsuario(input);
            if (loggedInUser != null) {
                logger.info("Login bem-sucedido para o usuário: {}", username);
                mainFrame.onLoginSuccess(loggedInUser); // Chama o método na MainApplicationFrame
                loginPanel.clearFields();
            } else {
                JOptionPane.showMessageDialog(loginPanel, "Credenciais inválidas.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
                logger.warn("Tentativa de login falhou para o usuário: {}", username);
            }
        } catch (DomainException e) {
            logger.error("Erro de domínio no login: {}", e.getMessage());
            JOptionPane.showMessageDialog(loginPanel, "Erro de login: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado durante o login", e);
            JOptionPane.showMessageDialog(loginPanel, "Erro inesperado durante o login.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}