package com.targosystem.varejo.produtos.domain.service;

import com.targosystem.varejo.produtos.domain.model.Categoria;
import com.targosystem.varejo.produtos.domain.model.Produto;
import com.targosystem.varejo.produtos.domain.repository.CategoriaRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;
import java.util.Optional;

public class ClassificadorProduto {

    private final CategoriaRepository categoriaRepository;

    public ClassificadorProduto(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = Objects.requireNonNull(categoriaRepository, "CategoriaRepository cannot be null");
    }

    /**
     * Tenta encontrar uma categoria existente pelo nome ou cria uma nova se não existir.
     * Retorna a categoria para ser associada a um produto.
     * @param nomeCategoria O nome da categoria.
     * @param descricaoCategoria A descrição da categoria (usada se for criar uma nova).
     * @return A categoria existente ou recém-criada.
     * @throws DomainException Se o nome da categoria for inválido.
     */
    public Categoria obterOuCriarCategoria(String nomeCategoria, String descricaoCategoria) {
        Objects.requireNonNull(nomeCategoria, "Category name cannot be null for classification.");

        Optional<Categoria> categoriaExistente = categoriaRepository.findByNome(nomeCategoria);

        if (categoriaExistente.isPresent()) {
            return categoriaExistente.get();
        } else {

            Categoria novaCategoria = new Categoria(
                    nomeCategoria,
                    descricaoCategoria // Pode ser null ou vazio se não fornecido
            );

            return categoriaRepository.save(novaCategoria);
        }
    }

    /**
     * Reclassifica um produto para uma nova categoria.
     * @param produto O produto a ser reclassificado.
     * @param novaCategoria A nova categoria para o produto.
     * @throws DomainException Se a nova categoria for nula.
     */
    public void reclassificarProduto(Produto produto, Categoria novaCategoria) {
        Objects.requireNonNull(produto, "Product cannot be null");
        Objects.requireNonNull(novaCategoria, "New category cannot be null");
    }
}