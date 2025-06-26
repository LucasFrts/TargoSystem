// src/main/java/com/targosystem/varejo/estoque/domain/model/MovimentacaoEstoque.java
package com.targosystem.varejo.estoque.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class MovimentacaoEstoque {

    private String id;
    private String estoqueId; // Referência ao ID do Estoque pai
    private TipoMovimentacao tipo;
    private int quantidade;
    private LocalDateTime dataHora;
    private String motivo;
    private Lote loteAfetado; // NOVO: Lote afetado pela movimentação (pode ser null para saídas gerais)
    private LocalizacaoArmazenamento localizacaoAfetada; // NOVO: Localização afetada (pode ser null)

    // Construtor para criar uma nova movimentação (com lote e localização)
    public MovimentacaoEstoque(String estoqueId, TipoMovimentacao tipo, int quantidade, LocalDateTime dataHora, String motivo, Lote loteAfetado, LocalizacaoArmazenamento localizacaoAfetada) {
        this.id = UUID.randomUUID().toString();
        this.estoqueId = Objects.requireNonNull(estoqueId, "ID do estoque não pode ser nulo.");
        this.tipo = Objects.requireNonNull(tipo, "Tipo de movimentação não pode ser nulo.");
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade da movimentação deve ser positiva.");
        }
        this.quantidade = quantidade;
        this.dataHora = Objects.requireNonNull(dataHora, "Data e hora da movimentação não pode ser nula.");
        this.motivo = motivo; // Motivo pode ser nulo ou vazio
        this.loteAfetado = loteAfetado;
        this.localizacaoAfetada = localizacaoAfetada;
    }

    // Sobrecarga para construtor sem lote/localização (para movimentações mais gerais)
    public MovimentacaoEstoque(String estoqueId, TipoMovimentacao tipo, int quantidade, LocalDateTime dataHora, String motivo) {
        this(estoqueId, tipo, quantidade, dataHora, motivo, null, null);
    }

    // Construtor para reconstruir a movimentação do banco de dados
    public MovimentacaoEstoque(String id, String estoqueId, TipoMovimentacao tipo, int quantidade, LocalDateTime dataHora, String motivo, Lote loteAfetado, LocalizacaoArmazenamento localizacaoAfetada) {
        this.id = Objects.requireNonNull(id, "ID da movimentação não pode ser nulo.");
        this.estoqueId = Objects.requireNonNull(estoqueId, "ID do estoque não pode ser nulo.");
        this.tipo = Objects.requireNonNull(tipo, "Tipo de movimentação não pode ser nulo.");
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade da movimentação deve ser positiva.");
        }
        this.quantidade = quantidade;
        this.dataHora = Objects.requireNonNull(dataHora, "Data e hora da movimentação não pode ser nula.");
        this.motivo = motivo;
        this.loteAfetado = loteAfetado;
        this.localizacaoAfetada = localizacaoAfetada;
    }

    // Getters
    public String getId() { return id; }
    public String getEstoqueId() { return estoqueId; }
    public TipoMovimentacao getTipo() { return tipo; }
    public int getQuantidade() { return quantidade; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getMotivo() { return motivo; }
    public Lote getLoteAfetado() { return loteAfetado; } // NOVO
    public LocalizacaoArmazenamento getLocalizacaoAfetada() { return localizacaoAfetada; } // NOVO

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovimentacaoEstoque that = (MovimentacaoEstoque) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}