package com.targosystem.varejo.fornecedores.domain.model;

import com.targosystem.varejo.shared.domain.AggregateRoot;
import com.targosystem.varejo.shared.domain.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;
// Não vamos adicionar lista de EntregaFornecedor diretamente aqui para manter o agregado pequeno.
// Entregas serão gerenciadas por um outro Use Case/Repositório, mas referenciarão o FornecedorId.

public class Fornecedor implements AggregateRoot {

    private FornecedorId id; // Usando FornecedorId
    private String nome;
    private String cnpj;
    private Contato contato; // Usando Contato
    private Endereco endereco; // Objeto de Valor
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // Construtor para criar um novo Fornecedor
    public Fornecedor(String nome, String cnpj, Contato contato, Endereco endereco) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DomainException("Nome do fornecedor não pode ser nulo ou vazio.");
        }
        if (cnpj == null || !cnpj.matches("\\d{14}")) {
            throw new DomainException("CNPJ inválido. Deve conter 14 dígitos numéricos.");
        }
        if (contato == null) {
            throw new DomainException("Contato do fornecedor não pode ser nulo.");
        }
        if (endereco == null) {
            throw new DomainException("Endereço do fornecedor não pode ser nulo.");
        }

        this.id = FornecedorId.generate(); // Geração de ID pelo Value Object
        this.nome = nome;
        this.cnpj = cnpj;
        this.contato = contato;
        this.endereco = endereco;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor para reconstruir o Fornecedor a partir da persistência
    public Fornecedor(FornecedorId id, String nome, String cnpj, Contato contato, Endereco endereco, boolean ativo, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = Objects.requireNonNull(id, "ID do fornecedor não pode ser nulo.");
        this.nome = Objects.requireNonNull(nome, "Nome do fornecedor não pode ser nulo.");
        this.cnpj = Objects.requireNonNull(cnpj, "CNPJ do fornecedor não pode ser nulo.");
        this.contato = Objects.requireNonNull(contato, "Contato do fornecedor não pode ser nulo.");
        this.endereco = Objects.requireNonNull(endereco, "Endereço do fornecedor não pode ser nulo.");
        this.ativo = ativo;
        this.dataCriacao = Objects.requireNonNull(dataCriacao, "Data de criação do fornecedor não pode ser nula.");
        this.dataAtualizacao = Objects.requireNonNull(dataAtualizacao, "Data de atualização do fornecedor não pode ser nula.");
    }

    // Métodos de Comportamento
    public void atualizarInformacoes(String novoNome, Contato novoContato, Endereco novoEndereco) {
        if (novoNome != null && !novoNome.trim().isEmpty()) {
            this.nome = novoNome;
        }
        if (novoContato != null) {
            this.contato = novoContato;
        }
        if (novoEndereco != null) {
            this.endereco = novoEndereco;
        }
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void ativar() {
        if (!this.ativo) {
            this.ativo = true;
            this.dataAtualizacao = LocalDateTime.now();
        }
    }

    public void inativar() {
        if (this.ativo) {
            this.ativo = false;
            this.dataAtualizacao = LocalDateTime.now();
        }
    }

    // Getters
    public FornecedorId getId() { return id; }
    public String getNome() { return nome; }
    public String getCnpj() { return cnpj; }
    public Contato getContato() { return contato; }
    public Endereco getEndereco() { return endereco; }
    public boolean isAtivo() { return ativo; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fornecedor that = (Fornecedor) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}