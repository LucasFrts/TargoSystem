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
import com.targosystem.varejo.shared.infra.EventPublisher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Objects;

public class CadastrarProdutoUseCase {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ClassificadorProduto classificadorProduto;
    private final EventPublisher eventPublisher;
    private final EntityManager entityManager;

    public CadastrarProdutoUseCase(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository, ClassificadorProduto classificadorProduto, EventPublisher eventPublisher, EntityManager entityManager) {
        this.produtoRepository = Objects.requireNonNull(produtoRepository, "ProdutoRepository cannot be null");
        this.categoriaRepository = Objects.requireNonNull(categoriaRepository, "CategoriaRepository cannot be null");
        this.classificadorProduto = Objects.requireNonNull(classificadorProduto, "ClassificadorProduto cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null");
        this.entityManager = Objects.requireNonNull(entityManager, "EntityManager cannot be null");
    }

    public ProdutoOutput execute(CadastrarProdutoInput input) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean newTransaction = false;

        try {
            if (!transaction.isActive()) {
                transaction.begin();
                newTransaction = true;
            }

            Objects.requireNonNull(input.nome(), "Product name cannot be null");
            Objects.requireNonNull(input.codigoBarras(), "Barcode cannot be null");
            Objects.requireNonNull(input.precoSugerido(), "Suggested price cannot be null");
            Objects.requireNonNull(input.nomeCategoria(), "Category name cannot be null");

            if (produtoRepository.existsByCodigoBarras(input.codigoBarras())) {
                throw new DomainException("Product with barcode " + input.codigoBarras() + " already exists.");
            }

            Categoria categoria = classificadorProduto.obterOuCriarCategoria(input.nomeCategoria(), input.descricaoCategoria());

            Produto novoProduto = new Produto(
                    input.nome(),
                    input.descricao(),
                    input.precoSugerido().getValue(),
                    input.codigoBarras(),
                    categoria,
                    input.marca()
            );

            Produto produtoSalvo = produtoRepository.save(novoProduto);

            if (newTransaction) {
                transaction.commit();
            }

            eventPublisher.publish(new ProdutoCadastradoEvent(produtoSalvo.getId().getValue(), produtoSalvo.getNome()));

            return ProdutoOutput.from(produtoSalvo);
        } catch (DomainException e) {
            if (transaction != null && transaction.isActive() && newTransaction) {
                transaction.rollback();
            }
            throw e;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive() && newTransaction) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DomainException("Erro ao cadastrar produto: " + e.getMessage(), e);
        }
    }
}