package com.targosystem.varejo.clientes.domain.model;

import com.targosystem.varejo.shared.domain.AggregateRoot;
import com.targosystem.varejo.shared.domain.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;

public class Cliente implements AggregateRoot {
    private ClienteId id;
    private String nome;
    private String cpf;
    private String email; // Adicional para um cliente
    private String telefone; // Adicional
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // Construtor para nova criação
    public Cliente(String nome, String cpf, String email, String telefone) {
        this.id = ClienteId.generate();
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setTelefone(telefone);
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor para reconstrução da persistência
    public Cliente(ClienteId id, String nome, String cpf, String email, String telefone, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = Objects.requireNonNull(id, "ID do cliente não pode ser nulo.");
        this.nome = Objects.requireNonNull(nome, "Nome do cliente não pode ser nulo.");
        this.cpf = Objects.requireNonNull(cpf, "CPF do cliente não pode ser nulo.");
        this.email = email; // Pode ser nulo/vazio
        this.telefone = telefone; // Pode ser nulo/vazio
        this.dataCriacao = Objects.requireNonNull(dataCriacao, "Data de criação não pode ser nula.");
        this.dataAtualizacao = Objects.requireNonNull(dataAtualizacao, "Data de atualização não pode ser nula.");
    }

    // Getters
    public ClienteId getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    // Setters com validação de domínio
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DomainException("Nome do cliente não pode ser nulo ou vazio.");
        }
        this.nome = nome;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) { // Validação simples para 11 dígitos
            throw new DomainException("CPF do cliente inválido. Deve conter 11 dígitos numéricos.");
        }
        this.cpf = cpf;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setEmail(String email) {
        // Validação de email mais complexa pode ser adicionada aqui
        this.email = email;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Método de domínio para atualizar informações do cliente
    public void atualizarInformacoes(String novoNome, String novoEmail, String novoTelefone) {
        setNome(novoNome);
        setEmail(novoEmail);
        setTelefone(novoTelefone);
        this.dataAtualizacao = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}