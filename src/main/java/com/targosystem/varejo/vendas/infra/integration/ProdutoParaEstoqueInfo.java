package com.targosystem.varejo.vendas.infra.integration;

/**
 * Represents the minimal information about a product needed for stock operations.
 * This record serves as an internal DTO for integration events, decoupling from ItemVenda.
 */
public record ProdutoParaEstoqueInfo(String idProduto, int quantidade) {}

