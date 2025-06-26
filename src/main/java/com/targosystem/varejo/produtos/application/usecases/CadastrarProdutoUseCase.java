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

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

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
        boolean newTransaction = false; // Flag para saber se esta Use Case iniciou a transação

        try {
            if (!transaction.isActive()) {
                transaction.begin(); // Inicia a transação se não houver uma ativa
                newTransaction = true;
            }

            Objects.requireNonNull(input.nome(), "Product name cannot be null");
            Objects.requireNonNull(input.codigoBarras(), "Barcode cannot be null");
            Objects.requireNonNull(input.precoSugerido(), "Suggested price cannot be null");
            Objects.requireNonNull(input.nomeCategoria(), "Category name cannot be null");

            // 2. Verificar regras de negócio de unicidade (RF01 - Código de Barras Único)
            if (produtoRepository.existsByCodigoBarras(input.codigoBarras())) {
                throw new DomainException("Product with barcode " + input.codigoBarras() + " already exists.");
            }

            Categoria categoria = classificadorProduto.obterOuCriarCategoria(input.nomeCategoria(), input.descricaoCategoria());

            Produto novoProduto = new Produto(
                    input.nome(),
                    input.descricao(),
                    input.precoSugerido().getValue(),
                    input.codigoBarras(),
                    categoria, // **Esta é a instância de Categoria que deve ser gerenciada pelo EM**
                    input.marca()
            );

            // 5. Persistir o produto.
            // Assumimos que produtoRepository.save() vai lidar corretamente com a entidade
            // Produto e sua associação com Categoria (já gerenciada).
            Produto produtoSalvo = produtoRepository.save(novoProduto);

            if (newTransaction) {
                transaction.commit(); // Confirma a transação se foi iniciada por este Use Case
            }

            // 6. Publicar evento de domínio
            eventPublisher.publish(new ProdutoCadastradoEvent(produtoSalvo.getId().getValue(), produtoSalvo.getNome()));

            // 7. Retornar DTO de saída
            return ProdutoOutput.from(produtoSalvo);
        } catch (DomainException e) {
            if (transaction != null && transaction.isActive() && newTransaction) { // Só faz rollback se esta transação foi iniciada aqui
                transaction.rollback();
            }
            throw e;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive() && newTransaction) { // Só faz rollback se esta transação foi iniciada aqui
                transaction.rollback();
            }
            e.printStackTrace(); // Para depuração
            throw new DomainException("Erro ao cadastrar produto: " + e.getMessage(), e);
        }
    }
}