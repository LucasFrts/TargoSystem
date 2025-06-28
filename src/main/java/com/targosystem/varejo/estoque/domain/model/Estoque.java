package com.targosystem.varejo.estoque.domain.model;

import com.targosystem.varejo.shared.domain.AggregateRoot;
import com.targosystem.varejo.shared.domain.DomainException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Estoque implements AggregateRoot {

    private String id;
    private String produtoId;
    private LocalEstoque localEstoque;
    private List<ItemEstoque> itensEstoque;

    public Estoque(String produtoId, LocalEstoque localEstoque) {
        this.id = UUID.randomUUID().toString();
        this.produtoId = Objects.requireNonNull(produtoId, "ID do produto não pode ser nulo para o estoque.");
        this.localEstoque = Objects.requireNonNull(localEstoque, "Local de estoque não pode ser nulo.");
        this.itensEstoque = new ArrayList<>();
    }

    public Estoque(String produtoId) {
        // Este construtor é problemático se Estoque SEMPRE deve ter um LocalEstoque.
        // Se este construtor é usado para criar um Estoque temporário sem LocalEstoque,
        // ele precisará ser atribuído um LocalEstoque antes de ser salvo ou usado para operações.
        // REAVALIAR se este construtor é realmente necessário ou se ele deve exigir um LocalEstoque.
        // Por ora, vamos mantê-lo mas o fluxo do Usecase usará o outro.
        this.id = UUID.randomUUID().toString(); // Adicionado para evitar NPE em getId() se usado.
        this.produtoId = produtoId;
        this.itensEstoque = new ArrayList<>();
        this.localEstoque = null; // Precisa ser setado posteriormente
    }


    public Estoque(String id, String produtoId, LocalEstoque localEstoque, List<ItemEstoque> itensEstoque) {
        this.id = Objects.requireNonNull(id);
        this.produtoId = Objects.requireNonNull(produtoId);
        this.localEstoque = Objects.requireNonNull(localEstoque);
        this.itensEstoque = (itensEstoque != null) ? new ArrayList<>(itensEstoque) : new ArrayList<>();
    }

    // === Comportamentos de Domínio ===

    /**
     * Adiciona uma quantidade de um produto com um Lote específico a este estoque.
     * Esta é a abordagem correta para entradas que exigem detalhes de lote.
     * Será usada no RegistrarMovimentacaoEstoqueUseCase para ENTRADAS.
     */
    public void adicionarQuantidadeComLote(int quantidade, Lote lote) {
        if (quantidade <= 0) {
            throw new DomainException("A quantidade para adicionar deve ser positiva.");
        }
        Objects.requireNonNull(lote, "Lote é obrigatório para adicionar itens ao estoque.");
        // Assumindo uma localização padrão, ou que a localização será um parâmetro também se necessário
        LocalizacaoArmazenamento defaultLocation = LocalizacaoArmazenamento.defaultLocation(); // Usar um default ou passar como parâmetro

        // Tenta encontrar um item existente com o mesmo lote e localização para somar
        Optional<ItemEstoque> existingItem = this.itensEstoque.stream()
                .filter(item -> item.getLote().equals(lote) && item.getLocalizacao().equals(defaultLocation))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().adicionarQuantidade(quantidade);
        } else {
            ItemEstoque novoItem = new ItemEstoque(
                    this.produtoId,
                    quantidade,
                    lote,
                    defaultLocation, // Usar localização padrão ou recebê-la
                    this.id,
                    this.localEstoque.getId()
            );
            this.itensEstoque.add(novoItem);
        }
    }

    public void removerItens(int quantidade, String motivo) {
        removerQuantidadeTotal(quantidade);
    }

    /**
     * Remove uma quantidade total do estoque, priorizando a remoção de itens existentes.
     * Permanece como está, pois para saída não há necessidade de especificar o lote,
     * a lógica de remoção é interna ao agregado.
     */
    public void removerQuantidadeTotal(int quantidade) {
        if (quantidade <= 0) {
            throw new DomainException("A quantidade para remover deve ser positiva.");
        }
        if (getQuantidadeTotalDisponivel() < quantidade) {
            throw new DomainException("Estoque insuficiente para remover " + quantidade + " unidades. Disponível: " + getQuantidadeTotalDisponivel());
        }

        int quantidadeRestante = quantidade;
        // Remove do primeiro item disponível até esgotar a quantidade (simplificado)
        // Para um sistema real, considere FIFO/LIFO ou por lote/validade específicos
        for (ItemEstoque item : new ArrayList<>(itensEstoque)) { // Criar cópia para evitar ConcurrentModificationException
            if (quantidadeRestante <= 0) break;

            int removidoDoItem = Math.min(quantidadeRestante, item.getQuantidade());
            item.removerQuantidade(removidoDoItem);
            quantidadeRestante -= removidoDoItem;
        }

        // Remove itens que ficaram com quantidade zero
        this.itensEstoque.removeIf(item -> item.getQuantidade() == 0);
    }

    /**
     * Adiciona uma quantidade de um produto com um Lote e Localização específicos a este estoque.
     * Esta é a abordagem ideal para entradas que exigem detalhes completos de lote e localização.
     * Será usada no RegistrarEntradaEstoqueUseCase.
     * O motivo é mais para registro e não afeta a lógica do agregado de estoque diretamente.
     */
    public void adicionarItens(int quantidade, Lote lote, LocalizacaoArmazenamento localizacao, String motivo) {
        if (quantidade <= 0) {
            throw new DomainException("A quantidade para adicionar deve ser positiva.");
        }
        Objects.requireNonNull(lote, "Lote é obrigatório para adicionar itens ao estoque.");
        Objects.requireNonNull(localizacao, "Localização de armazenamento é obrigatória para adicionar itens ao estoque.");
        // O 'motivo' pode ser nulo ou vazio, dependendo da regra de negócio.
        // if (motivo == null || motivo.trim().isEmpty()) {
        //     throw new DomainException("Motivo da movimentação é obrigatório.");
        // }


        // Tenta encontrar um item existente com o mesmo lote e localização para somar
        Optional<ItemEstoque> existingItem = this.itensEstoque.stream()
                .filter(item -> item.getLote().equals(lote) && item.getLocalizacao().equals(localizacao))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().adicionarQuantidade(quantidade);
        } else {
            ItemEstoque novoItem = new ItemEstoque(
                    this.produtoId,
                    quantidade,
                    lote,
                    localizacao,
                    this.id, // estoqueId
                    this.localEstoque.getId() // localEstoqueId
            );
            this.itensEstoque.add(novoItem);
        }
        // O 'motivo' seria registrado em um evento de domínio ou log de movimentação,
        // não diretamente no estado do ItemEstoque ou Estoque.
    }
    // Getters
    public String getId() { return id; }
    public String getProdutoId() { return produtoId; }
    public LocalEstoque getLocalEstoque() { return localEstoque; }
    public List<ItemEstoque> getItensEstoque() {
        return Collections.unmodifiableList(itensEstoque);
    }

    public int getQuantidadeTotalDisponivel() {
        return itensEstoque.stream().mapToInt(ItemEstoque::getQuantidade).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estoque estoque = (Estoque) o;
        return Objects.equals(produtoId, estoque.produtoId) && Objects.equals(localEstoque, estoque.localEstoque);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produtoId, localEstoque);
    }
}