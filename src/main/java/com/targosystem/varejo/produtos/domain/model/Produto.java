package com.targosystem.varejo.produtos.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.domain.Price;
import java.time.LocalDateTime;
import java.util.Objects;

public class Produto {
    private final ProdutoId id; // ID gerado pelo domínio
    private String nome;
    private String descricao;
    private String codigoBarras;
    private Categoria categoria; // Value Object ou Entidade do próprio BC
    private String marca;
    private Price precoSugerido;
    private boolean ativo;
    private final LocalDateTime dataCadastro;
    private LocalDateTime ultimaAtualizacao;

    // Construtor para criar um novo produto (sem ID inicial, será gerado)
    public Produto(String nome, String descricao, String codigoBarras, Categoria categoria, String marca, Price precoSugerido) {
        this.id = ProdutoId.generate();
        setNome(nome);
        setDescricao(descricao);
        setCodigoBarras(codigoBarras);
        setCategoria(categoria);
        setMarca(marca);
        setPrecoSugerido(precoSugerido);
        this.ativo = true; // Por padrão, um novo produto é ativo
        this.dataCadastro = LocalDateTime.now();
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    // Construtor para recarregar um produto existente do repositório
    public Produto(ProdutoId id, String nome, String descricao, String codigoBarras, Categoria categoria, String marca, Price precoSugerido, boolean ativo, LocalDateTime dataCadastro, LocalDateTime ultimaAtualizacao) {
        Objects.requireNonNull(id, "Product ID cannot be null for existing product");
        this.id = id;
        setNome(nome);
        setDescricao(descricao);
        setCodigoBarras(codigoBarras);
        setCategoria(categoria);
        setMarca(marca);
        setPrecoSugerido(precoSugerido);
        this.ativo = ativo;
        Objects.requireNonNull(dataCadastro, "Data de cadastro cannot be null");
        this.dataCadastro = dataCadastro;
        Objects.requireNonNull(ultimaAtualizacao, "Última atualização cannot be null");
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    // Métodos de comportamento (Regras de Negócio)
    public void atualizarInformacoes(String novoNome, String novaDescricao, String novoCodigoBarras, Categoria novaCategoria, String novaMarca, Price novoPrecoSugerido) {
        setNome(novoNome);
        setDescricao(novaDescricao);
        setCodigoBarras(novoCodigoBarras);
        setCategoria(novaCategoria);
        setMarca(novaMarca);
        setPrecoSugerido(novoPrecoSugerido);
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void ativar() {
        this.ativo = true;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void inativar() {
        this.ativo = false;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    // Getters
    public ProdutoId getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public String getMarca() {
        return marca;
    }

    public Price getPrecoSugerido() {
        return precoSugerido;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public LocalDateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    // Setters com validações de domínio (encapsulamento de regras)
    private void setNome(String nome) {
        Objects.requireNonNull(nome, "Product name cannot be null");
        if (nome.isBlank()) {
            throw new DomainException("Product name cannot be empty");
        }
        this.nome = nome;
    }

    private void setDescricao(String descricao) {
        this.descricao = descricao; // Descrição pode ser null/empty
    }

    private void setCodigoBarras(String codigoBarras) {
        Objects.requireNonNull(codigoBarras, "Barcode cannot be null");
        if (codigoBarras.isBlank()) {
            throw new DomainException("Barcode cannot be empty");
        }
        // Poderia ter mais validações de formato de código de barras
        this.codigoBarras = codigoBarras;
    }

    private void setCategoria(Categoria categoria) {
        Objects.requireNonNull(categoria, "Category cannot be null");
        this.categoria = categoria;
    }

    private void setMarca(String marca) {
        this.marca = marca; // Marca pode ser null/empty
    }

    private void setPrecoSugerido(Price precoSugerido) {
        Objects.requireNonNull(precoSugerido, "Suggested price cannot be null");
        // Validação de Price já ocorre no Value Object Price
        this.precoSugerido = precoSugerido;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return id.equals(produto.id); // Produtos são identificados por seu ID único
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}