// src/main/java/com/targosystem/varejo/promocoes/infra/persistence/entity/PromocaoJpaEntity.java
package com.targosystem.varejo.promocoes.infra.persistence.entity;

import com.targosystem.varejo.promocoes.domain.model.Promocao;
import com.targosystem.varejo.promocoes.domain.model.TipoDesconto;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList; // Para inicializar a lista
import java.util.List;

@Entity
@Table(name = "promocoes")
public class PromocaoJpaEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDesconto tipoDesconto;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDesconto;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    @Column(nullable = false)
    private LocalDateTime dataFim;

    @Column(nullable = false)
    private boolean ativa;

    // NOVO: Coleção de IDs de produtos associados à promoção
    @ElementCollection(fetch = FetchType.LAZY) // Lazy loading para otimização
    @CollectionTable(name = "promocao_produtos", joinColumns = @JoinColumn(name = "promocao_id"))
    @Column(name = "produto_id", nullable = false)
    private List<String> produtoIds = new ArrayList<>(); // Inicializa para evitar NullPointer

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    // Construtor padrão JPA
    protected PromocaoJpaEntity() {}

    // Construtor COMPLETO para facilitar a criação a partir do modelo de domínio
    // Agora com 9 argumentos (sem kitPromocionalId, com produtoIds)
    public PromocaoJpaEntity(String id, String nome, TipoDesconto tipoDesconto, BigDecimal valorDesconto,
                             LocalDateTime dataInicio, LocalDateTime dataFim, boolean ativa,
                             List<String> produtoIds, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) { // MUDADO
        this.id = id;
        this.nome = nome;
        this.tipoDesconto = tipoDesconto;
        this.valorDesconto = valorDesconto;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.ativa = ativa;
        this.produtoIds = produtoIds != null ? new ArrayList<>(produtoIds) : new ArrayList<>(); // Atribui a lista de IDs
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    // Métodos de conversão
    public static PromocaoJpaEntity fromDomain(Promocao promocao) {
        return new PromocaoJpaEntity(
                promocao.getId(),
                promocao.getNome(),
                promocao.getTipoDesconto(),
                promocao.getValorDesconto(),
                promocao.getDataInicio(),
                promocao.getDataFim(),
                promocao.isAtiva(),
                promocao.getProdutoIds(),     // Inclui a lista de IDs de produtos
                promocao.getDataCriacao(),
                promocao.getDataAtualizacao()
        );
    }

    public Promocao toDomain() {
        return new Promocao(
                this.id,
                this.nome,
                this.tipoDesconto,
                this.valorDesconto,
                this.dataInicio,
                this.dataFim,
                this.ativa,
                this.produtoIds,     // Inclui a lista de IDs de produtos
                this.dataCriacao,
                this.dataAtualizacao
        );
    }

    // Getters para JPA (e para uso no DAO)
    public String getId() { return id; }
    public String getNome() { return nome; }
    public TipoDesconto getTipoDesconto() { return tipoDesconto; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public boolean isAtiva() { return ativa; }
    public List<String> getProdutoIds() { return produtoIds; } // Getter para a lista de IDs de produtos
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }


    // Setters (necessários para que o JPA possa carregar e atualizar a entidade)
    public void setId(String id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setTipoDesconto(TipoDesconto tipoDesconto) { this.tipoDesconto = tipoDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    public void setProdutoIds(List<String> produtoIds) { this.produtoIds = produtoIds != null ? new ArrayList<>(produtoIds) : new ArrayList<>(); } // Setter para a lista de IDs de produtos
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}