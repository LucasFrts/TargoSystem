package com.targosystem.varejo.estoque.infra.persistence.entity;

import com.targosystem.varejo.estoque.domain.model.ItemMovimentacaoEstoque;
import com.targosystem.varejo.estoque.domain.model.MovimentacaoEstoque;
import com.targosystem.varejo.estoque.domain.model.TipoMovimentacao;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "movimentacoes_estoque")
public class MovimentacaoEstoqueJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(length = 255)
    private String motivo;

    // NOVOS: Referências aos IDs dos Locais de Estoque (Origem e Destino)
    @Column(name = "local_origem_id", nullable = false)
    private String localOrigemId;

    @Column(name = "local_destino_id", nullable = false)
    private String localDestinoId;

    // NOVO: Coleção de itens da movimentação (detalhes dos produtos e quantidades)
    @OneToMany(mappedBy = "movimentacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemMovimentacaoEstoqueJpaEntity> itens = new ArrayList<>();

    protected MovimentacaoEstoqueJpaEntity() {}

    public MovimentacaoEstoqueJpaEntity(String id, TipoMovimentacao tipo, LocalDateTime dataHora, String motivo, String localOrigemId, String localDestinoId, List<ItemMovimentacaoEstoqueJpaEntity> itens) {
        this.id = id;
        this.tipo = tipo;
        this.dataHora = dataHora;
        this.motivo = motivo;
        this.localOrigemId = localOrigemId;
        this.localDestinoId = localDestinoId;
        this.itens = (itens != null) ? new ArrayList<>(itens) : new ArrayList<>();
    }

    public static MovimentacaoEstoqueJpaEntity fromDomain(MovimentacaoEstoque movimentacao) {
        MovimentacaoEstoqueJpaEntity entity = new MovimentacaoEstoqueJpaEntity(
                movimentacao.getId(),
                movimentacao.getTipo(),
                movimentacao.getDataHora(),
                movimentacao.getMotivo(),
                movimentacao.getLocalOrigemId(),
                movimentacao.getLocalDestinoId(),
                null // Os itens serão preenchidos abaixo para setar a referência ao pai
        );

        List<ItemMovimentacaoEstoqueJpaEntity> jpaItens = movimentacao.getItens().stream()
                .map(ItemMovimentacaoEstoqueJpaEntity::fromDomain)
                .collect(Collectors.toList());
        jpaItens.forEach(item -> item.setMovimentacao(entity)); // Garante a ligação com o pai
        entity.setItens(jpaItens);

        return entity;
    }

    public MovimentacaoEstoque toDomain() {
        List<ItemMovimentacaoEstoque> domainItens = this.itens.stream()
                .map(ItemMovimentacaoEstoqueJpaEntity::toDomain)
                .collect(Collectors.toList());

        return new MovimentacaoEstoque(
                this.id,
                this.tipo,
                this.dataHora,
                this.motivo,
                this.localOrigemId,
                this.localDestinoId,
                domainItens
        );
    }

    // Getters e Setters (ATUALIZADOS)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getLocalOrigemId() { return localOrigemId; }
    public void setLocalOrigemId(String localOrigemId) { this.localOrigemId = localOrigemId; }
    public String getLocalDestinoId() { return localDestinoId; }
    public void setLocalDestinoId(String localDestinoId) { this.localDestinoId = localDestinoId; }

    public List<ItemMovimentacaoEstoqueJpaEntity> getItens() {
        return itens;
    }
    public void setItens(List<ItemMovimentacaoEstoqueJpaEntity> itens) {
        this.itens.clear();
        if (itens != null) {
            this.itens.addAll(itens);
        }
    }
}