package com.targosystem.varejo.promocoes.infra.persistence.entity;

import com.targosystem.varejo.promocoes.domain.model.Promocao;
import com.targosystem.varejo.promocoes.domain.model.TipoDesconto;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    // Construtor padrão JPA
    protected PromocaoJpaEntity() {}

    // Construtor para facilitar a criação a partir do modelo de domínio
    public PromocaoJpaEntity(String id, String nome, TipoDesconto tipoDesconto, BigDecimal valorDesconto, LocalDateTime dataInicio, LocalDateTime dataFim, boolean ativa) {
        this.id = id;
        this.nome = nome;
        this.tipoDesconto = tipoDesconto;
        this.valorDesconto = valorDesconto;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.ativa = ativa;
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
                promocao.isAtiva()
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
                this.ativa
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

    // Setters (necessários para que o JPA possa carregar e atualizar a entidade)
    public void setId(String id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setTipoDesconto(TipoDesconto tipoDesconto) { this.tipoDesconto = tipoDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
}