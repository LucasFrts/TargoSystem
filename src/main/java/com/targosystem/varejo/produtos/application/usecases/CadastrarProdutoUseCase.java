package com.targosystem.varejo.produtos.application.usecases;

import com.targosystem.varejo.produtos.application.input.CadastrarProdutoInput;
import com.targosystem.varejo.produtos.application.output.ProdutoOutput;
import com.targosystem.varejo.produtos.domain.events.ProdutoCadastradoEvent;
import com.targosystem.varejo.produtos.domain.model.Categoria;
import com.targosystem.varejo.produtos.domain.model.Produto;
import com.targosystem.varejo.produtos.domain.repository.CategoriaRepository;
import com.targosystem.varejo.produtos.domain.repository.ProdutoRepository;
import com.targosystem.varejo.produtos.domain.service.ClassificadorProduto;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher; // Para eventos de domínio
import java.util.Objects;

public class CadastrarProdutoUseCase {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ClassificadorProduto classificadorProduto;
    private final EventPublisher eventPublisher; // Para publicar eventos após o cadastro

    public CadastrarProdutoUseCase(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository, ClassificadorProduto classificadorProduto, EventPublisher eventPublisher) {
        this.produtoRepository = Objects.requireNonNull(produtoRepository, "ProdutoRepository cannot be null");
        this.categoriaRepository = Objects.requireNonNull(categoriaRepository, "CategoriaRepository cannot be null");
        this.classificadorProduto = Objects.requireNonNull(classificadorProduto, "ClassificadorProduto cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null");
    }

    public ProdutoOutput execute(CadastrarProdutoInput input) {
        // 1. Validar input (nível de aplicação - formato, nulos simples)
        Objects.requireNonNull(input.nome(), "Product name cannot be null");
        Objects.requireNonNull(input.codigoBarras(), "Barcode cannot be null");
        Objects.requireNonNull(input.precoSugerido(), "Suggested price cannot be null");
        Objects.requireNonNull(input.nomeCategoria(), "Category name cannot be null");

        // 2. Verificar regras de negócio de unicidade (RF01 - Código de Barras Único)
        if (produtoRepository.existsByCodigoBarras(input.codigoBarras())) {
            throw new DomainException("Product with barcode " + input.codigoBarras() + " already exists.");
        }
        if (categoriaRepository.existsByName(input.nomeCategoria()) && input.descricaoCategoria() != null && !input.descricaoCategoria().isBlank()) {
            // Regra: se a categoria já existe, a descrição não deve ser passada ou deve ser compatível.
            // Para simplificar, estamos permitindo que a descrição seja "ignorada" se a categoria já existe.
            // Uma regra mais forte poderia exigir que a descrição fosse nula ou igual à existente.
        }

        // 3. Obter ou criar categoria usando o serviço de domínio
        Categoria categoria = classificadorProduto.obterOuCriarCategoria(input.nomeCategoria(), input.descricaoCategoria());
        if (categoria.getId() == null) { // Se a categoria foi "criada" (sem ID), ela precisa ser persistida
            categoria = categoriaRepository.save(categoria); // Persiste a nova categoria
        }

        // 4. Criar o objeto de domínio Produto
        Produto novoProduto = new Produto(
                input.nome(),
                input.descricao(),
                input.codigoBarras(),
                categoria,
                input.marca(),
                input.precoSugerido()
        );

        // 5. Persistir o produto
        Produto produtoSalvo = produtoRepository.save(novoProduto);

        // 6. Publicar evento de domínio (se houver, por exemplo, ProdutoCadastradoEvent)
        eventPublisher.publish(new ProdutoCadastradoEvent(produtoSalvo.getId(), produtoSalvo.getNome()));

        // 7. Retornar DTO de saída
        return ProdutoOutput.from(produtoSalvo);
    }
}