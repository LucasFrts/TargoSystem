package com.targosystem.varejo.estoque.domain.model;

import com.targosystem.varejo.shared.domain.DomainException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Estoque {

    private String id;
    private String produtoId;
    private final List<ItemEstoque> itensEstoque;
    private final List<MovimentacaoEstoque> movimentacoes;

    public Estoque(String produtoId) {
        this.id = UUID.randomUUID().toString();
        this.produtoId = Objects.requireNonNull(produtoId, "ID do produto não pode ser nulo.");
        this.itensEstoque = new ArrayList<>();
        this.movimentacoes = new ArrayList<>();
    }

    // Construtor para reconstruir o estoque do banco de dados
    public Estoque(String id, String produtoId, List<ItemEstoque> itensEstoque, List<MovimentacaoEstoque> movimentacoes) {
        this.id = Objects.requireNonNull(id, "ID do estoque não pode ser nulo.");
        this.produtoId = Objects.requireNonNull(produtoId, "ID do produto não pode ser nulo.");
        this.itensEstoque = Objects.requireNonNull(itensEstoque, "Itens de estoque não podem ser nulos.");
        this.itensEstoque.addAll(itensEstoque); // Adiciona os itens da lista passada
        this.movimentacoes = Objects.requireNonNull(movimentacoes, "Movimentações não podem ser nulas.");
        this.movimentacoes.addAll(movimentacoes);
    }

    // Métodos de negócio:
    public void adicionarItensComLoteELocalizacao(int quantidade, Lote lote, LocalizacaoArmazenamento localizacao, String motivo) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a adicionar deve ser positiva.");
        }

        Optional<ItemEstoque> existingItem = this.itensEstoque.stream()
                .filter(item -> item.getLote().equals(lote) && item.getLocalizacao().equals(localizacao))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().adicionarQuantidade(quantidade);
        } else {
            ItemEstoque newItem = new ItemEstoque(this.produtoId, quantidade, lote, localizacao, this.id);
            this.itensEstoque.add(newItem);
        }

        this.movimentacoes.add(new MovimentacaoEstoque(this.id, TipoMovimentacao.ENTRADA, quantidade, LocalDateTime.now(), motivo, lote, localizacao));
    }

    public void removerItens(int quantidade, String motivo) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a remover deve ser positiva.");
        }
        if (getQuantidadeTotalDisponivel() < quantidade) {
            throw new DomainException("Quantidade insuficiente em estoque para o produto " + this.produtoId + ". Disponível: " + getQuantidadeTotalDisponivel() + ", Requisitado: " + quantidade);
        }

        int quantidadeRestante = quantidade;
        // Prioriza a remoção de itens com menor validade, ou pode ter outra lógica (FIFO, LIFO)
        List<ItemEstoque> itensParaRemover = new ArrayList<>(this.itensEstoque);
        itensParaRemover.sort((item1, item2) -> {
            // Lógica para ordenar: Ex: por data de validade (mais antiga primeiro)
            if (item1.getLote().getDataValidade() != null && item2.getLote().getDataValidade() != null) {
                return item1.getLote().getDataValidade().compareTo(item2.getLote().getDataValidade());
            }
            return 0;
        });

        for (ItemEstoque item : itensParaRemover) {
            if (quantidadeRestante == 0) break;

            if (item.getQuantidade() >= quantidadeRestante) {
                item.removerQuantidade(quantidadeRestante);
                this.movimentacoes.add(new MovimentacaoEstoque(this.id, TipoMovimentacao.SAIDA, quantidadeRestante, LocalDateTime.now(), motivo, item.getLote(), item.getLocalizacao()));
                quantidadeRestante = 0;
            } else {
                int qtdRemovidaDoItem = item.getQuantidade();
                item.removerQuantidade(qtdRemovidaDoItem); // Zera a quantidade deste item
                this.movimentacoes.add(new MovimentacaoEstoque(this.id, TipoMovimentacao.SAIDA, qtdRemovidaDoItem, LocalDateTime.now(), motivo, item.getLote(), item.getLocalizacao()));
                quantidadeRestante -= qtdRemovidaDoItem;
            }
        }

        // Remove itens com quantidade zero (se aplicável ao seu domínio)
        this.itensEstoque.removeIf(item -> item.getQuantidade() == 0);
    }

    public int getQuantidadeTotalDisponivel() {
        return this.itensEstoque.stream()
                .mapToInt(ItemEstoque::getQuantidade)
                .sum();
    }

    public boolean verificarDisponibilidade(int quantidade) {
        return getQuantidadeTotalDisponivel() >= quantidade;
    }

    // Getters
    public String getId() { return id; }
    public String getProdutoId() { return produtoId; }
    public List<ItemEstoque> getItensEstoque() {
        return Collections.unmodifiableList(itensEstoque);
    }
    public List<MovimentacaoEstoque> getMovimentacoes() {
        return Collections.unmodifiableList(movimentacoes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estoque estoque = (Estoque) o;
        return id.equals(estoque.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}