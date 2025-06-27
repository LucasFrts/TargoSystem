package com.targosystem.varejo.seguranca.infra.gui;

import com.targosystem.varejo.clientes.application.ClienteService;
import com.targosystem.varejo.clientes.infra.gui.ClienteController;
import com.targosystem.varejo.clientes.infra.gui.ClienteFrame;
import com.targosystem.varejo.estoque.application.EstoqueService;
import com.targosystem.varejo.estoque.infra.gui.EstoqueController;
import com.targosystem.varejo.estoque.infra.gui.EstoqueFrame;
import com.targosystem.varejo.fornecedores.application.FornecedorService;
import com.targosystem.varejo.fornecedores.infra.gui.FornecedorController;
import com.targosystem.varejo.fornecedores.infra.gui.FornecedorFrame;
import com.targosystem.varejo.produtos.application.ProdutoService;
import com.targosystem.varejo.produtos.infra.gui.ProdutoController;
import com.targosystem.varejo.produtos.infra.gui.ProdutoPanel;
import com.targosystem.varejo.promocoes.application.PromocaoService;
import com.targosystem.varejo.promocoes.infra.gui.PromocaoController;
import com.targosystem.varejo.promocoes.infra.gui.PromocaoFrame;
import com.targosystem.varejo.seguranca.application.SegurancaService;
import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;
import com.targosystem.varejo.vendas.application.VendaService;
import com.targosystem.varejo.vendas.infra.gui.VendaController;
import com.targosystem.varejo.vendas.infra.gui.VendaFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class MainApplicationFrame extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(MainApplicationFrame.class);

    private final SegurancaService segurancaService;
    private final ProdutoService produtoService;
    private final PromocaoService promocaoService;
    private final EstoqueService estoqueService;
    private final ClienteService clienteService;
    private final FornecedorService fornecedorService;
    private final VendaService vendaService;

    private JPanel cardsPanel;
    private CardLayout cardLayout;

    // Referências para os painéis e controllers
    private LoginFrame loginFrame;
    private SecurityController securityController;

    private DashboardFrame dashboardPanel;

    private ProdutoPanel produtoPanel;
    private ProdutoController produtoController;

    private PromocaoFrame promocaoFrame;
    private PromocaoController promocaoController;

    private EstoqueFrame estoqueFrame;
    private EstoqueController estoqueController;

    private ClienteFrame clienteFrame;
    private ClienteController clienteController;

    private FornecedorFrame fornecedorFrame;
    private FornecedorController fornecedorController;

    private VendaFrame vendaFrame;
    private VendaController vendaController;

    // NOVO: Para usuários
    private UsuarioFrame usuarioFrame;
    private UsuarioController usuarioController;

    private JMenuBar menuBar;
    private UsuarioOutput loggedInUser;

    public MainApplicationFrame(SegurancaService segurancaService, ProdutoService produtoService,
                                PromocaoService promocaoService, EstoqueService estoqueService,
                                ClienteService clienteService, FornecedorService fornecedorService,
                                VendaService vendaService) {
        super("Targo System - Sistema de Varejo");
        this.segurancaService = Objects.requireNonNull(segurancaService, "SegurancaService cannot be null");
        this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService cannot be null");
        this.promocaoService = Objects.requireNonNull(promocaoService, "PromocaoService cannot be null");
        this.estoqueService = Objects.requireNonNull(estoqueService, "EstoqueService cannot be null");
        this.clienteService = Objects.requireNonNull(clienteService, "ClienteService cannot be null");
        this.fornecedorService = Objects.requireNonNull(fornecedorService, "FornecedorService cannot be null");
        this.vendaService = Objects.requireNonNull(vendaService, "VendaService cannot be null");

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
        loginFrame = new LoginFrame();
        securityController = new SecurityController(segurancaService, loginFrame, this);
        cardsPanel.add(loginFrame, "Login");

        // 2. Painel de Dashboard / Boas-Vindas
        dashboardPanel = new DashboardFrame();
        cardsPanel.add(dashboardPanel, "Dashboard");

        // Outros painéis
        produtoPanel = new ProdutoPanel();
        produtoController = new ProdutoController(produtoService, produtoPanel);
        cardsPanel.add(produtoPanel, "Produtos");

        promocaoFrame = new PromocaoFrame();
        promocaoController = new PromocaoController(promocaoService,produtoService, promocaoFrame);
        cardsPanel.add(promocaoFrame, "Promocoes");

        estoqueFrame = new EstoqueFrame();
        estoqueController = new EstoqueController(estoqueService, estoqueFrame);
        cardsPanel.add(estoqueFrame, "Estoque");

        clienteFrame = new ClienteFrame();
        clienteController = new ClienteController(clienteService, clienteFrame);
        cardsPanel.add(clienteFrame, "Clientes");

        fornecedorFrame = new FornecedorFrame();
        fornecedorController = new FornecedorController(fornecedorService, fornecedorFrame);
        cardsPanel.add(fornecedorFrame, "Fornecedores");

        vendaFrame = new VendaFrame();
        vendaController = new VendaController(vendaService, vendaFrame);
        cardsPanel.add(vendaFrame, "Vendas");

        // NOVO: Painel de Usuários
        // Precisamos passar uma lista inicial de papéis para o UsuarioFrame.
        // Como o MainApplicationFrame não busca papéis, podemos passar uma lista vazia aqui,
        // e o controller de usuário vai carregá-los dinamicamente.
        usuarioFrame = new UsuarioFrame(java.util.Collections.emptyList());
        usuarioController = new UsuarioController(segurancaService, usuarioFrame);
        cardsPanel.add(usuarioFrame, "Usuarios"); // Nome da carta para o CardLayout

        setupMenuBar();
        setJMenuBar(null);

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
            setJMenuBar(null);
            showPanel("Login");
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

        // ATUALIZADO: Item de menu para Usuários
        JMenuItem usuariosItem = new JMenuItem("Usuários");
        usuariosItem.addActionListener(e -> {
            logger.info("Navigating to Usuários panel.");
            showPanel("Usuarios"); // Agora navega para o painel real
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

        setJMenuBar(menuBar);
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