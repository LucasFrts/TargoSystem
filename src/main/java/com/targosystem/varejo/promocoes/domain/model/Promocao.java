// src/main/java/com/targosystem/varejo/promocoes/domain/model/Promocao.java
package com.targosystem.varejo.promocoes.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections; // Importar para Collections.unmodifiableList
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.ArrayList; // Para inicializar a lista

public class Promocao {

    private String id;
    private String nome;
    private TipoDesconto tipoDesconto;
    private BigDecimal valorDesconto;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private List<String> produtoIds; // NOVO: Lista de IDs de produtos
    private boolean ativa;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // Construtor principal para criar novas promoções no domínio
    public Promocao(String nome, TipoDesconto tipoDesconto, BigDecimal valorDesconto, LocalDateTime dataInicio, LocalDateTime dataFim, List<String> produtoIds) {
        this.id = UUID.randomUUID().toString();
        this.nome = Objects.requireNonNull(nome, "Nome da promoção não pode ser nulo.");
        this.tipoDesconto = Objects.requireNonNull(tipoDesconto, "Tipo de desconto não pode ser nulo.");
        this.valorDesconto = Objects.requireNonNull(valorDesconto, "Valor de desconto não pode ser nulo.");
        this.dataInicio = Objects.requireNonNull(dataInicio, "Data de início não pode ser nula.");
        this.dataFim = Objects.requireNonNull(dataFim, "Data de fim não pode ser nula.");
        this.ativa = true; // Por padrão, a promoção é criada como ativa
        this.produtoIds = produtoIds != null ? new ArrayList<>(produtoIds) : new ArrayList<>(); // Inicializa a lista
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        validarDatas();
        validarValorDesconto();
    }

    // Construtor usado pelo repositório (infraestrutura) ao carregar do banco
    public Promocao(String id, String nome, TipoDesconto tipoDesconto, BigDecimal valorDesconto,
                    LocalDateTime dataInicio, LocalDateTime dataFim, boolean ativa,
                    List<String> produtoIds, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) { // MUDADO: kitPromocionalId para List<String> produtoIds
        this.id = Objects.requireNonNull(id, "ID da promoção não pode ser nulo.");
        this.nome = Objects.requireNonNull(nome, "Nome da promoção não pode ser nulo.");
        this.tipoDesconto = Objects.requireNonNull(tipoDesconto, "Tipo de desconto não pode ser nulo.");
        this.valorDesconto = Objects.requireNonNull(valorDesconto, "Valor de desconto não pode ser nulo.");
        this.dataInicio = Objects.requireNonNull(dataInicio, "Data de início não pode ser nula.");
        this.dataFim = Objects.requireNonNull(dataFim, "Data de fim não pode ser nula.");
        this.ativa = ativa;
        this.produtoIds = produtoIds != null ? new ArrayList<>(produtoIds) : new ArrayList<>(); // Inicializa a lista
        this.dataCriacao = Objects.requireNonNull(dataCriacao, "Data de criação não pode ser nula.");
        this.dataAtualizacao = Objects.requireNonNull(dataAtualizacao, "Data de atualização não pode ser nula.");
        validarDatas();
        validarValorDesconto();
    }

    // Métodos de negócio
    public void ativar() {
        if (!this.ativa) {
            this.ativa = true;
            this.dataAtualizacao = LocalDateTime.now();
        }
    }

    public void inativar() {
        if (this.ativa) {
            this.ativa = false;
            this.dataAtualizacao = LocalDateTime.now();
        }
    }

    public boolean estaAtivaNoMomento(LocalDateTime momento) {
        return this.ativa && !momento.isBefore(this.dataInicio) && !momento.isAfter(this.dataFim);
    }

    // Validações
    private void validarDatas() {
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim.");
        }
    }

    private void validarValorDesconto() {
        if (valorDesconto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor de desconto não pode ser negativo.");
        }
        // Ajuste: A validação para PERCENTUAL deve permitir 1.0 (100%)
        if (tipoDesconto == TipoDesconto.PERCENTUAL && (valorDesconto.compareTo(BigDecimal.ZERO) < 0 || valorDesconto.compareTo(BigDecimal.ONE) > 0)) {
            throw new IllegalArgumentException("Valor percentual de desconto deve ser entre 0 e 1.");
        }
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public TipoDesconto getTipoDesconto() { return tipoDesconto; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public boolean isAtiva() { return ativa; }
    public List<String> getProdutoIds() { return Collections.unmodifiableList(produtoIds); } // Retorna uma lista imutável
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    // Setters (com atualização de dataAtualizacao)
    public void setNome(String nome) {
        this.nome = Objects.requireNonNull(nome, "Nome da promoção não pode ser nulo.");
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setTipoDesconto(TipoDesconto tipoDesconto) {
        this.tipoDesconto = Objects.requireNonNull(tipoDesconto, "Tipo de desconto não pode ser nulo.");
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = Objects.requireNonNull(valorDesconto, "Valor de desconto não pode ser nulo.");
        validarValorDesconto();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = Objects.requireNonNull(dataInicio, "Data de início não pode ser nula.");
        validarDatas();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = Objects.requireNonNull(dataFim, "Data de fim não pode ser nula.");
        validarDatas();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setProdutoIds(List<String> produtoIds) { // Setter para lista de IDs de produtos
        this.produtoIds = produtoIds != null ? new ArrayList<>(produtoIds) : new ArrayList<>();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = Objects.requireNonNull(dataAtualizacao, "Data de atualização não pode ser nula.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Promocao promocao = (Promocao) o;
        return id.equals(promocao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}