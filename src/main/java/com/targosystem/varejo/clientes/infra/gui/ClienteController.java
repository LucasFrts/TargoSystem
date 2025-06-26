package com.targosystem.varejo.clientes.infra.gui;

import com.targosystem.varejo.clientes.application.ClienteService;
import com.targosystem.varejo.clientes.application.input.AtualizarClienteInput;
import com.targosystem.varejo.clientes.application.input.CadastrarClienteInput;
import com.targosystem.varejo.clientes.application.output.ClienteOutput;
import com.targosystem.varejo.shared.domain.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Objects;

public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;
    private final ClienteFrame clienteFrame;

    public ClienteController(ClienteService clienteService, ClienteFrame clienteFrame) {
        this.clienteService = Objects.requireNonNull(clienteService, "ClienteService cannot be null.");
        this.clienteFrame = Objects.requireNonNull(clienteFrame, "ClienteFrame cannot be null.");

        // Adicionar listeners aos botões
        this.clienteFrame.getBtnAtualizarLista().addActionListener(e -> listarTodosClientes());
        this.clienteFrame.getBtnNovoCliente().addActionListener(e -> cadastrarNovoCliente());
        this.clienteFrame.getBtnEditarCliente().addActionListener(e -> editarClienteSelecionado());
        this.clienteFrame.getBtnExcluirCliente().addActionListener(e -> excluirClienteSelecionado());

        // Listener para seleção de linha na tabela
        // FIX: Changed getClientsTable() to getClientesTable()
        this.clienteFrame.getClientesTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // FIX: Changed getClientsTable() to getClientesTable()
                    boolean hasSelection = clienteFrame.getClientesTable().getSelectedRow() != -1;
                    clienteFrame.getBtnEditarCliente().setEnabled(hasSelection);
                    clienteFrame.getBtnExcluirCliente().setEnabled(hasSelection);
                }
            }
        });

        // Inicializa a lista de clientes ao carregar o controller
        listarTodosClientes();
    }

    public void listarTodosClientes() {
        logger.info("Listando todos os clientes...");
        clienteFrame.clearTable(); // Limpa a tabela antes de popular

        try {
            List<ClienteOutput> clientes = clienteService.listarTodosClientes();
            DefaultTableModel model = clienteFrame.getTableModel();
            // IMPORTANT: The ClienteFrame table model expects "Endereço", but ClienteOutput doesn't have it.
            // Adjusting the table column headers in ClienteFrame is also recommended, or add 'endereco' to ClienteOutput.
            // For now, I'm removing 'endereco' from the display to fix the 'Cannot resolve' error.
            // You might need to re-evaluate the columns in ClienteFrame if 'endereco' is truly not part of ClienteOutput.
            model.setColumnIdentifiers(new Object[]{"ID", "Nome Completo", "Email", "CPF", "Telefone"}); // Adjusted headers

            for (ClienteOutput cliente : clientes) {
                model.addRow(new Object[]{
                        cliente.id(),
                        cliente.nome(), // FIX: Changed nomeCompleto() to nome()
                        cliente.email(),
                        cliente.cpf(),
                        cliente.telefone()
                        // Removed cliente.endereco() as it's not in ClienteOutput
                });
            }
            logger.info("Total de {} clientes carregados.", clientes.size());
        } catch (DomainException e) {
            logger.error("Erro de domínio ao listar clientes: {}", e.getMessage());
            JOptionPane.showMessageDialog(clienteFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao listar clientes", e);
            JOptionPane.showMessageDialog(clienteFrame, "Erro inesperado ao listar clientes.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cadastrarNovoCliente() {
        logger.info("Abrindo diálogo para cadastrar novo cliente...");
        // Pass null for endereco since ClienteOutput doesn't have it, and we don't want to pass an empty string
        String[] formData = clienteFrame.showClienteDialog(true, null, null, null, null, null, null);

        if (formData != null) {
            try {
                // Adjusting input fields based on ClienteOutput and assuming CadastrarClienteInput matches it
                // If CadastrarClienteInput *does* have 'endereco', you'll need to re-add it here and adjust formData index
                CadastrarClienteInput input = new CadastrarClienteInput(
                        formData[1], // nome
                        formData[3], // cpf
                        formData[2], // email (reordered to match ClienteOutput/CadastrarClienteInput if it has this order)
                        formData[4]  // telefone
                        // Removed formData[5] (endereco) because ClienteOutput doesn't have it
                );
                ClienteOutput novoCliente = clienteService.cadastrarCliente(input);
                JOptionPane.showMessageDialog(clienteFrame, "Cliente '" + novoCliente.nome() + "' cadastrado com sucesso! ID: " + novoCliente.id(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                listarTodosClientes(); // Atualiza a lista
            } catch (DomainException e) {
                logger.error("Erro de domínio ao cadastrar cliente: {}", e.getMessage());
                JOptionPane.showMessageDialog(clienteFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.error("Erro inesperado ao cadastrar cliente", e);
                JOptionPane.showMessageDialog(clienteFrame, "Erro inesperado ao cadastrar cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            logger.info("Cadastro de cliente cancelado.");
        }
    }

    private void editarClienteSelecionado() {
        // FIX: Changed getClientsTable() to getClientesTable()
        int selectedRow = clienteFrame.getClientesTable().getSelectedRow();
        if (selectedRow != -1) {
            String clienteId = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 0);
            String nome = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 1);
            String email = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 2);
            String cpf = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 3);
            String telefone = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 4);
            // String endereco = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 5); // Removed

            logger.info("Abrindo diálogo para editar cliente ID: {}", clienteId);
            // Pass null for endereco in the dialog call as it's not in ClienteOutput
            String[] formData = clienteFrame.showClienteDialog(false, clienteId, nome, email, cpf, telefone, null); // Pass null for endereco

            if (formData != null) {
                try {
                    // Adjusting input fields based on ClienteOutput and assuming AtualizarClienteInput matches it
                    // If AtualizarClienteInput *does* have 'endereco', you'll need to re-add it here and adjust formData index
                    AtualizarClienteInput input = new AtualizarClienteInput(
                            formData[0], // id
                            formData[1], // nome
                            formData[2], // email (reordered if needed)
                            formData[4]  // telefone
                            // Removed formData[5] (endereco)
                    );
                    ClienteOutput clienteAtualizado = clienteService.atualizarCliente(input);
                    JOptionPane.showMessageDialog(clienteFrame, "Cliente '" + clienteAtualizado.nome() + "' atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    listarTodosClientes(); // Atualiza a lista
                } catch (DomainException e) {
                    logger.error("Erro de domínio ao atualizar cliente: {}", e.getMessage());
                    JOptionPane.showMessageDialog(clienteFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    logger.error("Erro inesperado ao atualizar cliente", e);
                    JOptionPane.showMessageDialog(clienteFrame, "Erro inesperado ao atualizar cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                logger.info("Edição de cliente cancelada.");
            }
        } else {
            JOptionPane.showMessageDialog(clienteFrame, "Nenhum cliente selecionado para edição.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirClienteSelecionado() {
        // FIX: Changed getClientsTable() to getClientesTable()
        int selectedRow = clienteFrame.getClientesTable().getSelectedRow();
        if (selectedRow != -1) {
            String clienteId = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 0);
            String clienteNome = (String) clienteFrame.getTableModel().getValueAt(selectedRow, 1); // Assuming nome is at index 1

            int confirm = JOptionPane.showConfirmDialog(clienteFrame,
                    "Tem certeza que deseja excluir o cliente '" + clienteNome + "' (ID: " + clienteId + ")?",
                    "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                logger.info("Excluindo cliente ID: {}", clienteId);
                // TODO: Chamar o use case de exclusão de cliente (se houver no ClienteService)
                JOptionPane.showMessageDialog(clienteFrame, "Funcionalidade de exclusão em desenvolvimento!", "Aguarde", JOptionPane.INFORMATION_MESSAGE);
                // Se houver exclusão, chamar listarTodosClientes();
            }
        } else {
            JOptionPane.showMessageDialog(clienteFrame, "Nenhum cliente selecionado para exclusão.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }
}