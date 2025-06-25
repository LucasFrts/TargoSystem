package com.targosystem.varejo.promocoes.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

// Sem anotações JPA aqui!
public class Promocao {

    private String id; // O ID ainda faz parte do modelo de domínio
    private String nome;
    private TipoDesconto tipoDesconto;
    private BigDecimal valorDesconto;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String kitPromocionalId;
    private boolean ativa;

    // Construtor principal para criar novas promoções no domínio
    public Promocao(String nome, TipoDesconto tipoDesconto, BigDecimal valorDesconto, LocalDateTime dataInicio, LocalDateTime dataFim, String kitPromocionalId) {
        this.id = UUID.randomUUID().toString(); // Gerado no domínio
        this.nome = Objects.requireNonNull(nome, "Nome da promoção não pode ser nulo.");
        this.tipoDesconto = Objects.requireNonNull(tipoDesconto, "Tipo de desconto não pode ser nulo.");
        this.valorDesconto = Objects.requireNonNull(valorDesconto, "Valor de desconto não pode ser nulo.");
        this.dataInicio = Objects.requireNonNull(dataInicio, "Data de início não pode ser nula.");
        this.dataFim = Objects.requireNonNull(dataFim, "Data de fim não pode ser nula.");
        this.ativa = true; // Por padrão, a promoção é criada como ativa
        this.kitPromocionalId = kitPromocionalId;
        validarDatas();
        validarValorDesconto();
    }

    // Construtor usado pelo repositório (infraestrutura) ao carregar do banco
    public Promocao(String id, String nome, TipoDesconto tipoDesconto, BigDecimal valorDesconto, LocalDateTime dataInicio, LocalDateTime dataFim, boolean ativa, String kitPromocionalId) {
        this.id = Objects.requireNonNull(id, "ID da promoção não pode ser nulo.");
        this.nome = Objects.requireNonNull(nome, "Nome da promoção não pode ser nulo.");
        this.tipoDesconto = Objects.requireNonNull(tipoDesconto, "Tipo de desconto não pode ser nulo.");
        this.valorDesconto = Objects.requireNonNull(valorDesconto, "Valor de desconto não pode ser nulo.");
        this.dataInicio = Objects.requireNonNull(dataInicio, "Data de início não pode ser nula.");
        this.dataFim = Objects.requireNonNull(dataFim, "Data de fim não pode ser nula.");
        this.ativa = ativa;
        this.kitPromocionalId = kitPromocionalId;
        validarDatas(); // As validações ainda pertencem ao domínio
        validarValorDesconto();
    }

    public Promocao(String nome, TipoDesconto tipoDesconto, BigDecimal valorDesconto, LocalDateTime dataInicio, LocalDateTime dataFim) {
        this(nome, tipoDesconto, valorDesconto, dataInicio, dataFim, null);
    }


    // Métodos de negócio (inalterados)
    public void ativar() {
        if (!this.ativa) {
            this.ativa = true;
            // Opcional: Publicar evento PromocaoAtivadaEvent
        }
    }

    public void inativar() {
        if (this.ativa) {
            this.ativa = false;
            // Opcional: Publicar evento PromocaoInativadaEvent
        }
    }

    public boolean estaAtivaNoMomento(LocalDateTime momento) {
        return this.ativa && !momento.isBefore(this.dataInicio) && !momento.isAfter(this.dataFim);
    }

    // Validações (inalteradas)
    private void validarDatas() {
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim.");
        }
    }

    private void validarValorDesconto() {
        if (valorDesconto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor de desconto não pode ser negativo.");
        }
        if (tipoDesconto == TipoDesconto.PERCENTUAL && valorDesconto.compareTo(BigDecimal.ONE) > 0) {
            // Para percentual, o valor deve ser entre 0 e 1 (ex: 0.10 para 10%)
            throw new IllegalArgumentException("Valor percentual de desconto deve ser entre 0 e 1.");
        }
    }

    public String getKitPromocionalId() {
        return kitPromocionalId;
    }

    public void setKitPromocionalId(String kitPromocionalId) {
        this.kitPromocionalId = kitPromocionalId;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoDesconto getTipoDesconto() {
        return tipoDesconto;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public boolean isAtiva() {
        return ativa;
    }

    // Setters (apenas se for necessário para atualização de estado)
    public void setNome(String nome) {
        this.nome = Objects.requireNonNull(nome, "Nome da promoção não pode ser nulo.");
    }

    public void setTipoDesconto(TipoDesconto tipoDesconto) {
        this.tipoDesconto = Objects.requireNonNull(tipoDesconto, "Tipo de desconto não pode ser nulo.");
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = Objects.requireNonNull(valorDesconto, "Valor de desconto não pode ser nulo.");
        validarValorDesconto(); // Revalidar ao definir
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = Objects.requireNonNull(dataInicio, "Data de início não pode ser nula.");
        validarDatas(); // Revalidar ao definir
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = Objects.requireNonNull(dataFim, "Data de fim não pode ser nula.");
        validarDatas(); // Revalidar ao definir
    }

    // hashCode e equals (inalterados, baseados no ID de domínio)
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