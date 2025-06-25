// src/main/java/com/targosystem/Main.java
package com.targosystem;

import com.targosystem.varejo.produtos.application.ProdutoService;
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
import com.targosystem.varejo.seguranca.application.SegurancaService;
import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;
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

        SegurancaService segurancaService = new SegurancaService(
                criarUsuarioUseCase,
                atualizarUsuarioUseCaseSeguranca,
                loginUsuarioUseCase,
                obterUsuarioPorIdQuerySeguranca,
                listarUsuariosQuery
        );

        // --- Inicialização da Interface Gráfica Principal ---
        SwingUtilities.invokeLater(() -> {
            try {
                logger.info("Starting Main Application Frame (UI).");
                MainApplicationFrame mainFrame = new MainApplicationFrame(segurancaService, produtoService);

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