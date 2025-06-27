package com.targosystem.varejo.promocoes.infra.gui;

import com.targosystem.varejo.promocoes.application.PromocaoService;
import com.targosystem.varejo.promocoes.application.input.AtualizarPromocaoInput;
import com.targosystem.varejo.promocoes.application.input.CriarKitPromocionalInput;
import com.targosystem.varejo.promocoes.application.input.CriarPromocaoInput;
import com.targosystem.varejo.promocoes.application.output.KitPromocionalOutput;
import com.targosystem.varejo.promocoes.application.output.PromocaoOutput;
import com.targosystem.varejo.produtos.application.ProdutoService;
import com.targosystem.varejo.produtos.application.output.ProdutoOutput;
import com.targosystem.varejo.shared.domain.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*; // Importa java.awt.Frame
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PromocaoController {

    private static final Logger logger = LoggerFactory.getLogger(PromocaoController.class);

    private final PromocaoService promocaoService;
    private final ProdutoService produtoService;
    private final PromocaoFrame promocaoFrame;

    public PromocaoController(PromocaoService promocaoService, ProdutoService produtoService, PromocaoFrame promocaoFrame) {
        this.promocaoService = Objects.requireNonNull(promocaoService, "PromocaoService cannot be null.");
        this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService cannot be null.");
        this.promocaoFrame = Objects.requireNonNull(promocaoFrame, "PromocaoFrame cannot be null.");

        // Listeners para Promoções
        this.promocaoFrame.getBtnListarPromocoes().addActionListener(e -> listarPromocoesAtivas());
        this.promocaoFrame.getBtnNovaPromocao().addActionListener(e -> criarNovaPromocao());
        this.promocaoFrame.getBtnEditarPromocao().addActionListener(e -> editarPromocaoSelecionada());
        this.promocaoFrame.getBtnExcluirPromocao().addActionListener(e -> excluirPromocaoSelecionada());

        // Listeners para Kits Promocionais
        this.promocaoFrame.getBtnListarKits().addActionListener(e -> listarTodosKits());
        this.promocaoFrame.getBtnNovoKit().addActionListener(e -> criarNovoKitPromocional());
        this.promocaoFrame.getBtnEditarKit().addActionListener(e -> editarKitPromocionalSelecionado());
        this.promocaoFrame.getBtnExcluirKit().addActionListener(e -> excluirKitPromocionalSelecionado());

        // Configuração inicial de botões (habilitar/desabilitar)
        this.promocaoFrame.getPromocoesTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = promocaoFrame.getPromocoesTable().getSelectedRow() != -1;
                promocaoFrame.getBtnEditarPromocao().setEnabled(hasSelection);
                promocaoFrame.getBtnExcluirPromocao().setEnabled(hasSelection);
            }
        });
        this.promocaoFrame.getKitsTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = promocaoFrame.getKitsTable().getSelectedRow() != -1;
                promocaoFrame.getBtnEditarKit().setEnabled(hasSelection);
                promocaoFrame.getBtnExcluirKit().setEnabled(hasSelection);
            }
        });

        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        listarPromocoesAtivas();
        listarTodosKits();
    }

    // --- Métodos para Promoções ---
    public void listarPromocoesAtivas() {
        logger.info("Listando promoções ativas...");
        promocaoFrame.clearPromocoesTable();

        try {
            List<PromocaoOutput> promocoes = promocaoService.listarPromocoesAtivas();
            DefaultTableModel model = promocaoFrame.getPromocoesTableModel();
            for (PromocaoOutput promocao : promocoes) {
                model.addRow(new Object[]{
                        promocao.id(),
                        promocao.nome(),
                        promocao.tipoDesconto().name(),
                        promocao.valorDesconto().toPlainString(),
                        promocao.ativa() ? "Sim" : "Não",
                        promocao.dataInicio().format(PromocaoDialog.DATE_TIME_FORMATTER),
                        promocao.dataFim().format(PromocaoDialog.DATE_TIME_FORMATTER)
                });
            }
            logger.info("Total de {} promoções ativas carregadas.", promocoes.size());
        } catch (DomainException e) {
            logger.error("Erro de domínio ao listar promoções: {}", e.getMessage());
            JOptionPane.showMessageDialog(promocaoFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao listar promoções", e);
            JOptionPane.showMessageDialog(promocaoFrame, "Erro inesperado ao listar promoções: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarNovaPromocao() {
        logger.info("Abrindo diálogo para criar nova promoção...");
        try {
            // Obter a lista de todos os produtos disponíveis antes de abrir o diálogo
            List<ProdutoOutput> todosProdutosDisponiveis = produtoService.listarTodosProdutos();

            Frame ownerFrame = SwingUtilities.getWindowAncestor(promocaoFrame) instanceof JFrame ? (JFrame) SwingUtilities.getWindowAncestor(promocaoFrame) : null;
            // Passar a lista de produtos para o construtor
            PromocaoDialog dialog = new PromocaoDialog(ownerFrame, todosProdutosDisponiveis);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                CriarPromocaoInput input = dialog.getCriarPromocaoInput();
                promocaoService.criarPromocao(input);
                JOptionPane.showMessageDialog(promocaoFrame, "Promoção criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                listarPromocoesAtivas();
            }
        } catch (DomainException e) {
            logger.error("Erro de domínio ao criar promoção: {}", e.getMessage());
            JOptionPane.showMessageDialog(promocaoFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar promoção", e);
            JOptionPane.showMessageDialog(promocaoFrame, "Erro inesperado ao criar promoção: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarPromocaoSelecionada() {
        int selectedRow = promocaoFrame.getPromocoesTable().getSelectedRow();
        if (selectedRow != -1) {
            String promocaoId = (String) promocaoFrame.getPromocoesTableModel().getValueAt(selectedRow, 0);
            logger.info("Abrindo diálogo para editar promoção ID: {}", promocaoId);

            try {
                PromocaoOutput promocaoExistente = promocaoService.obterPromocaoPorId(promocaoId);
                // Obter a lista de todos os produtos disponíveis antes de abrir o diálogo
                List<ProdutoOutput> todosProdutosDisponiveis = produtoService.listarTodosProdutos();

                Frame ownerFrame = SwingUtilities.getWindowAncestor(promocaoFrame) instanceof JFrame ? (JFrame) SwingUtilities.getWindowAncestor(promocaoFrame) : null;
                // Passar a promoção existente E a lista de produtos para o construtor
                PromocaoDialog dialog = new PromocaoDialog(ownerFrame, promocaoExistente, todosProdutosDisponiveis);
                dialog.setVisible(true);

                if (dialog.isSaved()) {
                    AtualizarPromocaoInput input = dialog.getAtualizarPromocaoInput();
                    promocaoService.atualizarPromocao(input);
                    JOptionPane.showMessageDialog(promocaoFrame, "Promoção atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    listarPromocoesAtivas();
                }
            } catch (DomainException e) {
                logger.error("Erro de domínio ao editar promoção: {}", e.getMessage());
                JOptionPane.showMessageDialog(promocaoFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.error("Erro inesperado ao editar promoção", e);
                JOptionPane.showMessageDialog(promocaoFrame, "Erro inesperado ao editar promoção: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(promocaoFrame, "Nenhuma promoção selecionada para edição.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirPromocaoSelecionada() {
        int selectedRow = promocaoFrame.getPromocoesTable().getSelectedRow();
        if (selectedRow != -1) {
            String promocaoId = (String) promocaoFrame.getPromocoesTableModel().getValueAt(selectedRow, 0);
            String promocaoNome = (String) promocaoFrame.getPromocoesTableModel().getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(promocaoFrame,
                    "Tem certeza que deseja excluir a promoção '" + promocaoNome + "' (ID: " + promocaoId + ")?",
                    "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // CUIDADO: Você não tem um método delete no PromocaoService ainda.
                    // public void excluirPromocao(String id) { promocaoRepository.delete(id); }
                    // E um Use Case se necessário.
                    promocaoService.excluirPromocao(promocaoId); // Você precisará implementar isso
                    JOptionPane.showMessageDialog(promocaoFrame, "Promoção excluida com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    listarPromocoesAtivas();
                } catch (DomainException e) {
                    logger.error("Erro de domínio ao excluir promoção: {}", e.getMessage());
                    JOptionPane.showMessageDialog(promocaoFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    logger.error("Erro inesperado ao excluir promoção", e);
                    JOptionPane.showMessageDialog(promocaoFrame, "Erro inesperado ao excluir promoção: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(promocaoFrame, "Nenhuma promoção selecionada para exclusão.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- Métodos para Kits Promocionais ---
    public void listarTodosKits() {
        logger.info("Listando todos os kits promocionais...");
        promocaoFrame.clearKitsTable();

        try {
            // Este método devera chamar um usecase/query que constroi o KitPromocionalOutput
            // já com o nome do produto. O PromocaoService precisará de um método assim.
            List<KitPromocionalOutput> kits = promocaoService.listarTodosKitsComDetalhesProduto(); // NOVO: Assumindo este método
            DefaultTableModel model = promocaoFrame.getKitsTableModel();
            for (KitPromocionalOutput kit : kits) {
                // Converta a lista de itens para uma string legível para a tabela
                String itensString = kit.itens().stream()
                        // CORREÇÃO: Usando item.nomeProduto() que foi adicionado ao ItemKitOutput
                        .map(item -> item.nomeProduto() + " (x" + item.quantidade() + ")")
                        .collect(Collectors.joining(", "));
                model.addRow(new Object[]{
                        kit.id(),
                        kit.nome(),
                        kit.descricao(),
                        kit.precoFixoKit().toPlainString(),
                        itensString
                });
            }
            logger.info("Total de {} kits promocionais carregados.", kits.size());
        } catch (DomainException e) {
            logger.error("Erro de domínio ao listar kits: {}", e.getMessage());
            JOptionPane.showMessageDialog(promocaoFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao listar kits", e);
            JOptionPane.showMessageDialog(promocaoFrame, "Erro inesperado ao listar kits: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarNovoKitPromocional() {
        logger.info("Abrindo diálogo para criar novo kit promocional...");
        try {
            List<ProdutoOutput> todosProdutos = produtoService.listarTodosProdutos();
            Frame ownerFrame = SwingUtilities.getWindowAncestor(promocaoFrame) instanceof JFrame ? (JFrame) SwingUtilities.getWindowAncestor(promocaoFrame) : null;
            KitPromocionalDialog dialog = new KitPromocionalDialog(ownerFrame, todosProdutos);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                CriarKitPromocionalInput input = dialog.getCriarKitPromocionalInput();
                promocaoService.criarKitPromocional(input);
                JOptionPane.showMessageDialog(promocaoFrame, "Kit promocional criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                listarTodosKits();
            }
        } catch (DomainException e) {
            logger.error("Erro de domínio ao criar kit: {}", e.getMessage());
            JOptionPane.showMessageDialog(promocaoFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar kit", e);
            JOptionPane.showMessageDialog(promocaoFrame, "Erro inesperado ao criar kit: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarKitPromocionalSelecionado() {
        int selectedRow = promocaoFrame.getKitsTable().getSelectedRow();
        if (selectedRow != -1) {
            String kitId = (String) promocaoFrame.getKitsTableModel().getValueAt(selectedRow, 0);
            logger.info("Abrindo diálogo para editar kit promocional ID: {}", kitId);

            try {
                // Ao obter o KitPromocionalOutput para edição, ele também precisará vir com os nomes dos produtos
                KitPromocionalOutput kitExistente = promocaoService.obterKitPromocionalPorIdComDetalhesProduto(kitId); // NOVO: Assumindo este método
                List<ProdutoOutput> todosProdutos = produtoService.listarTodosProdutos();
                Frame ownerFrame = SwingUtilities.getWindowAncestor(promocaoFrame) instanceof JFrame ? (JFrame) SwingUtilities.getWindowAncestor(promocaoFrame) : null;
                KitPromocionalDialog dialog = new KitPromocionalDialog(ownerFrame, kitExistente, todosProdutos);
                dialog.setVisible(true);

                if (dialog.isSaved()) {
                    // Você precisará de um input e use case para atualizar kits.
                    // AtualizarKitPromocionalInput input = dialog.getAtualizarKitPromocionalInput(); // O dialog precisaria fornecer isso
                    // promocaoService.atualizarKitPromocional(input); // Você precisará implementar isso
                    JOptionPane.showMessageDialog(promocaoFrame, "Funcionalidade de edição de Kit em desenvolvimento.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    // listarTodosKits();
                }
            } catch (DomainException e) {
                logger.error("Erro de domínio ao editar kit: {}", e.getMessage());
                JOptionPane.showMessageDialog(promocaoFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.error("Erro inesperado ao editar kit", e);
                JOptionPane.showMessageDialog(promocaoFrame, "Erro inesperado ao editar kit: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(promocaoFrame, "Nenhum kit selecionado para edição.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirKitPromocionalSelecionado() {
        int selectedRow = promocaoFrame.getKitsTable().getSelectedRow();
        if (selectedRow != -1) {
            String kitId = (String) promocaoFrame.getKitsTableModel().getValueAt(selectedRow, 0);
            String kitNome = (String) promocaoFrame.getKitsTableModel().getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(promocaoFrame,
                    "Tem certeza que deseja excluir o kit '" + kitNome + "' (ID: " + kitId + ")?",
                    "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Você precisará de um Use Case e método no PromocaoService para excluir kits.
                    // promocaoService.excluirKitPromocional(kitId); // Você precisará implementar isso
                    JOptionPane.showMessageDialog(promocaoFrame, "Funcionalidade de exclusão de Kit em desenvolvimento.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    // listarTodosKits();
                } catch (DomainException e) {
                    logger.error("Erro de domínio ao excluir kit: {}", e.getMessage());
                    JOptionPane.showMessageDialog(promocaoFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    logger.error("Erro inesperado ao excluir kit", e);
                    JOptionPane.showMessageDialog(promocaoFrame, "Erro inesperado ao excluir kit: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(promocaoFrame, "Nenhum kit selecionado para exclusão.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }
}