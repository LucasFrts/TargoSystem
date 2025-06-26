package com.targosystem.varejo.seguranca.infra.gui;

import com.targosystem.varejo.seguranca.application.SegurancaService;
import com.targosystem.varejo.seguranca.application.input.LoginInput;
import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;
import com.targosystem.varejo.shared.domain.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class SecurityController {
    private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

    private final SegurancaService segurancaService;
    private final LoginFrame loginFrame; // Agora é LoginFrame, não LoginPanel
    private final MainApplicationFrame mainFrame;

    public SecurityController(SegurancaService segurancaService, LoginFrame loginFrame, MainApplicationFrame mainFrame) {
        this.segurancaService = segurancaService;
        this.loginFrame = loginFrame;
        this.mainFrame = mainFrame;
        setupListeners();
    }

    private void setupListeners() {
        loginFrame.addLoginActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = loginFrame.getUsername();
        String password = new String(loginFrame.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame, "Por favor, preencha o usuário e a senha.", "Campos Vazios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LoginInput input = new LoginInput(username, password);
            UsuarioOutput loggedInUser = segurancaService.loginUsuario(input);

            if (loggedInUser != null) {
                logger.info("Login bem-sucedido para o usuário: {}", username);
                mainFrame.onLoginSuccess(loggedInUser); // Notifica o MainApplicationFrame sobre o sucesso
                loginFrame.clearFields(); // Limpa os campos do formulário de login
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Usuário ou senha inválidos.", "Falha no Login", JOptionPane.ERROR_MESSAGE);
                logger.warn("Tentativa de login falhou para o usuário: {}", username);
            }
        } catch (DomainException e) {
            logger.error("Erro de domínio durante o login: {}", e.getMessage());
            JOptionPane.showMessageDialog(loginFrame, "Erro de login: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado durante o login", e);
            JOptionPane.showMessageDialog(loginFrame, "Ocorreu um erro inesperado ao tentar fazer login. Tente novamente.", "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
        }
    }
}