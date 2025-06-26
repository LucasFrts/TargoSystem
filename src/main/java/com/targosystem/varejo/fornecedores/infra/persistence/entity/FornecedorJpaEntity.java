package com.targosystem.varejo.fornecedores.infra.persistence.entity;

import com.targosystem.varejo.fornecedores.domain.model.Fornecedor;
import com.targosystem.varejo.fornecedores.domain.model.FornecedorId; // NOVO
import com.targosystem.varejo.fornecedores.domain.model.Contato; // NOVO
import com.targosystem.varejo.fornecedores.domain.model.Endereco;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "fornecedores")
public class FornecedorJpaEntity {

    @Id
    private String id; // Agora representa o valor do FornecedorId

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cnpj;

    // Campos de Contato mapeados diretamente
    @Column(name = "contato_email", nullable = false)
    private String emailContato;
    @Column(name = "contato_telefone", nullable = false)
    private String telefoneContato;

    // Campos de Endereço mapeados diretamente
    @Column(name = "endereco_logradouro", nullable = false)
    private String logradouro;
    @Column(name = "endereco_numero", nullable = false)
    private String numero;
    @Column(name = "endereco_complemento")
    private String complemento;
    @Column(name = "endereco_bairro", nullable = false)
    private String bairro;
    @Column(name = "endereco_cidade", nullable = false)
    private String cidade;
    @Column(name = "endereco_estado", nullable = false, length = 2)
    private String estado;
    @Column(name = "endereco_cep", nullable = false, length = 8)
    private String cep;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    protected FornecedorJpaEntity() {}

    public FornecedorJpaEntity(String id, String nome, String cnpj, String emailContato, String telefoneContato, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep, boolean ativo, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.emailContato = emailContato;
        this.telefoneContato = telefoneContato;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public static FornecedorJpaEntity fromDomain(Fornecedor fornecedor) {
        Endereco endereco = fornecedor.getEndereco();
        Contato contato = fornecedor.getContato(); // NOVO
        return new FornecedorJpaEntity(
                fornecedor.getId().value(), // Acessa o valor do FornecedorId
                fornecedor.getNome(),
                fornecedor.getCnpj(),
                contato.email(), // NOVO
                contato.telefone(), // NOVO
                endereco.logradouro(),
                endereco.numero(),
                endereco.complemento(),
                endereco.bairro(),
                endereco.cidade(),
                endereco.estado(),
                endereco.cep(),
                fornecedor.isAtivo(),
                fornecedor.getDataCriacao(),
                fornecedor.getDataAtualizacao()
        );
    }

    public Fornecedor toDomain() {
        Endereco endereco = new Endereco(
                this.logradouro,
                this.numero,
                this.complemento,
                this.bairro,
                this.cidade,
                this.estado,
                this.cep
        );
        Contato contato = new Contato(this.emailContato, this.telefoneContato); // NOVO
        return new Fornecedor(
                new FornecedorId(this.id), // Converte de volta para FornecedorId
                this.nome,
                this.cnpj,
                contato, // NOVO
                endereco,
                this.ativo,
                this.dataCriacao,
                this.dataAtualizacao
        );
    }

    // Getters e Setters (Certifique-se de que todos estão presentes e atualizados)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getEmailContato() { return emailContato; } // NOVO
    public void setEmailContato(String emailContato) { this.emailContato = emailContato; } // NOVO
    public String getTelefoneContato() { return telefoneContato; } // NOVO
    public void setTelefoneContato(String telefoneContato) { this.telefoneContato = telefoneContato; } // NOVO
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}