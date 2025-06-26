package com.targosystem;

import com.targosystem.varejo.clientes.application.query.ObterClientePorIdQuery;
import com.targosystem.varejo.estoque.application.queries.ConsultarEstoquePorProdutoIdQuery;
import com.targosystem.varejo.estoque.application.usecases.RegistrarEntradaEstoqueUseCase;
import com.targosystem.varejo.estoque.application.usecases.RegistrarSaidaEstoqueUseCase;
import com.targosystem.varejo.fornecedores.application.query.ConsultarFornecedorPorCnpjQuery;
import com.targosystem.varejo.fornecedores.application.query.ConsultarFornecedorPorIdQuery;
import com.targosystem.varejo.produtos.application.ProdutoService;
import com.targosystem.varejo.produtos.application.query.ConsultarPrecoProdutoQuery;
import com.targosystem.varejo.produtos.application.query.ListarTodosProdutosQuery;
import com.targosystem.varejo.produtos.application.query.ObterProdutoPorIdQuery;
import com.targosystem.varejo.produtos.application.usecases.AtualizarProdutoUseCase;
import com.targosystem.varejo.produtos.application.usecases.CadastrarProdutoUseCase;
import com.targosystem.varejo.produtos.domain.events.ProdutoCadastradoEvent;
import com.targosystem.varejo.produtos.domain.listeners.ProdutoCadastradoLoggerListener;
import com.targosystem.varejo.produtos.domain.repository.CategoriaRepository;
import com.targosystem.varejo.produtos.domain.repository.ProdutoRepository;
import com.targosystem.varejo.produtos.domain.service.ClassificadorProduto;
import com.targosystem.varejo.produtos.infra.persistence.CategoriaDao;
import com.targosystem.varejo.produtos.infra.persistence.ProdutoDao;

import com.targosystem.varejo.promocoes.application.query.ObterKitPromocionalPorIdQuery;
import com.targosystem.varejo.promocoes.application.query.ObterPromocaoPorIdQuery;
import com.targosystem.varejo.promocoes.application.usecases.AtualizarPromocaoUseCase;
import com.targosystem.varejo.promocoes.application.usecases.CriarKitPromocionalUseCase;
import com.targosystem.varejo.promocoes.domain.repository.KitPromocionalRepository;
import com.targosystem.varejo.promocoes.infra.persistence.KitPromocionalDao;
import com.targosystem.varejo.seguranca.application.SegurancaService;
import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;
import com.targosystem.varejo.seguranca.application.query.ListarPapeisQuery;
import com.targosystem.varejo.seguranca.application.query.ListarUsuariosQuery;
import com.targosystem.varejo.seguranca.application.query.ObterUsuarioPorIdQuery;
import com.targosystem.varejo.seguranca.application.usecases.AtualizarUsuarioUseCase;
import com.targosystem.varejo.seguranca.application.usecases.CriarUsuarioUseCase;
import com.targosystem.varejo.seguranca.application.usecases.LoginUsuarioUseCase;
import com.targosystem.varejo.seguranca.domain.events.UsuarioCriadoEvent;
import com.targosystem.varejo.seguranca.domain.listeners.UsuarioCriadoLoggerListener;
import com.targosystem.varejo.seguranca.domain.repository.PapelRepository;
import com.targosystem.varejo.seguranca.domain.repository.UsuarioRepository;
import com.targosystem.varejo.seguranca.domain.service.AutenticadorUsuario;
import com.targosystem.varejo.seguranca.domain.service.PasswordEncryptor;
import com.targosystem.varejo.seguranca.infra.gui.MainApplicationFrame;
import com.targosystem.varejo.seguranca.infra.persistence.PapelDao;
import com.targosystem.varejo.seguranca.infra.persistence.UsuarioDao;
import com.targosystem.varejo.seguranca.infra.security.BCryptPasswordEncryptor;

import com.targosystem.varejo.promocoes.application.PromocaoService;
import com.targosystem.varejo.promocoes.application.usecases.CriarPromocaoUseCase;
import com.targosystem.varejo.promocoes.application.query.ListarPromocoesAtivasQuery;
import com.targosystem.varejo.promocoes.domain.repository.PromocaoRepository;
import com.targosystem.varejo.promocoes.infra.persistence.PromocaoDao;

