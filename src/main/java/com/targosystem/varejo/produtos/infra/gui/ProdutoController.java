package com.targosystem.varejo.produtos.infra.gui;

import com.targosystem.varejo.produtos.application.ProdutoService;
import com.targosystem.varejo.produtos.application.input.AtualizarProdutoInput;
import com.targosystem.varejo.produtos.application.input.CadastrarProdutoInput;
import com.targosystem.varejo.produtos.application.output.CategoriaOutput;
import com.targosystem.varejo.produtos.application.output.ProdutoOutput;
import com.targosystem.varejo.produtos.domain.model.ProdutoId;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.domain.Price;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        // *** Novo: Popula o ComboBox de categorias ao iniciar o controller ***
        popularCategoriasComboBox();
        // Também liste os produtos ao iniciar a tela
        listarProdutos();
    }

    // *** Novo método para popular o JComboBox de categorias ***
    private void popularCategoriasComboBox() {
        try {
            // Chama o ProdutoService para obter a lista de CategoriaOutput
            List<CategoriaOutput> categoriasOutput = produtoService.listarTodasCategorias();
            // Mapeia para uma lista de Strings (nomes das categorias)
            List<String> nomesDasCategorias = categoriasOutput.stream()
                    .map(CategoriaOutput::nome)
                    .collect(Collectors.toList());
            // Passa a lista de Strings para o ProdutoPanel
            produtoPanel.setCategorias(nomesDasCategorias);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(produtoPanel,
                    "Erro ao carregar categorias: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cadastrarProduto() {
        try {
            String nome = produtoPanel.getProdutoNome();
            String descricao = produtoPanel.getProdutoDescricao();
            String codigoBarras = produtoPanel.getProdutoCodigoBarras();
            String nomeCategoria = produtoPanel.getProdutoCategoria(); // Obtém a categoria do ComboBox
            String marca = produtoPanel.getProdutoMarca();
            BigDecimal precoValue = BigDecimal.valueOf(produtoPanel.getProdutoPrecoAsDouble());

            CadastrarProdutoInput input = new CadastrarProdutoInput(
                    nome, descricao, codigoBarras, nomeCategoria, null, marca, Price.of(precoValue)
            );

            ProdutoOutput produtoOutput = produtoService.cadastrarProduto(input);
            JOptionPane.showMessageDialog(produtoPanel, "Produto cadastrado com sucesso: " + produtoOutput.nome(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            produtoPanel.limparCampos();
            listarProdutos(); // Atualiza a lista após o cadastro
            popularCategoriasComboBox(); // Recarrega categorias, caso uma nova tenha sido digitada e salva
        } catch (DomainException e) {
            JOptionPane.showMessageDialog(produtoPanel, "Erro de negócio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(produtoPanel, "Preço inválido. Por favor, insira um número válido: " + e.getMessage(), "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
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
            String nomeCategoria = produtoPanel.getProdutoCategoria(); // Obtém a categoria do ComboBox
            String marca = produtoPanel.getProdutoMarca();
            BigDecimal precoValue = new BigDecimal(produtoPanel.getProdutoPreco());

            AtualizarProdutoInput input = new AtualizarProdutoInput(
                    ProdutoId.from(id), // Converter String para ProdutoId
                    nome, descricao, codigoBarras, nomeCategoria, null, marca, Price.of(precoValue)
            );

            ProdutoOutput produtoOutput = produtoService.atualizarProduto(input);
            JOptionPane.showMessageDialog(produtoPanel, "Produto atualizado com sucesso: " + produtoOutput.nome(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            produtoPanel.limparCampos();
            listarProdutos(); // Atualiza a lista
            popularCategoriasComboBox(); // Recarrega categorias, caso uma nova tenha sido digitada e salva
        } catch (DomainException e) {
            JOptionPane.showMessageDialog(produtoPanel, "Erro de negócio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(produtoPanel, "Preço inválido. Por favor, insira um número válido: " + e.getMessage(), "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
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

    public void carregarProdutoParaEdicao(String produtoId) {
        try {
            ProdutoOutput produto = produtoService.obterProdutoPorId(produtoId);
            produtoPanel.preencherCampos(
                    produto.id(),
                    produto.nome(),
                    produto.descricao(),
                    produto.codigoBarras(),
                    produto.categoriaNome(), // Este valor irá para o JComboBox da categoria
                    produto.marca(),
                    produto.precoSugerido().toString()
            );
        } catch (DomainException e) {
            JOptionPane.showMessageDialog(produtoPanel, "Erro ao carregar produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}