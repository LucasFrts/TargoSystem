package com.targosystem.varejo.produtos.infra.gui;

import com.targosystem.varejo.produtos.application.ProdutoService;
import com.targosystem.varejo.produtos.application.input.AtualizarProdutoInput;
import com.targosystem.varejo.produtos.application.input.CadastrarProdutoInput;
import com.targosystem.varejo.produtos.application.output.ProdutoOutput;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.domain.Price;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ProdutoController {

    private final ProdutoService produtoService;
    private final ProdutoPanel produtoPanel; // A View real do Swing

    public ProdutoController(ProdutoService produtoService, ProdutoPanel produtoPanel) {
        this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService cannot be null");
        this.produtoPanel = Objects.requireNonNull(produtoPanel, "ProdutoPanel cannot be null");

        // Associa as ações da UI aos métodos do controller
        produtoPanel.setCadastrarButtonAction(e -> cadastrarProduto());
        produtoPanel.setAtualizarButtonAction(e -> atualizarProduto());
        produtoPanel.setListarButtonAction(e -> listarProdutos());
        // ... outras ações
    }

    private void cadastrarProduto() {
        try {
            // Obter dados da UI
            String nome = produtoPanel.getProdutoNome();
            String descricao = produtoPanel.getProdutoDescricao();
            String codigoBarras = produtoPanel.getProdutoCodigoBarras();
            String nomeCategoria = produtoPanel.getProdutoCategoria();
            String marca = produtoPanel.getProdutoMarca();
            BigDecimal precoValue = new BigDecimal(produtoPanel.getProdutoPreco());

            CadastrarProdutoInput input = new CadastrarProdutoInput(
                    nome, descricao, codigoBarras, nomeCategoria, null, marca, Price.of(precoValue)
            );

            ProdutoOutput produtoOutput = produtoService.cadastrarProduto(input);
            JOptionPane.showMessageDialog(produtoPanel, "Produto cadastrado com sucesso: " + produtoOutput.nome(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            produtoPanel.limparCampos();
            listarProdutos(); // Atualiza a lista após o cadastro
        } catch (DomainException e) {
            JOptionPane.showMessageDialog(produtoPanel, "Erro de negócio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(produtoPanel, "Erro de formato numérico: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(produtoPanel, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void atualizarProduto() {
        try {
            // Obter dados da UI, incluindo o ID do produto selecionado
            String id = produtoPanel.getProdutoId(); // Assumindo que o ID é visível ou selecionável na UI
            if (id == null || id.isBlank()) {
                JOptionPane.showMessageDialog(produtoPanel, "Selecione um produto para atualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nome = produtoPanel.getProdutoNome();
            String descricao = produtoPanel.getProdutoDescricao();
            String codigoBarras = produtoPanel.getProdutoCodigoBarras();
            String nomeCategoria = produtoPanel.getProdutoCategoria();
            String marca = produtoPanel.getProdutoMarca();
            BigDecimal precoValue = new BigDecimal(produtoPanel.getProdutoPreco());

            AtualizarProdutoInput input = new AtualizarProdutoInput(
                    com.targosystem.varejo.produtos.domain.model.ProdutoId.from(id),
                    nome, descricao, codigoBarras, nomeCategoria, null, marca, Price.of(precoValue)
            );

            ProdutoOutput produtoOutput = produtoService.atualizarProduto(input);
            JOptionPane.showMessageDialog(produtoPanel, "Produto atualizado com sucesso: " + produtoOutput.nome(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            produtoPanel.limparCampos();
            listarProdutos(); // Atualiza a lista
        } catch (DomainException e) {
            JOptionPane.showMessageDialog(produtoPanel, "Erro de negócio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(produtoPanel, "Erro de formato numérico: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(produtoPanel, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void listarProdutos() {
        try {
            List<ProdutoOutput> produtos = produtoService.listarTodosProdutos();
            produtoPanel.exibirProdutos(produtos); // Método na view para exibir a lista
        } catch (Exception e) {
            JOptionPane.showMessageDialog(produtoPanel, "Erro ao listar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Método para carregar um produto selecionado na UI para edição
    public void carregarProdutoParaEdicao(String produtoId) {
        try {
            ProdutoOutput produto = produtoService.obterProdutoPorId(produtoId);
            produtoPanel.preencherCampos(
                    produto.id(),
                    produto.nome(),
                    produto.descricao(),
                    produto.codigoBarras(),
                    produto.categoriaNome(),
                    produto.marca(),
                    produto.precoSugerido()
            );
        } catch (DomainException e) {
            JOptionPane.showMessageDialog(produtoPanel, "Erro ao carregar produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ... outros métodos para outros casos de uso (ativar/inativar, etc.)
}