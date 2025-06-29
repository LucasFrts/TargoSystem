package com.targosystem.varejo.produtos.domain.service;

import com.targosystem.varejo.produtos.domain.model.Categoria;
import com.targosystem.varejo.produtos.domain.model.Produto;
import com.targosystem.varejo.produtos.domain.repository.CategoriaRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;
import java.util.Optional;

// Serviço de domínio para gerenciar a classificação de produtos em categorias
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
            return categoriaExistente.get(); // Retorna a categoria existente (já gerenciada pelo EM se foi buscada na transação atual)
        } else {
            // Se a categoria não existe, cria uma nova
            Categoria novaCategoria = new Categoria(
                    nomeCategoria,
                    descricaoCategoria // Pode ser null ou vazio se não fornecido
            );
            // Salva a nova categoria. O CategoriaDao.save() agora usa merge e garante
            // que a instância retornada é a gerenciada.
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
        // A lógica de setar a categoria no produto já está em Produto.java
        // Aqui, o serviço de domínio garante que a Categoria é válida e existe ou foi criada
        // Antes de ser associada ao produto.
        // Se a Categoria for uma entidade separada com ciclo de vida próprio,
        // o CategoriaRepository seria usado para buscar/salvar aqui.
        // Como Categoria é VO aqui, a associação é direta no Produto.
        // produto.setCategoria(novaCategoria); // Não é um setter público em Produto
        // O UseCase chamaria o método de atualização do produto.
    }
}