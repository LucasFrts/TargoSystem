package com.targosystem.varejo.estoque.application.queries; // Colocado em infra.queries, como é comum para implementações

import com.targosystem.varejo.estoque.application.output.EstoqueOutput;
import com.targosystem.varejo.estoque.domain.model.Estoque; // Assumindo que Estoque é a entidade agregadora
import com.targosystem.varejo.estoque.domain.repository.EstoqueRepository;
import com.targosystem.varejo.shared.domain.DomainException; // Importado se você usar
import java.util.Objects;
import java.util.Optional;

public class ConsultarEstoqueTotalPorProdutoIdQuery {

    private final EstoqueRepository estoqueRepository;

    public ConsultarEstoqueTotalPorProdutoIdQuery(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = Objects.requireNonNull(estoqueRepository, "EstoqueRepository cannot be null.");
    }

    public EstoqueOutput execute(String produtoId) throws DomainException {
        // Assume que EstoqueRepository.findByProdutoId(produtoId) retorna a entidade Estoque
        // que já encapsula a lógica de ter a quantidade total (somando seus ItemEstoques)
        Optional<Estoque> estoqueOptional = estoqueRepository.findByProdutoId(produtoId);

        if (estoqueOptional.isPresent()) {
            Estoque estoque = estoqueOptional.get();
            return EstoqueOutput.fromDomain(estoque);
        } else {
            // Se o Estoque para o produtoId não for encontrado, significa que não há itens desse produto em estoque.
            // Retornamos um EstoqueOutput com quantidade 0.
            // Alternativamente, você poderia lançar uma DomainException, dependendo da regra de negócio.
            // Para uma UI de consulta, retornar um "estoque vazio" é mais comum e amigável.
            return new EstoqueOutput(produtoId, null, null, null, 0, null); // produtoId, id do Estoque (null porque não existe), quantidade 0
        }
    }
}