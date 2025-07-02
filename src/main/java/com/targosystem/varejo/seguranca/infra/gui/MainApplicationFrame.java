package com.targosystem.varejo.seguranca.infra.gui;

import com.targosystem.varejo.clientes.application.ClienteService;
import com.targosystem.varejo.clientes.application.usecases.ExcluirClienteUseCase;
import com.targosystem.varejo.clientes.domain.repository.ClienteRepository;
import com.targosystem.varejo.clientes.infra.gui.ClienteController;
import com.targosystem.varejo.clientes.infra.gui.ClienteFrame; // Deve ser ClientePanel ou similar
import com.targosystem.varejo.estoque.application.EstoqueService;
import com.targosystem.varejo.estoque.infra.gui.EstoqueController;
import com.targosystem.varejo.estoque.infra.gui.EstoqueFrame; // Deve ser EstoquePanel ou similar
import com.targosystem.varejo.fornecedores.application.FornecedorService;
import com.targosystem.varejo.fornecedores.infra.gui.FornecedorController;
import com.targosystem.varejo.fornecedores.infra.gui.FornecedorFrame; // Deve ser FornecedorPanel ou similar
import com.targosystem.varejo.produtos.application.ProdutoService;
import com.targosystem.varejo.produtos.infra.gui.ProdutoController;
import com.targosystem.varejo.produtos.infra.gui.ProdutoPanel;
import com.targosystem.varejo.promocoes.application.PromocaoService;
import com.targosystem.varejo.promocoes.infra.gui.PromocaoController;
import com.targosystem.varejo.promocoes.infra.gui.PromocaoFrame; // Deve ser PromocaoPanel ou similar
import com.targosystem.varejo.seguranca.application.SegurancaService;
import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;
import com.targosystem.varejo.vendas.application.VendaService;
import com.targosystem.varejo.vendas.infra.gui.VendaController;
import com.targosystem.varejo.vendas.infra.gui.VendaFrame; // Deve ser VendaPanel ou similar
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

// NOVO IMPORT para UsuarioFrame
import com.targosystem.varejo.seguranca.infra.gui.UsuarioController; // Adicionado
import com.targosystem.varejo.seguranca.infra.gui.UsuarioFrame; // Adicionado, assuming this is a JPanel

