package com.targosystem.varejo.estoque.domain.model;

import com.targosystem.varejo.shared.domain.AggregateRoot;
import com.targosystem.varejo.shared.domain.DomainException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MovimentacaoEstoque implements AggregateRoot {
    private String id;
    private TipoMovimentacao tipo;
    private LocalDateTime dataHora;
    private String motivo;
    private String localOrigemId; // ID do LocalEstoque de origem
    private String localDestinoId; // ID do LocalEstoque de destino
    private List<ItemMovimentacaoEstoque> itens; // Lista de produtos e quantidades

    // Construtor para criar uma nova movimentação
    public MovimentacaoEstoque(TipoMovimentacao tipo, String localOrigemId, String localDestinoId, String motivo, List<ItemMovimentacaoEstoque> itens) {
        this.id = UUID.randomUUID().toString();
        this.dataHora = LocalDateTime.now();
        this.tipo = Objects.requireNonNull(tipo, "Tipo de movimentação não pode ser nulo.");
        this.localOrigemId = Objects.requireNonNull(localOrigemId, "Local de origem não pode ser nulo.");
        this.localDestinoId = Objects.requireNonNull(localDestinoId, "Local de destino não pode ser nulo.");

        if (itens == null || itens.isEmpty()) {
            throw new DomainException("Uma movimentação deve conter pelo menos um item.");
        }
        // Garante que cada item tenha uma referência à movimentação e um ID
        this.itens = new ArrayList<>();
        itens.forEach(item -> {
            if (item.getMovimentacaoId() == null) {
                item.setMovimentacaoId(this.id);
            }
            if (item.getId() == null) {
                item.setId(UUID.randomUUID().toString()); // Garante ID para o item
            }
            this.itens.add(item);
        });

        this.motivo = motivo; // Motivo pode ser nulo
    }

    // Construtor para reconstrução da persistência
    public MovimentacaoEstoque(String id, TipoMovimentacao tipo, LocalDateTime dataHora, String motivo, String localOrigemId, String localDestinoId, List<ItemMovimentacaoEstoque> itens) {
        this.id = Objects.requireNonNull(id);
        this.tipo = Objects.requireNonNull(tipo);
        this.dataHora = Objects.requireNonNull(dataHora);
        this.motivo = motivo;
        this.localOrigemId = Objects.requireNonNull(localOrigemId);
        this.localDestinoId = Objects.requireNonNull(localDestinoId);
        this.itens = (itens != null) ? new ArrayList<>(itens) : new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public TipoMovimentacao getTipo() { return tipo; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getMotivo() { return motivo; }
    public String getLocalOrigemId() { return localOrigemId; }
    public String getLocalDestinoId() { return localDestinoId; }
    public List<ItemMovimentacaoEstoque> getItens() {
        return Collections.unmodifiableList(itens);
    }

    // Para setar o ID da movimentação nos itens se for necessário após a criação
    public void setIdForItems(String movimentacaoId) {
        this.itens.forEach(item -> item.setMovimentacaoId(movimentacaoId));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovimentacaoEstoque that = (MovimentacaoEstoque) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}