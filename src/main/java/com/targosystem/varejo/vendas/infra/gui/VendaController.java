package com.targosystem.varejo.vendas.infra.gui;

import com.targosystem.varejo.vendas.application.VendaService;
import com.targosystem.varejo.vendas.application.input.AplicarDescontoVendaInput;
import com.targosystem.varejo.vendas.application.input.CancelarVendaInput;
import com.targosystem.varejo.vendas.application.input.RealizarVendaInput;
import com.targosystem.varejo.vendas.application.output.VendaOutput;
import com.targosystem.varejo.shared.domain.DomainException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VendaController {

    private static final Logger logger = LoggerFactory.getLogger(VendaController.class);

    private final VendaService vendaService;
    private final VendaFrame vendaFrame;

    public VendaController(VendaService vendaService, VendaFrame vendaFrame) {
        this.vendaService = Objects.requireNonNull(vendaService, "VendaService cannot be null.");
        this.vendaFrame = Objects.requireNonNull(vendaFrame, "VendaFrame cannot be null.");

        // --- Listeners para o Painel de Vendas ---
        this.vendaFrame.getBtnAtualizarVendas().addActionListener(e -> listarTodasVendas());
        this.vendaFrame.getBtnNovaVenda().addActionListener(e -> realizarNovaVenda());
        this.vendaFrame.getBtnCancelarVenda().addActionListener(e -> cancelarVendaSelecionada());
        this.vendaFrame.getBtnAplicarDesconto().addActionListener(e -> aplicarDescontoVendaSelecionada());

        // Listener para seleção de linha na tabela de vendas
        this.vendaFrame.getVendasTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean hasSelection = vendaFrame.getVendasTable().getSelectedRow() != -1;
                    vendaFrame.getBtnCancelarVenda().setEnabled(hasSelection);
                    vendaFrame.getBtnAplicarDesconto().setEnabled(hasSelection);
                }
            }
        });

        // Inicializa a lista de vendas ao carregar o controller
        listarTodasVendas();
    }

    public void listarTodasVendas() {
        logger.info("Listando todas as vendas...");
        vendaFrame.clearVendasTable();

        try {
            List<VendaOutput> vendas = vendaService.listarTodasVendas();
            DefaultTableModel model = vendaFrame.getVendasTableModel();

            model.setColumnIdentifiers(new Object[]{"ID", "Cliente ID", "Data Venda", "Total Bruto", "Desconto", "Total Líquido", "Status"});


            for (VendaOutput venda : vendas) {
                model.addRow(new Object[]{
                        venda.id(),
                        venda.cliente().id(),
                        venda.dataVenda(),
                        venda.valorTotal(),
                        venda.valorDesconto(),
                        venda.valorFinal(),
                        venda.status()
                });
            }
            logger.info("Total de {} vendas carregadas.", vendas.size());
        } catch (DomainException e) {
            logger.error("Erro de domínio ao listar vendas: {}", e.getMessage());
            JOptionPane.showMessageDialog(vendaFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao listar vendas", e);
            JOptionPane.showMessageDialog(vendaFrame, "Erro inesperado ao listar vendas.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void realizarNovaVenda() {
        logger.info("Abrindo diálogo para realizar nova venda...");

        String[] formData = vendaFrame.showRealizarVendaDialog();

        if (formData != null) {
            try {
                String idCliente = formData[0];
                String idProduto = formData[1];
                int quantidade = Integer.parseInt(formData[2]);
                BigDecimal valorDesconto = new BigDecimal(formData[3]);

                List<RealizarVendaInput.ItemVendaInput> itensVenda = new ArrayList<>();
                itensVenda.add(new RealizarVendaInput.ItemVendaInput(idProduto, quantidade));

                RealizarVendaInput input = new RealizarVendaInput(idCliente, itensVenda, valorDesconto);

                VendaOutput novaVenda = vendaService.realizarVenda(input);
                JOptionPane.showMessageDialog(vendaFrame, "Venda '" + novaVenda.id() + "' realizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                listarTodasVendas();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(vendaFrame, "Erro de formato. Verifique ID do Produto (não vazio), Quantidade (número inteiro) e Valor Desconto (número decimal válido).", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            } catch (DomainException e) {
                logger.error("Erro de domínio ao realizar venda: {}", e.getMessage());
                JOptionPane.showMessageDialog(vendaFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.error("Erro inesperado ao realizar venda", e);
                JOptionPane.showMessageDialog(vendaFrame, "Erro inesperado ao realizar venda.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            logger.info("Realização de venda cancelada.");
        }
    }


    private void cancelarVendaSelecionada() {
        int selectedRow = vendaFrame.getVendasTable().getSelectedRow();
        if (selectedRow != -1) {
            String vendaId = (String) vendaFrame.getVendasTableModel().getValueAt(selectedRow, 0);
            String statusAtual = (String) vendaFrame.getVendasTableModel().getValueAt(selectedRow, 6); // Coluna do Status

            if ("CANCELADA".equalsIgnoreCase(statusAtual)) {
                JOptionPane.showMessageDialog(vendaFrame, "Esta venda já está CANCELADA.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(vendaFrame,
                    "Tem certeza que deseja cancelar a venda ID: '" + vendaId + "'?",
                    "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    logger.info("Cancelando venda ID: {}", vendaId);
                    CancelarVendaInput input = new CancelarVendaInput(vendaId);
                    VendaOutput vendaCancelada = vendaService.cancelarVenda(input);
                    JOptionPane.showMessageDialog(vendaFrame, "Venda '" + vendaCancelada.id() + "' cancelada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    listarTodasVendas();
                } catch (DomainException e) {
                    logger.error("Erro de domínio ao cancelar venda: {}", e.getMessage());
                    JOptionPane.showMessageDialog(vendaFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    logger.error("Erro inesperado ao cancelar venda", e);
                    JOptionPane.showMessageDialog(vendaFrame, "Erro inesperado ao cancelar venda.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(vendaFrame, "Nenhuma venda selecionada para cancelar.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void aplicarDescontoVendaSelecionada() {
        int selectedRow = vendaFrame.getVendasTable().getSelectedRow();
        if (selectedRow != -1) {
            String vendaId = (String) vendaFrame.getVendasTableModel().getValueAt(selectedRow, 0);
            BigDecimal totalBruto = (BigDecimal) vendaFrame.getVendasTableModel().getValueAt(selectedRow, 3); // Coluna do Total Bruto
            String statusAtual = (String) vendaFrame.getVendasTableModel().getValueAt(selectedRow, 6); // Coluna do Status

            if ("CANCELADA".equalsIgnoreCase(statusAtual) || "CONCLUIDA".equalsIgnoreCase(statusAtual)) {
                JOptionPane.showMessageDialog(vendaFrame, "Não é possível aplicar desconto em vendas canceladas ou já concluídas.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            logger.info("Abrindo diálogo para aplicar desconto na venda ID: {}", vendaId);
            String[] formData = vendaFrame.showAplicarDescontoDialog(vendaId, totalBruto.toPlainString()); // Passa o total bruto como String

            if (formData != null) {
                try {
                    BigDecimal valorDesconto = new BigDecimal(formData[0]);

                    AplicarDescontoVendaInput input = new AplicarDescontoVendaInput(vendaId, valorDesconto);
                    VendaOutput vendaAtualizada = vendaService.aplicarDescontoVenda(input);
                    JOptionPane.showMessageDialog(vendaFrame, "Desconto de R$" + vendaAtualizada.valorDesconto() + " aplicado na venda ID: " + vendaAtualizada.id() + " com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    listarTodasVendas();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(vendaFrame, "Valor do desconto inválido. Use um formato numérico válido (ex: 10.50).", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                } catch (DomainException e) {
                    logger.error("Erro de domínio ao aplicar desconto: {}", e.getMessage());
                    JOptionPane.showMessageDialog(vendaFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    logger.error("Erro inesperado ao aplicar desconto", e);
                    JOptionPane.showMessageDialog(vendaFrame, "Erro inesperado ao aplicar desconto.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                logger.info("Aplicação de desconto cancelada.");
            }
        } else {
            JOptionPane.showMessageDialog(vendaFrame, "Nenhuma venda selecionada para aplicar desconto.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }
}