import com.targosystem.varejo.estoque.application.EstoqueService;
import com.targosystem.varejo.estoque.application.usecases.RegistrarMovimentacaoEstoqueUseCase;
import com.targosystem.varejo.estoque.domain.repository.EstoqueRepository;
import com.targosystem.varejo.estoque.infra.persistence.EstoqueDao;

import com.targosystem.varejo.clientes.application.ClienteService;
import com.targosystem.varejo.clientes.application.usecases.CadastrarClienteUseCase;
import com.targosystem.varejo.clientes.application.usecases.AtualizarClienteUseCase;
import com.targosystem.varejo.clientes.application.query.ListarTodosClientesQuery;
import com.targosystem.varejo.clientes.domain.repository.ClienteRepository;
import com.targosystem.varejo.clientes.infra.persistence.ClienteDao;

// TODO: Importações para Fornecedor e Venda
import com.targosystem.varejo.fornecedores.application.FornecedorService;
import com.targosystem.varejo.fornecedores.application.usecases.AtualizarFornecedorUseCase;
import com.targosystem.varejo.fornecedores.application.usecases.AvaliarEntregaFornecedorUseCase;
import com.targosystem.varejo.fornecedores.application.usecases.CriarFornecedorUseCase;
import com.targosystem.varejo.fornecedores.application.usecases.InativarFornecedorUseCase;
import com.targosystem.varejo.fornecedores.application.usecases.RegistrarEntregaFornecedorUseCase;
import com.targosystem.varejo.fornecedores.application.usecases.RegistrarRecebimentoEntregaUseCase;
import com.targosystem.varejo.fornecedores.application.query.ListarEntregasPorFornecedorQuery;
import com.targosystem.varejo.fornecedores.application.query.ListarTodosFornecedoresQuery;
import com.targosystem.varejo.fornecedores.domain.repository.FornecedorRepository;
import com.targosystem.varejo.fornecedores.domain.repository.EntregaFornecedorRepository;
import com.targosystem.varejo.fornecedores.infra.persistence.FornecedorDao;
import com.targosystem.varejo.fornecedores.infra.persistence.EntregaFornecedorDao;


import com.targosystem.varejo.vendas.application.VendaService;
import com.targosystem.varejo.vendas.application.usecases.RealizarVendaUseCase;
import com.targosystem.varejo.vendas.application.usecases.CancelarVendaUseCase;
import com.targosystem.varejo.vendas.application.usecases.AplicarDescontoVendaUseCase;
import com.targosystem.varejo.vendas.application.query.ConsultarVendaPorIdQuery;
import com.targosystem.varejo.vendas.application.query.ListarTodasVendasQuery;
import com.targosystem.varejo.vendas.domain.repository.VendaRepository;
import com.targosystem.varejo.vendas.domain.service.GestorPoliticaPreco;
import com.targosystem.varejo.vendas.domain.service.ValidadorDesconto;
import com.targosystem.varejo.vendas.infra.integration.EstoqueEventProducer;
import com.targosystem.varejo.vendas.infra.persistence.VendaDao;

