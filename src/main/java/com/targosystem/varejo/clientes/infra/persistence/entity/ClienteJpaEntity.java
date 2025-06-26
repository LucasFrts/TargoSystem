package com.targosystem.varejo.clientes.infra.persistence.entity;

import com.targosystem.varejo.clientes.domain.model.Cliente;
import com.targosystem.varejo.clientes.domain.model.ClienteId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "clientes")
public class ClienteJpaEntity {

    @Id
    private String id; // ID como String para corresponder ao ClienteId

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 14) // Ex: Para CPF formatado ou não, 11 ou 14 dígitos
    private String cpf;

    @Column(length = 100) // Opcional
    private String email;

    @Column(length = 20) // Opcional
    private String telefone;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    protected ClienteJpaEntity() {}

    public ClienteJpaEntity(String id, String nome, String cpf, String email, String telefone, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public static ClienteJpaEntity fromDomain(Cliente cliente) {
        return new ClienteJpaEntity(
                cliente.getId() != null ? cliente.getId().value() : null, // ID pode ser nulo se for nova entidade
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getDataCriacao(),
                cliente.getDataAtualizacao()
        );
    }

    public Cliente toDomain() {
        return new Cliente(
                new ClienteId(this.id),
                this.nome,
                this.cpf,
                this.email,
                this.telefone,
                this.dataCriacao,
                this.dataAtualizacao
        );
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}