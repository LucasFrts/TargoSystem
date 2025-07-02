package com.targosystem.varejo.produtos.domain.model;

import com.targosystem.varejo.shared.domain.AggregateRoot;
import com.targosystem.varejo.shared.domain.DomainException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Produto implements AggregateRoot {
    private ProdutoId id;
    private String nome;
    private String descricao;
    private BigDecimal precoVenda;
    private String codigoBarras;
    private Categoria categoria;
    private String marca;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Produto(String nome, String descricao, BigDecimal precoVenda, String codigoBarras, Categoria categoria, String marca) {
        this.id = ProdutoId.generate();
        setNome(nome);
        setDescricao(descricao);
        setPrecoVenda(precoVenda);
        setCodigoBarras(codigoBarras);
        setCategoria(categoria);
        setMarca(marca);
        this.status = "ATIVO";
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Produto(ProdutoId id, String nome, String descricao, BigDecimal precoVenda, String codigoBarras, Categoria categoria, String marca, String status, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = Objects.requireNonNull(id);
        this.nome = Objects.requireNonNull(nome);
        this.descricao = Objects.requireNonNull(descricao);
        this.precoVenda = Objects.requireNonNull(precoVenda);
        this.codigoBarras = Objects.requireNonNull(codigoBarras);
        this.categoria = Objects.requireNonNull(categoria);
        this.marca = Objects.requireNonNull(marca);
        this.status = Objects.requireNonNull(status);
        this.dataCriacao = Objects.requireNonNull(dataCriacao);
        this.dataAtualizacao = Objects.requireNonNull(dataAtualizacao);
    }

    // Getters
    public ProdutoId getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getPrecoVenda() { return precoVenda; }
    public String getCodigoBarras() { return codigoBarras; }
    public Categoria getCategoria() { return categoria; } // Getter para Categoria
    public String getMarca() { return marca; } // Getter para Marca
    public String getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    // Setters com validação de domínio
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DomainException("Nome do produto não pode ser nulo ou vazio.");
        }
        this.nome = nome;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao; // Descrição pode ser vazia
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        if (precoVenda == null || precoVenda.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Preço de venda do produto não pode ser nulo ou negativo.");
        }
        this.precoVenda = precoVenda;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            throw new DomainException("Código de barras não pode ser nulo ou vazio.");
        }
        this.codigoBarras = codigoBarras;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setCategoria(Categoria categoria) { // Setter para Categoria
        Objects.requireNonNull(categoria, "Categoria do produto não pode ser nula.");
        this.categoria = categoria;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setMarca(String marca) { // Setter para Marca
        this.marca = marca;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void inativar() {
        if (this.status.equals("INATIVO")) {
            throw new DomainException("Produto já está inativo.");
        }
        this.status = "INATIVO";
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void ativar() {
        if (this.status.equals("ATIVO")) {
            throw new DomainException("Produto já está ativo.");
        }
        this.status = "ATIVO";
        this.dataAtualizacao = LocalDateTime.now();
    }

    public boolean isAtivo(){
        return this.status.equals("ATIVO");
    }

    /**
     * Atualiza as informações do produto. Este é o método de domínio que encapsula
     * a lógica de alteração do estado do produto.
     */
    public void atualizarInformacoes(
            String nome,
            String descricao,
            String codigoBarras,
            Categoria categoria,
            String marca,
            BigDecimal precoSugerido // Mapeado para precoVenda
    ) {
        setNome(nome);
        setDescricao(descricao);
        setCodigoBarras(codigoBarras);
        setCategoria(categoria);
        setMarca(marca);
        setPrecoVenda(precoSugerido);

        this.dataAtualizacao = LocalDateTime.now();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(id, produto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}