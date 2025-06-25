// src/main/java/com/targosystem/varejo/seguranca/infra/gui/MainApplicationFrame.java
package com.targosystem.varejo.seguranca.infra.gui;

import com.targosystem.varejo.produtos.application.ProdutoService;
import com.targosystem.varejo.produtos.infra.gui.ProdutoPanel;
import com.targosystem.varejo.produtos.infra.gui.ProdutoController; // <--- Importação atualizada
import com.targosystem.varejo.seguranca.application.SegurancaService;
import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApplicationFrame extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(MainApplicationFrame.class);

    private final SegurancaService segurancaService;
    private final ProdutoService produtoService;

    private JPanel cardsPanel;
    private CardLayout cardLayout;

    private ProdutoPanel produtoPanel;
    private ProdutoController produtoController; // <--- Referência atualizada

    private JMenuBar menuBar;
    private UsuarioOutput loggedInUser;

    public MainApplicationFrame(SegurancaService segurancaService, ProdutoService produtoService) {
        super("Targo System - Sistema de Varejo");
        this.segurancaService = Objects.requireNonNull(segurancaService, "SegurancaService cannot be null");
        this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService cannot be null");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        add(cardsPanel, BorderLayout.CENTER);

        // 1. Painel de Login (placeholder)
        JPanel tempLoginPanel = new JPanel(new GridBagLayout());
        JLabel loginPlaceholder = new JLabel("Tela de Login (Implementar LoginPanel aqui)");
        loginPlaceholder.setFont(new Font("Arial", Font.ITALIC, 18));
        tempLoginPanel.add(loginPlaceholder);
        cardsPanel.add(tempLoginPanel, "Login");

        // 2. Painel de Produtos
        produtoPanel = new ProdutoPanel();
        // Inicializa o controller com a nova nomenclatura:
        produtoController = new ProdutoController(produtoService, produtoPanel); // <--- Instanciação atualizada
        cardsPanel.add(produtoPanel, "Produtos");

        setupMenuBar();
        showPanel("Login");
    }

    public void showPanel(String panelName) {
        cardLayout.show(cardsPanel, panelName);
        logger.info("Switched to panel: {}", panelName);
        // Se o painel for o de produtos, force uma atualização dos dados
        if ("Produtos".equals(panelName) && produtoController != null) {
            produtoController.listarProdutos(); // Garante que a tabela seja atualizada ao exibir o painel
        }
    }

    private void setupMenuBar() {
        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Arquivo");
        JMenuItem logoutItem = new JMenuItem("Sair");
        logoutItem.addActionListener(e -> {
            logger.info("User requested logout. Performing logout actions.");
            this.loggedInUser = null;
            setJMenuBar(null);
            showPanel("Login");
            JOptionPane.showMessageDialog(this, "Você foi desconectado.", "Logout", JOptionPane.INFORMATION_MESSAGE);
        });
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);

        JMenu cadastroMenu = new JMenu("Cadastros");
        JMenuItem produtosItem = new JMenuItem("Produtos");
        produtosItem.addActionListener(e -> {
            logger.info("Navigating to Produtos panel.");
            showPanel("Produtos"); // showPanel já vai chamar listarProdutos()
        });
        cadastroMenu.add(produtosItem);

        JMenuItem usuariosItem = new JMenuItem("Usuários");
        usuariosItem.addActionListener(e -> {
            logger.info("Navigating to Usuários panel (future implementation).");
            JOptionPane.showMessageDialog(this, "Painel de Usuários em desenvolvimento!", "Aguarde", JOptionPane.INFORMATION_MESSAGE);
        });
        cadastroMenu.add(usuariosItem);

        menuBar.add(cadastroMenu);

        logger.info("MenuBar configured successfully.");
    }

    public void onLoginSuccess(UsuarioOutput loggedInUser) {
        this.loggedInUser = loggedInUser;
        logger.info("Login successful for user: {}. Displaying dashboard and menu.", loggedInUser.username());

        setJMenuBar(menuBar);
        showPanel("Produtos");

        setTitle("Targo System - Logado como: " + loggedInUser.nomeCompleto());
    }

    public SegurancaService getSegurancaService() {
        return segurancaService;
    }

    public ProdutoService getProdutoService() {
        return produtoService;
    }
}