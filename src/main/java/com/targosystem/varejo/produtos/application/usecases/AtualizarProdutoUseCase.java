package com.targosystem.varejo.produtos.application.usecases;

import com.targosystem.varejo.produtos.application.input.AtualizarProdutoInput;
import com.targosystem.varejo.produtos.application.output.ProdutoOutput;
import com.targosystem.varejo.produtos.domain.model.Categoria;
import com.targosystem.varejo.produtos.domain.model.Produto;
import com.targosystem.varejo.produtos.domain.repository.CategoriaRepository;
import com.targosystem.varejo.produtos.domain.repository.ProdutoRepository;
import com.targosystem.varejo.produtos.domain.service.ClassificadorProduto;
import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;

public class AtualizarProdutoUseCase {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ClassificadorProduto classificadorProduto;

    public AtualizarProdutoUseCase(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository, ClassificadorProduto classificadorProduto) {
        this.produtoRepository = Objects.requireNonNull(produtoRepository, "ProdutoRepository cannot be null");
        this.categoriaRepository = Objects.requireNonNull(categoriaRepository, "CategoriaRepository cannot be null");
        this.classificadorProduto = Objects.requireNonNull(classificadorProduto, "ClassificadorProduto cannot be null");
    }

    public ProdutoOutput execute(AtualizarProdutoInput input) {
        Objects.requireNonNull(input.id(), "Product ID for update cannot be null");
        Objects.requireNonNull(input.nome(), "Product name cannot be null");
        Objects.requireNonNull(input.codigoBarras(), "Barcode cannot be null");
        Objects.requireNonNull(input.precoSugerido(), "Suggested price cannot be null");
        Objects.requireNonNull(input.nomeCategoria(), "Category name cannot be null");

        Produto produtoExistente = produtoRepository.findById(input.id())
                .orElseThrow(() -> new DomainException("Product with ID " + input.id().getValue() + " not found."));

        // Verificar unicidade do código de barras se ele foi alterado e já existe em outro produto
        if (!produtoExistente.getCodigoBarras().equals(input.codigoBarras())) {
            if (produtoRepository.existsByCodigoBarras(input.codigoBarras())) {
                throw new DomainException("Another product with barcode " + input.codigoBarras() + " already exists.");
            }
        }

        // Obter ou criar categoria
        Categoria categoria = classificadorProduto.obterOuCriarCategoria(input.nomeCategoria(), input.descricaoCategoria());
        if (categoria.getId() == null) {
            categoria = categoriaRepository.save(categoria);
        }

        // Atualizar informações do produto (comportamento de domínio)
        produtoExistente.atualizarInformacoes(
                input.nome(),
                input.descricao(),
                input.codigoBarras(),
                categoria,
                input.marca(),
                input.precoSugerido()
        );

        // Persistir as mudanças
        Produto produtoAtualizado = produtoRepository.save(produtoExistente);

        return ProdutoOutput.from(produtoAtualizado);
    }
}