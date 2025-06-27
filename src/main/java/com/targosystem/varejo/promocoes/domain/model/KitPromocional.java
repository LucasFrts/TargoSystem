package com.targosystem.varejo.promocoes.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class KitPromocional {

    private String id;
    private String nome;
    private String descricao;
    private BigDecimal precoFixoKit;
    private List<ItemKit> itens;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // Construtor principal para criar um novo kit no domínio (gera ID e datas)
    public KitPromocional(String nome, String descricao, BigDecimal precoFixoKit, List<ItemKit> itens) {
        this.id = UUID.randomUUID().toString();
        this.nome = Objects.requireNonNull(nome, "Nome do kit não pode ser nulo.");
        this.descricao = Objects.requireNonNull(descricao, "Descrição do kit não pode ser nula.");
        this.precoFixoKit = Objects.requireNonNull(precoFixoKit, "Preço fixo do kit não pode ser nulo.");
        this.itens = Objects.requireNonNull(itens, "Itens do kit não podem ser nulos.");
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Um kit promocional deve ter pelo menos um item.");
        }
        this.itens = itens.stream().collect(Collectors.toUnmodifiableList());
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        validarPrecoFixoKit();
    }

    // Construtor usado pelo repositório (infraestrutura) ao carregar do banco (recebe ID e datas)
    public KitPromocional(String id, String nome, String descricao, BigDecimal precoFixoKit,
                          List<ItemKit> itens, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = Objects.requireNonNull(id, "ID do kit não pode ser nulo.");
        this.nome = Objects.requireNonNull(nome, "Nome do kit não pode ser nulo.");
        this.descricao = Objects.requireNonNull(descricao, "Descrição do kit não pode ser nula.");
        this.precoFixoKit = Objects.requireNonNull(precoFixoKit, "Preço fixo do kit não pode ser nulo.");
        this.itens = Objects.requireNonNull(itens, "Itens do kit não podem ser nulos.");
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Um kit promocional deve ter pelo menos um item.");
        }
        this.itens = itens.stream().collect(Collectors.toUnmodifiableList());
        this.dataCriacao = Objects.requireNonNull(dataCriacao, "Data de criação não pode ser nula.");
        this.dataAtualizacao = Objects.requireNonNull(dataAtualizacao, "Data de atualização não pode ser nula.");
        validarPrecoFixoKit();
    }

    // Validações
    private void validarPrecoFixoKit() {
        if (precoFixoKit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço fixo do kit não pode ser negativo.");
        }
    }

    // Métodos de negócio
    public void adicionarItem(ItemKit item) {
        List<ItemKit> novaLista = new java.util.ArrayList<>(this.itens);
        novaLista.add(Objects.requireNonNull(item, "Item do kit não pode ser nulo."));
        this.itens = Collections.unmodifiableList(novaLista);
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void removerItem(String produtoId) {
        List<ItemKit> novaLista = this.itens.stream()
                .filter(item -> !item.getProdutoId().equals(produtoId))
                .collect(Collectors.toCollection(java.util.ArrayList::new));
        if (novaLista.size() == this.itens.size()) {
            throw new IllegalArgumentException("Item com ID de produto " + produtoId + " não encontrado no kit.");
        }
        if (novaLista.isEmpty()) {
            throw new IllegalArgumentException("Um kit promocional deve ter pelo menos um item. Não é possível remover o último item.");
        }
        this.itens = Collections.unmodifiableList(novaLista);
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getPrecoFixoKit() { return precoFixoKit; }
    public List<ItemKit> getItens() { return itens; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    // Setters para atualização (com validação, se aplicável)
    public void setNome(String nome) {
        this.nome = Objects.requireNonNull(nome, "Nome do kit não pode ser nulo.");
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setDescricao(String descricao) {
        this.descricao = Objects.requireNonNull(descricao, "Descrição do kit não pode ser nula.");
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setPrecoFixoKit(BigDecimal precoFixoKit) {
        this.precoFixoKit = Objects.requireNonNull(precoFixoKit, "Preço fixo do kit não pode ser nulo.");
        validarPrecoFixoKit();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setItens(List<ItemKit> itens) {
        this.itens = Objects.requireNonNull(itens, "Itens do kit não podem ser nulos.");
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Um kit promocional deve ter pelo menos um item.");
        }
        this.itens = itens.stream().collect(Collectors.toUnmodifiableList());
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = Objects.requireNonNull(dataAtualizacao, "Data de atualização não pode ser nula.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KitPromocional that = (KitPromocional) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}