package com.targosystem.varejo.produtos.application.query;

import com.targosystem.varejo.produtos.application.output.PrecoProdutoOutput;
import com.targosystem.varejo.produtos.domain.model.ProdutoId; // Assumindo que você tem um ProdutoId no domínio do Produto
import com.targosystem.varejo.produtos.domain.repository.ProdutoRepository; // Repositório de Produto
import com.targosystem.varejo.shared.domain.DomainException; // Usando a DomainException compartilhada

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Query para consultar o preço de venda de um produto no Bounded Context de Produto.
 */
public class ConsultarPrecoProdutoQuery {

    private static final Logger logger = LoggerFactory.getLogger(ConsultarPrecoProdutoQuery.class);

    private final ProdutoRepository produtoRepository;

    public ConsultarPrecoProdutoQuery(ProdutoRepository produtoRepository) {
        this.produtoRepository = Objects.requireNonNull(produtoRepository, "ProdutoRepository cannot be null.");
    }

    /**
     * Executa a consulta para obter o preço de um produto.
     * @param idProduto O ID do produto.
     * @return Um PrecoProdutoOutput contendo o ID do produto e seu preço.
     * @throws DomainException Se o produto ou seu preço não for encontrado.
     */
    public PrecoProdutoOutput execute(String idProduto) {
        logger.info("Consultando preço do produto com ID: {}", idProduto);

        // Assumindo que o Produto (domain model) tem um método getPrecoVenda()
        return produtoRepository.findById(new ProdutoId(idProduto))
                .map(produto -> new PrecoProdutoOutput(produto.getId().getValue(), produto.getPrecoVenda()))
                .orElseThrow(() -> {
                    logger.warn("Produto com ID {} não encontrado para consulta de preço.", idProduto);
                    return new DomainException("Produto não encontrado para consulta de preço: " + idProduto);
                });
    }
}