public class MainApplicationFrame extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(MainApplicationFrame.class);

    private final SegurancaService segurancaService;
    private final ProdutoService produtoService;
    private final PromocaoService promocaoService;
    private final EstoqueService estoqueService;
    private final ClienteService clienteService;
    private final FornecedorService fornecedorService;
    private final VendaService vendaService;
    private final ClienteRepository clienteRepository;

    private JPanel cardsPanel;
    private CardLayout cardLayout;

    // Referências para os painéis e controllers
    // CORRIGIDO: O nome da variável sugere que é um Frame, mas deve ser um JPanel
    private LoginFrame loginFrame; // <- Esta classe LoginFrame deve estender JPanel
    private SecurityController securityController;

    private DashboardFrame dashboardPanel; // <- Esta classe DashboardFrame deve estender JPanel

    private ProdutoPanel produtoPanel;
    private ProdutoController produtoController;

    private PromocaoFrame promocaoFrame; // <- Esta classe PromocaoFrame deve estender JPanel
    private PromocaoController promocaoController;

    private EstoqueFrame estoqueFrame; // <- Esta classe EstoqueFrame deve estender JPanel
    private EstoqueController estoqueController;

    private ClienteFrame clienteFrame; // <- Esta classe ClienteFrame deve estender JPanel
    private ClienteController clienteController;

    private FornecedorFrame fornecedorFrame; // <- Esta classe FornecedorFrame deve estender JPanel
    private FornecedorController fornecedorController;

    private VendaFrame vendaFrame; // <- Esta classe VendaFrame deve estender JPanel
    private VendaController vendaController;

    // NOVO: Para usuários
    private UsuarioFrame usuarioFrame; // <- Esta classe UsuarioFrame deve estender JPanel
    private UsuarioController usuarioController;
    private final EntityManager entityManager;
    private JMenuBar menuBar;
    private UsuarioOutput loggedInUser;

    public MainApplicationFrame(SegurancaService segurancaService, ProdutoService produtoService,
                                PromocaoService promocaoService, EstoqueService estoqueService,
                                ClienteService clienteService, FornecedorService fornecedorService,
                                VendaService vendaService,  ClienteRepository clienteRepository, EntityManager entityManager) {
        super("Targo System - Sistema de Varejo");
        this.segurancaService = Objects.requireNonNull(segurancaService, "SegurancaService cannot be null");
        this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService cannot be null");
        this.promocaoService = Objects.requireNonNull(promocaoService, "PromocaoService cannot be null");
        this.estoqueService = Objects.requireNonNull(estoqueService, "EstoqueService cannot be null");
        this.clienteService = Objects.requireNonNull(clienteService, "ClienteService cannot be null");
        this.fornecedorService = Objects.requireNonNull(fornecedorService, "FornecedorService cannot be null");
        this.vendaService = Objects.requireNonNull(vendaService, "VendaService cannot be null");
        this.clienteRepository = Objects.requireNonNull(clienteRepository, "ClienteRepository cannot be null");
        this.entityManager = Objects.requireNonNull(entityManager);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        add(cardsPanel, BorderLayout.CENTER);

        // 1. Painel de Login
        // Certifique-se que LoginFrame estenda JPanel
        loginFrame = new LoginFrame();
        securityController = new SecurityController(segurancaService, loginFrame, this);
        cardsPanel.add(loginFrame, "Login");

        // 2. Painel de Dashboard / Boas-Vindas
        // Certifique-se que DashboardFrame estenda JPanel
        dashboardPanel = new DashboardFrame();
        cardsPanel.add(dashboardPanel, "Dashboard");

        // Outros painéis
        // ProdutoPanel já deve estender JPanel
        produtoPanel = new ProdutoPanel();
        produtoController = new ProdutoController(produtoService, produtoPanel);
        cardsPanel.add(produtoPanel, "Produtos");

        // Certifique-se que PromocaoFrame estenda JPanel
        promocaoFrame = new PromocaoFrame();
        promocaoController = new PromocaoController(promocaoService,produtoService, promocaoFrame);
        cardsPanel.add(promocaoFrame, "Promocoes");

        // Certifique-se que EstoqueFrame estenda JPanel
        estoqueFrame = new EstoqueFrame();
        estoqueController = new EstoqueController(estoqueService, estoqueFrame, produtoService);
        cardsPanel.add(estoqueFrame, "Estoque");

        // Certifique-se que ClienteFrame estenda JPanel
        clienteFrame = new ClienteFrame();
        clienteController = new ClienteController(clienteService, clienteFrame, new ExcluirClienteUseCase(clienteRepository, entityManager));
        cardsPanel.add(clienteFrame, "Clientes");

        // Certifique-se que FornecedorFrame estenda JPanel
        fornecedorFrame = new FornecedorFrame();
        fornecedorController = new FornecedorController(fornecedorService, fornecedorFrame);
        cardsPanel.add(fornecedorFrame, "Fornecedores");

        // Certifique-se que VendaFrame estenda JPanel
        vendaFrame = new VendaFrame();
        vendaController = new VendaController(vendaService, vendaFrame);
        cardsPanel.add(vendaFrame, "Vendas");

        // NOVO: Painel de Usuários
        // Certifique-se que UsuarioFrame estenda JPanel
        usuarioFrame = new UsuarioFrame(java.util.Collections.emptyList());
        usuarioController = new UsuarioController(segurancaService, usuarioFrame);
        cardsPanel.add(usuarioFrame, "Usuarios"); // Nome da carta para o CardLayout

        setupMenuBar();
        // Não definir o menu bar aqui, apenas depois do login bem-sucedido
        // setJMenuBar(null); // Essa linha pode ser removida ou mantida para garantir que não haja menu antes do login

        showPanel("Login");
    }

    public void showPanel(String panelName) {
        cardLayout.show(cardsPanel, panelName);
        logger.info("Switched to panel: {}", panelName);

        // Chame os métodos de atualização dos controladores APENAS quando o painel for exibido
        if ("Produtos".equals(panelName) && produtoController != null) {
            produtoController.listarProdutos();
        } else if ("Promocoes".equals(panelName) && promocaoController != null) {
            promocaoController.listarPromocoesAtivas();
        } else if ("Estoque".equals(panelName) && estoqueController != null) {
            estoqueFrame.getTxtAreaResultadoConsulta().setText("");
            estoqueFrame.clearMovimentacaoFields();
        } else if ("Clientes".equals(panelName) && clienteController != null) {
            clienteController.listarTodosClientes();
        } else if ("Fornecedores".equals(panelName) && fornecedorController != null) {
            fornecedorController.listarTodosFornecedores();
        } else if ("Vendas".equals(panelName) && vendaController != null) {
            vendaController.listarTodasVendas();
        } else if ("Usuarios".equals(panelName) && usuarioController != null) { // NOVO: Para usuários
            usuarioController.carregarPapeisDisponiveis(); // Carrega os papéis ao exibir o painel
            usuarioFrame.clearFields(); // Limpa campos ao exibir
        }
    }

    private void setupMenuBar() {
        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Arquivo");
        JMenuItem logoutItem = new JMenuItem("Sair");
        logoutItem.addActionListener(e -> {
            logger.info("User requested logout. Performing logout actions.");
            this.loggedInUser = null;
            setJMenuBar(null); // Remove o menu bar
            showPanel("Login"); // Volta para a tela de login
            JOptionPane.showMessageDialog(this, "Você foi desconectado.", "Logout", JOptionPane.INFORMATION_MESSAGE);
            setTitle("Targo System - Sistema de Varejo");
        });
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);

        JMenu cadastroMenu = new JMenu("Cadastros");
        JMenuItem produtosItem = new JMenuItem("Produtos");
        produtosItem.addActionListener(e -> {
            logger.info("Navigating to Produtos panel.");
            showPanel("Produtos");
        });
        cadastroMenu.add(produtosItem);

        JMenuItem usuariosItem = new JMenuItem("Usuários");
        usuariosItem.addActionListener(e -> {
            logger.info("Navigating to Usuários panel.");
            showPanel("Usuarios");
        });
        cadastroMenu.add(usuariosItem);

        JMenuItem promocoesItem = new JMenuItem("Promoções");
        promocoesItem.addActionListener(e -> {
            logger.info("Navigating to Promoções panel.");
            showPanel("Promocoes");
        });
        cadastroMenu.add(promocoesItem);

        JMenuItem estoqueItem = new JMenuItem("Estoque");
        estoqueItem.addActionListener(e -> {
            logger.info("Navigating to Estoque panel.");
            showPanel("Estoque");
        });
        cadastroMenu.add(estoqueItem);

        JMenuItem clientesItem = new JMenuItem("Clientes");
        clientesItem.addActionListener(e -> {
            logger.info("Navigating to Clientes panel.");
            showPanel("Clientes");
        });
        cadastroMenu.add(clientesItem);

        JMenuItem fornecedoresItem = new JMenuItem("Fornecedores");
        fornecedoresItem.addActionListener(e -> {
            logger.info("Navigating to Fornecedores panel.");
            showPanel("Fornecedores");
        });
        cadastroMenu.add(fornecedoresItem);

        JMenuItem vendasItem = new JMenuItem("Vendas");
        vendasItem.addActionListener(e -> {
            logger.info("Navigating to Vendas panel.");
            showPanel("Vendas");
        });
        cadastroMenu.add(vendasItem);

        menuBar.add(cadastroMenu);

        logger.info("MenuBar configured successfully.");
    }

    public void onLoginSuccess(UsuarioOutput loggedInUser) {
        this.loggedInUser = loggedInUser;
        logger.info("Login successful for user: {}. Displaying dashboard and menu.", loggedInUser.username());

        setJMenuBar(menuBar); // Define o menu bar APÓS o login bem-sucedido
        showPanel("Dashboard"); // Vai para o Dashboard após o login

        setTitle("Targo System - Logado como: " + loggedInUser.nomeCompleto());
    }

    // Getters para os Services (se necessário em algum ponto)
    public SegurancaService getSegurancaService() { return segurancaService; }
    public ProdutoService getProdutoService() { return produtoService; }
    public PromocaoService getPromocaoService() { return promocaoService; }
    public EstoqueService getEstoqueService() { return estoqueService; }
    public ClienteService getClienteService() { return clienteService; }
    public FornecedorService getFornecedorService() { return fornecedorService; }
    public VendaService getVendaService() { return vendaService; }
}