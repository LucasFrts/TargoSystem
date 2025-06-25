// src/main/java/com/targosystem/varejo/produtos/domain/events/ProdutoCadastradoEvent.java
package com.targosystem.varejo.produtos.domain.events;

import com.targosystem.varejo.produtos.domain.model.ProdutoId;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Evento de domínio que representa o cadastro de um novo produto.
 * Publicado após o produto ser persistido com sucesso.
 */
public class ProdutoCadastradoEvent {
    private final ProdutoId produtoId;
    private final String nomeProduto;
    private final LocalDateTime ocorreuEm;

    public ProdutoCadastradoEvent(ProdutoId produtoId, String nomeProduto) {
        this.produtoId = Objects.requireNonNull(produtoId, "ProdutoId cannot be null for ProdutoCadastradoEvent");
        this.nomeProduto = Objects.requireNonNull(nomeProduto, "NomeProduto cannot be null for ProdutoCadastradoEvent");
        this.ocorreuEm = LocalDateTime.now();
    }

    public ProdutoId getProdutoId() {
        return produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public LocalDateTime getOcorreuEm() {
        return ocorreuEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProdutoCadastradoEvent that = (ProdutoCadastradoEvent) o;
        return produtoId.equals(that.produtoId) && nomeProduto.equals(that.nomeProduto) && ocorreuEm.equals(that.ocorreuEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produtoId, nomeProduto, ocorreuEm);
    }

    @Override
    public String toString() {
        return "ProdutoCadastradoEvent{" +
                "produtoId=" + produtoId +
                ", nomeProduto='" + nomeProduto + '\'' +
                ", ocorreuEm=" + ocorreuEm +
                '}';
    }
}