import com.targosystem.varejo.shared.infra.SimpleEventPublisher;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Initializing Targo System Application...");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("targo_system_pu");
        EntityManager entityManager = emf.createEntityManager();

        SimpleEventPublisher eventPublisher = new SimpleEventPublisher();
        eventPublisher.subscribe(ProdutoCadastradoEvent.class, new ProdutoCadastradoLoggerListener());
        eventPublisher.subscribe(UsuarioCriadoEvent.class, new UsuarioCriadoLoggerListener());

        // --- Configuração do Bounded Context de PRODUTOS ---
        logger.info("Configuring 'Produtos' Bounded Context.");
        ProdutoRepository produtoRepository = new ProdutoDao(entityManager);
        CategoriaRepository categoriaRepository = new CategoriaDao(entityManager);
        ClassificadorProduto classificadorProduto = new ClassificadorProduto(categoriaRepository);

        CadastrarProdutoUseCase cadastrarProdutoUseCase = new CadastrarProdutoUseCase(produtoRepository, categoriaRepository, classificadorProduto, eventPublisher);
        AtualizarProdutoUseCase atualizarProdutoUseCase = new AtualizarProdutoUseCase(produtoRepository, categoriaRepository, classificadorProduto);
        ObterProdutoPorIdQuery obterProdutoPorIdQuery = new ObterProdutoPorIdQuery(produtoRepository);
        ListarTodosProdutosQuery listarTodosProdutosQuery = new ListarTodosProdutosQuery(produtoRepository);

        ProdutoService produtoService = new ProdutoService(
                cadastrarProdutoUseCase,
                atualizarProdutoUseCase,
                obterProdutoPorIdQuery,
                listarTodosProdutosQuery
        );

        // --- Configuração do Bounded Context de SEGURANÇA ---
        logger.info("Configuring 'Seguranca' Bounded Context.");
        UsuarioRepository usuarioRepository = new UsuarioDao(entityManager);
        PapelRepository papelRepository = new PapelDao(entityManager);
        PasswordEncryptor passwordEncryptor = new BCryptPasswordEncryptor();
        AutenticadorUsuario autenticadorUsuario = new AutenticadorUsuario(usuarioRepository, passwordEncryptor);

        CriarUsuarioUseCase criarUsuarioUseCase = new CriarUsuarioUseCase(usuarioRepository, papelRepository, passwordEncryptor, eventPublisher);
        AtualizarUsuarioUseCase atualizarUsuarioUseCaseSeguranca = new AtualizarUsuarioUseCase(usuarioRepository, papelRepository);
        LoginUsuarioUseCase loginUsuarioUseCase = new LoginUsuarioUseCase(autenticadorUsuario, eventPublisher);
        ObterUsuarioPorIdQuery obterUsuarioPorIdQuerySeguranca = new ObterUsuarioPorIdQuery(usuarioRepository);
        ListarUsuariosQuery listarUsuariosQuery = new ListarUsuariosQuery(usuarioRepository);
        ListarPapeisQuery listarPapeisQuery = new ListarPapeisQuery(papelRepository);

        SegurancaService segurancaService = new SegurancaService(
                criarUsuarioUseCase,
                atualizarUsuarioUseCaseSeguranca,
                loginUsuarioUseCase,
                obterUsuarioPorIdQuerySeguranca,
                listarUsuariosQuery,
                papelRepository,
                entityManager
        );

        // --- Configuração do Bounded Context de PROMOÇÕES ---
        logger.info("Configuring 'Promocoes' Bounded Context.");
        PromocaoRepository promocaoRepository = new PromocaoDao(entityManager);
        KitPromocionalRepository kitPromocionalRepository = new KitPromocionalDao(entityManager);

        CriarPromocaoUseCase criarPromocaoUseCase = new CriarPromocaoUseCase(promocaoRepository, eventPublisher);
        AtualizarPromocaoUseCase atualizarPromocaoUseCase = new AtualizarPromocaoUseCase(promocaoRepository, eventPublisher);
        ObterPromocaoPorIdQuery obterPromocaoPorIdQuery = new ObterPromocaoPorIdQuery(promocaoRepository);
        ListarPromocoesAtivasQuery listarPromocoesAtivasQuery = new ListarPromocoesAtivasQuery(promocaoRepository);

        CriarKitPromocionalUseCase criarKitPromocionalUseCase = new CriarKitPromocionalUseCase(kitPromocionalRepository, eventPublisher);
        ObterKitPromocionalPorIdQuery obterKitPromocionalPorIdQuery = new ObterKitPromocionalPorIdQuery(kitPromocionalRepository);

        PromocaoService promocaoService = new PromocaoService(
                criarPromocaoUseCase,
                atualizarPromocaoUseCase,
                obterPromocaoPorIdQuery,
                listarPromocoesAtivasQuery,
                criarKitPromocionalUseCase,
                obterKitPromocionalPorIdQuery
        );

        // --- Configuração do Bounded Context de ESTOQUE ---
        logger.info("Configuring 'Estoque' Bounded Context.");
        EstoqueRepository estoqueRepository = new EstoqueDao(entityManager); // Assuming EstoqueDao exists
        // Nota: Um serviço de estoque pode precisar de ProdutoRepository para buscar produtos,
        // mas por simplicidade, para o exemplo, vamos apenas usar o EstoqueRepository.

        RegistrarMovimentacaoEstoqueUseCase registrarMovimentacaoEstoqueUseCase = new RegistrarMovimentacaoEstoqueUseCase(estoqueRepository, eventPublisher);
        RegistrarEntradaEstoqueUseCase registrarEntradaEstoqueUseCase = new RegistrarEntradaEstoqueUseCase(estoqueRepository, eventPublisher);
        RegistrarSaidaEstoqueUseCase registrarSaidaEstoqueUseCase = new RegistrarSaidaEstoqueUseCase(estoqueRepository, eventPublisher);
        ConsultarEstoquePorProdutoIdQuery consultarEstoquePorProdutoIdQuery = new ConsultarEstoquePorProdutoIdQuery(estoqueRepository);

        EstoqueService estoqueService = new EstoqueService(
              registrarEntradaEstoqueUseCase,
              registrarSaidaEstoqueUseCase,
              consultarEstoquePorProdutoIdQuery,
              registrarMovimentacaoEstoqueUseCase
        );

        // --- Configuração do Bounded Context de CLIENTES ---
        logger.info("Configuring 'Clientes' Bounded Context.");
        ClienteRepository clienteRepository = new ClienteDao(entityManager);

        CadastrarClienteUseCase cadastrarClienteUseCase = new CadastrarClienteUseCase(clienteRepository);
        AtualizarClienteUseCase atualizarClienteUseCase = new AtualizarClienteUseCase(clienteRepository);
        ListarTodosClientesQuery listarTodosClientesQuery = new ListarTodosClientesQuery(clienteRepository);
        ObterClientePorIdQuery obterClientePorIdQuery = new ObterClientePorIdQuery(clienteRepository);

        ClienteService clienteService = new ClienteService(
                cadastrarClienteUseCase,
                atualizarClienteUseCase,
                obterClientePorIdQuery,
                listarTodosClientesQuery
        );

        // --- Configuração do Bounded Context de FORNECEDORES (PENDENTE) ---
        // TODO: Você precisará criar as DAOs, Repositórios e UseCases/Queries
        // para Fornecedor e EntregaFornecedor.
        logger.info("Configuring 'Fornecedores' Bounded Context (Placeholder).");
        FornecedorRepository fornecedorRepository = new FornecedorDao(entityManager);
        EntregaFornecedorRepository entregaFornecedorRepository = new EntregaFornecedorDao(entityManager);

        CriarFornecedorUseCase criarFornecedorUseCase = new CriarFornecedorUseCase(fornecedorRepository, eventPublisher);
        AtualizarFornecedorUseCase atualizarFornecedorUseCase = new AtualizarFornecedorUseCase(fornecedorRepository, eventPublisher);
        InativarFornecedorUseCase inativarFornecedorUseCase = new InativarFornecedorUseCase(fornecedorRepository, eventPublisher);

        RegistrarEntregaFornecedorUseCase registrarEntregaFornecedorUseCase = new RegistrarEntregaFornecedorUseCase(entregaFornecedorRepository, fornecedorRepository, eventPublisher);
        RegistrarRecebimentoEntregaUseCase registrarRecebimentoEntregaUseCase = new RegistrarRecebimentoEntregaUseCase(entregaFornecedorRepository, eventPublisher);
        AvaliarEntregaFornecedorUseCase avaliarEntregaFornecedorUseCase = new AvaliarEntregaFornecedorUseCase(entregaFornecedorRepository, eventPublisher);

        ListarTodosFornecedoresQuery listarTodosFornecedoresQuery = new ListarTodosFornecedoresQuery(fornecedorRepository);
        ConsultarFornecedorPorIdQuery consultarFornecedorPorIdQuery = new ConsultarFornecedorPorIdQuery(fornecedorRepository);
        ConsultarFornecedorPorCnpjQuery consultarFornecedorPorCnpjQuery = new ConsultarFornecedorPorCnpjQuery(fornecedorRepository);
        ListarEntregasPorFornecedorQuery listarEntregasPorFornecedorQuery = new ListarEntregasPorFornecedorQuery(entregaFornecedorRepository);


        FornecedorService fornecedorService = new FornecedorService(
                criarFornecedorUseCase,
                atualizarFornecedorUseCase,
                inativarFornecedorUseCase,
                registrarEntregaFornecedorUseCase,
                registrarRecebimentoEntregaUseCase,
                avaliarEntregaFornecedorUseCase,
                consultarFornecedorPorIdQuery,
                consultarFornecedorPorCnpjQuery,
                listarTodosFornecedoresQuery,
                listarEntregasPorFornecedorQuery
        );


        // --- Configuração do Bounded Context de VENDAS (PENDENTE) ---
        // TODO: Você precisará criar as DAOs, Repositórios e UseCases/Queries
        // para Venda e Itens de Venda.
        logger.info("Configuring 'Vendas' Bounded Context (Placeholder).");
        EstoqueEventProducer estoqueEventProducer = new EstoqueEventProducer(eventPublisher);
        ConsultarPrecoProdutoQuery consultarPrecoProdutoQuery = new ConsultarPrecoProdutoQuery(produtoRepository);
        GestorPoliticaPreco gestorPoliticaPreco = new GestorPoliticaPreco(consultarPrecoProdutoQuery);
        ValidadorDesconto validadorDesconto = new ValidadorDesconto();
        VendaRepository vendaRepository = new VendaDao(entityManager, clienteRepository);
        RealizarVendaUseCase realizarVendaUseCase = new RealizarVendaUseCase(vendaRepository, clienteRepository, produtoRepository, gestorPoliticaPreco, estoqueEventProducer, eventPublisher); // Assume dependência de ProdutoRepository
        CancelarVendaUseCase cancelarVendaUseCase = new CancelarVendaUseCase(vendaRepository, estoqueEventProducer, eventPublisher);
        AplicarDescontoVendaUseCase aplicarDescontoVendaUseCase = new AplicarDescontoVendaUseCase(vendaRepository, validadorDesconto, eventPublisher);

        ConsultarVendaPorIdQuery consultarVendaPorIdQuery = new ConsultarVendaPorIdQuery(vendaRepository);
        ListarTodasVendasQuery listarTodasVendasQuery = new ListarTodasVendasQuery(vendaRepository);

        VendaService vendaService = new VendaService(
                realizarVendaUseCase,
                cancelarVendaUseCase,
                aplicarDescontoVendaUseCase,
                consultarVendaPorIdQuery,
                listarTodasVendasQuery
        );


        // --- Inicialização da Interface Gráfica Principal ---
        SwingUtilities.invokeLater(() -> {
            try {
                logger.info("Starting Main Application Frame (UI).");
                MainApplicationFrame mainFrame = new MainApplicationFrame(
                        segurancaService,
                        produtoService,
                        promocaoService,
                        estoqueService,
                        clienteService,
                        fornecedorService, // Adicionando FornecedorService
                        vendaService // Adicionando VendaService
                );

                mainFrame.setVisible(true);

                // --- Simulação de Login (TEMPORÁRIA) ---
                UsuarioOutput dummyUser = new UsuarioOutput(
                        "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                        "admin",
                        "Administrador do Sistema",
                        "admin@targosystem.com",
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        Collections.singletonList("ADMIN")
                );
                mainFrame.onLoginSuccess(dummyUser);
            } catch (Exception e) {
                logger.error("Failed to start UI: {}", e.getMessage(), e);
                JOptionPane.showMessageDialog(null, "Erro ao iniciar a interface gráfica: " + e.getMessage(), "Erro Fatal", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });

        // Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down application resources...");
            if (entityManager.isOpen()) {
                entityManager.close();
                logger.info("EntityManager closed.");
            }
            if (emf.isOpen()) {
                emf.close();
                logger.info("EntityManagerFactory closed.");
            }
            logger.info("Targo System Application terminated.");
        }));
    }
}