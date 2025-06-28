package com.targosystem.varejo.estoque.application.queries;

import com.targosystem.varejo.estoque.domain.repository.ItemEstoqueRepository; // Assumindo que você tem este repositório
import com.targosystem.varejo.shared.domain.DomainException; // Se a query puder lançar uma exceção de domínio

import java.util.Objects;

public class ConsultarQuantidadeTotalProdutoEmLocalQuery {

    private final ItemEstoqueRepository itemEstoqueRepository;

    public ConsultarQuantidadeTotalProdutoEmLocalQuery(ItemEstoqueRepository itemEstoqueRepository) {
        this.itemEstoqueRepository = Objects.requireNonNull(itemEstoqueRepository, "ItemEstoqueRepository cannot be null.");
    }

    /**
     * Executa a consulta para obter a quantidade total de um produto em um local de estoque específico.
     * @param produtoId O ID do produto.
     * @param localEstoqueId O ID do local de estoque.
     * @return A quantidade total do produto naquele local.
     * @throws DomainException Se houver um erro de domínio.
     */
    public long execute(String produtoId, String localEstoqueId) throws DomainException {
        // Implementação real buscaria no ItemEstoqueRepository a soma das quantidades
        // de ItemEstoque para o produtoId e localEstoqueId.
        // Você precisará ter um método sumQuantityByProdutoIdAndLocalEstoqueId ou similar no seu ItemEstoqueRepository.
        // Por exemplo: return itemEstoqueRepository.sumQuantityByProdutoIdAndLocalEstoqueId(produtoId, localEstoqueId);
        // Ou, se o repositório retornar uma lista, você somaria aqui:
        return itemEstoqueRepository.findByProdutoIdAndLocalEstoqueId(produtoId, localEstoqueId).stream()
                .mapToLong(item -> item.getQuantidade())
                .sum();
    }
}