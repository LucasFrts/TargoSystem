package com.targosystem.varejo.estoque.infra.gui;

import com.targosystem.varejo.estoque.application.EstoqueService;
import com.targosystem.varejo.estoque.application.input.RegistrarMovimentacaoEstoqueInput;
import com.targosystem.varejo.estoque.application.output.EstoqueOutput;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.estoque.domain.model.TipoMovimentacao; // Import TipoMovimentacao
import com.targosystem.varejo.estoque.application.input.LocalizacaoArmazenamentoInput; // Import LocalizacaoArmazenamentoInput
import com.targosystem.varejo.estoque.application.input.LoteInput; // Import LoteInput if needed, for now will be null

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Objects;

public class EstoqueController {

    private static final Logger logger = LoggerFactory.getLogger(EstoqueController.class);

    private final EstoqueService estoqueService;
    private final EstoqueFrame estoqueFrame;

    public EstoqueController(EstoqueService estoqueService, EstoqueFrame estoqueFrame) {
        this.estoqueService = Objects.requireNonNull(estoqueService, "EstoqueService cannot be null.");
        this.estoqueFrame = Objects.requireNonNull(estoqueFrame, "EstoqueFrame cannot be null.");

        // Adicionar listeners
        this.estoqueFrame.getBtnConsultarEstoque().addActionListener(e -> consultarEstoque());
        this.estoqueFrame.getBtnRegistrarMovimentacao().addActionListener(e -> registrarMovimentacao());
    }

    public void consultarEstoque() {
        String produtoId = estoqueFrame.getTxtProdutoIdConsulta().getText().trim();
        estoqueFrame.getTxtAreaResultadoConsulta().setText(""); // Limpa resultado anterior

        if (produtoId.isEmpty()) {
            JOptionPane.showMessageDialog(estoqueFrame, "Por favor, insira o ID do produto para consultar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            logger.info("Consultando estoque para Produto ID: {}", produtoId);
            EstoqueOutput estoque = estoqueService.consultarEstoquePorProdutoId(produtoId);
            if (estoque != null) {
                String result = String.format("Estoque do Produto '%s':\n" +
                                "Quantidade Total Disponível: %d",
                        estoque.produtoId(),
                        estoque.quantidadeTotalDisponivel());

                estoqueFrame.getTxtAreaResultadoConsulta().setText(result);
                logger.info("Consulta de estoque para produto {} concluída com sucesso. Qtd: {}", produtoId, estoque.quantidadeTotalDisponivel());
            } else {
                estoqueFrame.getTxtAreaResultadoConsulta().setText("Estoque não encontrado para o Produto ID: " + produtoId);
                logger.warn("Estoque não encontrado para Produto ID: {}", produtoId);
            }
        } catch (DomainException e) {
            logger.error("Erro de domínio ao consultar estoque: {}", e.getMessage());
            JOptionPane.showMessageDialog(estoqueFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao consultar estoque para Produto ID: {}", produtoId, e);
            JOptionPane.showMessageDialog(estoqueFrame, "Erro inesperado ao consultar estoque.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void registrarMovimentacao() {
        String produtoId = estoqueFrame.getTxtProdutoIdMovimentacao().getText().trim();
        String quantidadeStr = estoqueFrame.getTxtQuantidadeMovimentacao().getText().trim();
        String tipoMovimentacaoStr = (String) estoqueFrame.getCmbTipoMovimentacao().getSelectedItem();
        String localOrigemStr = estoqueFrame.getTxtLocalOrigem().getText().trim();
        String localDestinoStr = estoqueFrame.getTxtLocalDestino().getText().trim();

        if (produtoId.isEmpty() || quantidadeStr.isEmpty()) {
            JOptionPane.showMessageDialog(estoqueFrame, "ID do Produto e Quantidade são campos obrigatórios.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeStr);
            if (quantidade <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(estoqueFrame, "Quantidade deve ser um número inteiro positivo.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TipoMovimentacao tipoMovimentacao;
        try {
            tipoMovimentacao = TipoMovimentacao.valueOf(tipoMovimentacaoStr);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(estoqueFrame, "Tipo de movimentação inválido.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalizacaoArmazenamentoInput localizacao = null;
        String motivo = "Movimentação Manual GUI"; // Default motivo

        if (tipoMovimentacao == TipoMovimentacao.TRANSFERENCIA) {
            if (localOrigemStr.isEmpty() || localDestinoStr.isEmpty()) {
                JOptionPane.showMessageDialog(estoqueFrame, "Para TRANSFERENCIA, Local Origem e Local Destino são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // ASSUMPTION: Mapping localDestino to 'corredor' for LocalizacaoArmazenamentoInput.
            // This needs refinement if your UI intends to capture all 'corredor', 'prateleira', 'nivel'.
            localizacao = new LocalizacaoArmazenamentoInput(localDestinoStr, null, null);
            motivo = "Transferência GUI de " + localOrigemStr + " para " + localDestinoStr;
        } else if (tipoMovimentacao == TipoMovimentacao.ENTRADA) {
            // For ENTRADA, you might want to specify a default location or capture it
            // For simplicity, we'll make localizacao null if not explicitly captured in UI for non-transfers
            // If you add a "local de entrada" field to the UI, you'd use it here
            localizacao = null; // Assuming no specific location captured in UI for simple entry
            // Lote input would also be handled here
        } else if (tipoMovimentacao == TipoMovimentacao.SAIDA) {
            localizacao = null; // Assuming no specific location captured in UI for simple exit
        }


        // LoteInput for now will be null as it's not captured in the current GUI
        LoteInput loteInput = null;

        try {
            RegistrarMovimentacaoEstoqueInput input = new RegistrarMovimentacaoEstoqueInput(
                    produtoId,
                    quantidade,
                    tipoMovimentacao,
                    motivo,
                    loteInput, // Pass null if not used
                    localizacao // Pass null if not used or a partially filled record
            );

            logger.info("Registrando movimentação de estoque: Produto ID {}, Tipo: {}, Quantidade: {}", produtoId, tipoMovimentacao, quantidade);
            EstoqueOutput estoqueAtualizado = estoqueService.registrarMovimentacao(input);

            JOptionPane.showMessageDialog(estoqueFrame,
                    String.format("Movimentação registrada com sucesso para Produto ID: %s\n" +
                                    "Nova Quantidade Total Disponível: %d",
                            estoqueAtualizado.produtoId(), estoqueAtualizado.quantidadeTotalDisponivel()),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            logger.info("Movimentação de estoque para produto {} registrada com sucesso. Nova Qtd: {}", produtoId, estoqueAtualizado.quantidadeTotalDisponivel());

            estoqueFrame.clearMovimentacaoFields();
            estoqueFrame.getTxtProdutoIdConsulta().setText(produtoId);
            consultarEstoque();
        } catch (DomainException e) {
            logger.error("Erro de domínio ao registrar movimentação: {}", e.getMessage());
            JOptionPane.showMessageDialog(estoqueFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao registrar movimentação para Produto ID: {}", produtoId, e);
            JOptionPane.showMessageDialog(estoqueFrame, "Erro inesperado ao registrar movimentação.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}