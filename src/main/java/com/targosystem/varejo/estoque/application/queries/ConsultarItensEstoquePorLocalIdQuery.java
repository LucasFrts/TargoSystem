package com.targosystem.varejo.estoque.application.queries;

import com.targosystem.varejo.estoque.application.output.ItemEstoqueOutput;
import com.targosystem.varejo.estoque.domain.repository.ItemEstoqueRepository; // Assumindo que você tem este repositório
import com.targosystem.varejo.shared.domain.DomainException; // Se a query puder lançar uma exceção de domínio

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConsultarItensEstoquePorLocalIdQuery {

    private final ItemEstoqueRepository itemEstoqueRepository;

    public ConsultarItensEstoquePorLocalIdQuery(ItemEstoqueRepository itemEstoqueRepository) {
        this.itemEstoqueRepository = Objects.requireNonNull(itemEstoqueRepository, "ItemEstoqueRepository cannot be null.");
    }

    /**
     * Executa a consulta para obter todos os itens de estoque em um local específico.
     * @param localEstoqueId O ID do local de estoque.
     * @return Uma lista de ItemEstoqueOutput.
     * @throws DomainException Se o local de estoque não for encontrado ou houver outro erro de domínio.
     */
    public List<ItemEstoqueOutput> execute(String localEstoqueId) throws DomainException {
        // Implementação real buscaria no ItemEstoqueRepository por localEstoqueId
        // e mapearia os resultados para ItemEstoqueOutput.
        // Você precisará ter um método findByLocalEstoqueId no seu ItemEstoqueRepository.
        return itemEstoqueRepository.findByLocalEstoqueId(localEstoqueId).stream()
                .map(ItemEstoqueOutput::fromDomain)
                .collect(Collectors.toList());
    }
}