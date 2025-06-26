package com.targosystem.varejo.seguranca.infra.gui;

import com.targosystem.varejo.seguranca.application.SegurancaService;
import com.targosystem.varejo.seguranca.application.input.CriarUsuarioInput;
import com.targosystem.varejo.seguranca.application.output.PapelOutput; // Precisamos disso para listar os papéis
import com.targosystem.varejo.seguranca.application.output.UsuarioOutput; // Para o retorno do serviço
import com.targosystem.varejo.shared.domain.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioController {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final SegurancaService segurancaService;
    private final UsuarioFrame usuarioFrame;

    public UsuarioController(SegurancaService segurancaService, UsuarioFrame usuarioFrame) {
        this.segurancaService = segurancaService;
        this.usuarioFrame = usuarioFrame;
        setupListeners();
    }

    private void setupListeners() {
        usuarioFrame.addCriarUsuarioListener(e -> criarUsuario());
        usuarioFrame.addLimparCamposListener(e -> usuarioFrame.clearFields());
    }

    public void carregarPapeisDisponiveis() {
        try {
            List<PapelOutput> papeis = segurancaService.listarPapeis(); // Assumindo que este método existe
            List<String> nomesPapeis = papeis.stream()
                    .map(PapelOutput::nome)
                    .collect(Collectors.toList());
            usuarioFrame.setPapeisDisponiveis(nomesPapeis);
            logger.info("Papéis disponíveis carregados para o frame de usuário.");
        } catch (Exception e) {
            logger.error("Erro ao carregar papéis disponíveis: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(usuarioFrame, "Erro ao carregar papéis: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarUsuario() {
        String username = usuarioFrame.getUsername();
        String nomeCompleto = usuarioFrame.getNomeCompleto();
        String email = usuarioFrame.getEmail();
        String password = new String(usuarioFrame.getPassword()); // Cuidado com segurança em produção.
        boolean isActive = usuarioFrame.isActive();
        List<String> selectedPapeis = usuarioFrame.getSelectedPapeis();

        if (username.isEmpty() || nomeCompleto.isEmpty() || email.isEmpty() || password.isEmpty() || selectedPapeis.isEmpty()) {
            JOptionPane.showMessageDialog(usuarioFrame, "Todos os campos e pelo menos um papel devem ser preenchidos.", "Campos Obrigatórios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Criar o input para o serviço
            CriarUsuarioInput input = new CriarUsuarioInput(
                    username,
                    password,
                    nomeCompleto,
                    email,
                    selectedPapeis
            );

            // Chamar o serviço para criar o usuário
            UsuarioOutput novoUsuario = segurancaService.criarUsuario(input);

            JOptionPane.showMessageDialog(usuarioFrame, "Usuário '" + novoUsuario.username() + "' criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            usuarioFrame.clearFields(); // Limpa o formulário após o sucesso
            logger.info("Usuário {} criado com sucesso.", novoUsuario.username());

        } catch (DomainException e) {
            logger.error("Erro de domínio ao criar usuário: {}", e.getMessage());
            JOptionPane.showMessageDialog(usuarioFrame, "Erro ao criar usuário: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar usuário: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(usuarioFrame, "Ocorreu um erro inesperado ao criar usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}