package com.targosystem.varejo.promocoes.domain.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class KitPromocional {

    private String id;
    private String nome;
    private String descricao;
    private BigDecimal precoFixoKit; // Preço do kit como um todo
    private List<ItemKit> itens; // Objetos de valor dentro do agregado

    // Construtor para criar um novo kit
    public KitPromocional(String nome, String descricao, BigDecimal precoFixoKit, List<ItemKit> itens) {
        this.id = UUID.randomUUID().toString();
        this.nome = Objects.requireNonNull(nome, "Nome do kit não pode ser nulo.");
        this.descricao = Objects.requireNonNull(descricao, "Descrição do kit não pode ser nula.");
        this.precoFixoKit = Objects.requireNonNull(precoFixoKit, "Preço fixo do kit não pode ser nulo.");
        this.itens = Objects.requireNonNull(itens, "Itens do kit não podem ser nulos.");
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Um kit promocional deve ter pelo menos um item.");
        }
        this.itens = itens.stream().collect(Collectors.toUnmodifiableList()); // Torna a lista imutável
        validarPrecoFixoKit();
    }

    // Construtor para reconstruir o kit do banco de dados
    public KitPromocional(String id, String nome, String descricao, BigDecimal precoFixoKit, List<ItemKit> itens) {
        this.id = Objects.requireNonNull(id, "ID do kit não pode ser nulo.");
        this.nome = Objects.requireNonNull(nome, "Nome do kit não pode ser nulo.");
        this.descricao = Objects.requireNonNull(descricao, "Descrição do kit não pode ser nula.");
        this.precoFixoKit = Objects.requireNonNull(precoFixoKit, "Preço fixo do kit não pode ser nulo.");
        this.itens = Objects.requireNonNull(itens, "Itens do kit não podem ser nulos.");
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Um kit promocional deve ter pelo menos um item.");
        }
        this.itens = itens.stream().collect(Collectors.toUnmodifiableList());
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
        // Para adicionar um item, criamos uma nova lista, mantendo a imutabilidade
        List<ItemKit> novaLista = new java.util.ArrayList<>(this.itens);
        novaLista.add(Objects.requireNonNull(item, "Item do kit não pode ser nulo."));
        this.itens = Collections.unmodifiableList(novaLista);
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
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getPrecoFixoKit() { return precoFixoKit; }
    public List<ItemKit> getItens() { return itens; }

    // Setters para atualização (com validação, se aplicável)
    public void setNome(String nome) {
        this.nome = Objects.requireNonNull(nome, "Nome do kit não pode ser nulo.");
    }

    public void setDescricao(String descricao) {
        this.descricao = Objects.requireNonNull(descricao, "Descrição do kit não pode ser nula.");
    }

    public void setPrecoFixoKit(BigDecimal precoFixoKit) {
        this.precoFixoKit = Objects.requireNonNull(precoFixoKit, "Preço fixo do kit não pode ser nulo.");
        validarPrecoFixoKit();
    }

    // Este setter substitui a lista de itens, use com cautela (métodos de adição/remoção são preferíveis)
    public void setItens(List<ItemKit> itens) {
        this.itens = Objects.requireNonNull(itens, "Itens do kit não podem ser nulos.");
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Um kit promocional deve ter pelo menos um item.");
        }
        this.itens = itens.stream().collect(Collectors.toUnmodifiableList());
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