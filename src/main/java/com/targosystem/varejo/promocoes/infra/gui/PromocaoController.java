package com.targosystem.varejo.promocoes.infra.gui;

import com.targosystem.varejo.promocoes.application.PromocaoService;
import com.targosystem.varejo.promocoes.application.output.PromocaoOutput;
import com.targosystem.varejo.shared.domain.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Objects;

public class PromocaoController {

    private static final Logger logger = LoggerFactory.getLogger(PromocaoController.class);

    private final PromocaoService promocaoService;
    private final PromocaoFrame promocaoFrame;

    public PromocaoController(PromocaoService promocaoService, PromocaoFrame promocaoFrame) {
        this.promocaoService = Objects.requireNonNull(promocaoService, "PromocaoService cannot be null.");
        this.promocaoFrame = Objects.requireNonNull(promocaoFrame, "PromocaoFrame cannot be null.");

        this.promocaoFrame.getBtnListarPromocoes().addActionListener(e -> listarPromocoesAtivas());
        this.promocaoFrame.getBtnNovaPromocao().addActionListener(e -> criarNovaPromocao());
        this.promocaoFrame.getBtnEditarPromocao().addActionListener(e -> editarPromocaoSelecionada());

        this.promocaoFrame.getPromocoesTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = promocaoFrame.getPromocoesTable().getSelectedRow() != -1;
                promocaoFrame.getBtnEditarPromocao().setEnabled(hasSelection);
            }
        });
        promocaoFrame.getBtnEditarPromocao().setEnabled(false);
    }

    public void listarPromocoesAtivas() {
        logger.info("Listando promoções ativas...");
        promocaoFrame.clearTable();

        try {
            List<PromocaoOutput> promocoes = promocaoService.listarPromocoesAtivas();
            DefaultTableModel model = promocaoFrame.getTableModel();
            for (PromocaoOutput promocao : promocoes) {
                model.addRow(new Object[]{
                        promocao.id(),
                        promocao.nome(),
                        promocao.tipoDesconto(),
                        promocao.valorDesconto().toPlainString(),
                        promocao.ativa() ? "Sim" : "Não",
                        promocao.dataInicio(),
                        promocao.dataFim()
                });
            }
            logger.info("Total de {} promoções ativas carregadas.", promocoes.size());
        } catch (DomainException e) {
            logger.error("Erro de domínio ao listar promoções: {}", e.getMessage());
            JOptionPane.showMessageDialog(promocaoFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao listar promoções", e);
            JOptionPane.showMessageDialog(promocaoFrame, "Erro inesperado ao listar promoções.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarNovaPromocao() {
        logger.info("Abrindo diálogo para criar nova promoção...");
        JOptionPane.showMessageDialog(promocaoFrame, "Funcionalidade 'Nova Promoção' em desenvolvimento!", "Aguarde", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editarPromocaoSelecionada() {
        int selectedRow = promocaoFrame.getPromocoesTable().getSelectedRow();
        if (selectedRow != -1) {
            String promocaoId = (String) promocaoFrame.getTableModel().getValueAt(selectedRow, 0);
            logger.info("Abrindo diálogo para editar promoção ID: {}", promocaoId);
            JOptionPane.showMessageDialog(promocaoFrame, "Funcionalidade 'Editar Promoção' em desenvolvimento para ID: " + promocaoId, "Aguarde", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(promocaoFrame, "Nenhuma promoção selecionada para edição.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }
}