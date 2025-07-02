package com.targosystem.varejo.clientes.infra.gui;

import com.targosystem.varejo.clientes.application.ClienteService;
import com.targosystem.varejo.clientes.application.input.AtualizarClienteInput;
import com.targosystem.varejo.clientes.application.input.CadastrarClienteInput;
import com.targosystem.varejo.clientes.application.output.ClienteOutput;
import com.targosystem.varejo.clientes.application.usecases.ExcluirClienteUseCase;
import com.targosystem.varejo.shared.domain.DomainException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Objects;

public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;
    private final ExcluirClienteUseCase excluirClienteUseCase;
    private final ClienteFrame clienteFrame;

    public ClienteController(
            ClienteService clienteService,
            ClienteFrame clienteFrame,
            ExcluirClienteUseCase excluirClienteUseCase
    ) {
        this.clienteService = Objects.requireNonNull(clienteService);
        this.clienteFrame = Objects.requireNonNull(clienteFrame);
        this.excluirClienteUseCase = Objects.requireNonNull(excluirClienteUseCase);

        this.clienteFrame.getBtnAtualizarLista().addActionListener(e -> listarTodosClientes());
        this.clienteFrame.getBtnNovoCliente().addActionListener(e -> cadastrarNovoCliente());
        this.clienteFrame.getBtnEditarCliente().addActionListener(e -> editarClienteSelecionado());
        this.clienteFrame.getBtnExcluirCliente().addActionListener(e -> excluirClienteSelecionado());

        this.clienteFrame.getClientesTable().getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = clienteFrame.getClientesTable().getSelectedRow() != -1;
            clienteFrame.getBtnEditarCliente().setEnabled(hasSelection);
            clienteFrame.getBtnExcluirCliente().setEnabled(hasSelection);
        });

        listarTodosClientes();
    }

    public void listarTodosClientes() {
        logger.info("Listando todos os clientes...");
        clienteFrame.clearTable();

        try {
            List<ClienteOutput> clientes = clienteService.listarTodosClientes();
            DefaultTableModel model = clienteFrame.getTableModel();

            model.setColumnIdentifiers(new Object[]{"ID", "Nome Completo", "Email", "CPF", "Telefone"});

            for (ClienteOutput cliente : clientes) {
                model.addRow(new Object[]{
                        cliente.id(),
                        cliente.nome(),
                        cliente.email(),
                        cliente.cpf(),
                        cliente.telefone()
                });
            }

            logger.info("Total de {} clientes carregados.", clientes.size());
        } catch (DomainException e) {
            logger.error("Erro de domínio ao listar clientes: {}", e.getMessage());
            JOptionPane.showMessageDialog(clienteFrame, "Erro: " + e.getMessage(), "Erro de Domínio", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao listar clientes", e);
            JOptionPane.showMessageDialog(clienteFrame, "Erro inesperado ao listar clientes.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cadastrarNovoCliente() {
        logger.info("Abrindo diálogo para cadastrar novo cliente...");
        String[] formData = clienteFrame.showClienteDialog(true, null, null, null, null, null, null);

        if (formData != null) {
            try {
                CadastrarClienteInput input = new CadastrarClienteInput(
                        formData[1],
                        formData[3],
                        formData[2],
                        formData[4]
                );
                ClienteOutput novoCliente = clienteService.cadastrarCliente(input);
                JOptionPane.showMessageDialog(clienteFrame, "Cliente '" + novoCliente.nome() + "' cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                listarTodosClientes();
            } catch (DomainException e) {
                logger.error("Erro de domínio ao cadastrar cliente: {}", e.getMessage());
                JOptionPane.showMessageDialog(clienteFrame, "Erro: " + e.getMessage(), "Erro de Domínio", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.error("Erro inesperado ao cadastrar cliente", e);
                JOptionPane.showMessageDialog(clienteFrame, "Erro inesperado ao cadastrar cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarClienteSelecionado() {
        int selectedRow = clienteFrame.getClientesTable().getSelectedRow();
        if (selectedRow != -1) {
            String clienteId = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 0);
            String nome = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 1);
            String email = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 2);
            String cpf = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 3);
            String telefone = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 4);

            logger.info("Editando cliente ID: {}", clienteId);
            String[] formData = clienteFrame.showClienteDialog(false, clienteId, nome, email, cpf, telefone, null);

            if (formData != null) {
                try {
                    AtualizarClienteInput input = new AtualizarClienteInput(
                            formData[0],
                            formData[1],
                            formData[2],
                            formData[4]
                    );
                    ClienteOutput clienteAtualizado = clienteService.atualizarCliente(input);
                    JOptionPane.showMessageDialog(clienteFrame, "Cliente '" + clienteAtualizado.nome() + "' atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    listarTodosClientes();
                } catch (DomainException e) {
                    logger.error("Erro de domínio ao atualizar cliente: {}", e.getMessage());
                    JOptionPane.showMessageDialog(clienteFrame, "Erro: " + e.getMessage(), "Erro de Domínio", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    logger.error("Erro inesperado ao atualizar cliente", e);
                    JOptionPane.showMessageDialog(clienteFrame, "Erro inesperado ao atualizar cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void excluirClienteSelecionado() {
        int selectedRow = clienteFrame.getClientesTable().getSelectedRow();
        if (selectedRow != -1) {
            String clienteId = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 0);
            String clienteNome = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(clienteFrame,
                    "Tem certeza que deseja excluir o cliente '" + clienteNome + "' (ID: " + clienteId + ")?",
                    "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    excluirClienteUseCase.execute(clienteId);
                    JOptionPane.showMessageDialog(clienteFrame, "Cliente '" + clienteNome + "' excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    listarTodosClientes();
                } catch (DomainException e) {
                    logger.error("Erro de domínio ao excluir cliente: {}", e.getMessage());
                    JOptionPane.showMessageDialog(clienteFrame, "Erro: " + e.getMessage(), "Erro de Domínio", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    logger.error("Erro inesperado ao excluir cliente", e);
                    JOptionPane.showMessageDialog(clienteFrame, "Erro inesperado ao excluir cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
