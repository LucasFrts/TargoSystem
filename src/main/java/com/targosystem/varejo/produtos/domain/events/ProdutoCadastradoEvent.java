// src/main/java/com/targosystem/varejo/produtos/domain/events/ProdutoCadastradoEvent.java
package com.targosystem.varejo.produtos.domain.events;

import com.targosystem.varejo.shared.domain.DomainEvent; // Importar DomainEvent
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Evento de domínio que sinaliza que um novo produto foi cadastrado.
 */
public class ProdutoCadastradoEvent implements DomainEvent { // <--- Verificar esta linha crucial

    private final String produtoId;
    private final String nomeProduto;
    private final LocalDateTime occurredOn;

    public ProdutoCadastradoEvent(String produtoId, String nomeProduto) {
        this.produtoId = Objects.requireNonNull(produtoId, "Product ID cannot be null");
        this.nomeProduto = Objects.requireNonNull(nomeProduto, "Product name cannot be null");
        this.occurredOn = LocalDateTime.now();
    }

    @Override
    public LocalDateTime getOcorreuEm() { // Certifique-se de que o nome é o mesmo da interface
        return occurredOn; // Retorna o valor que você já está armazenando
    }

    public String getProdutoId() {
        return produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String toString() {
        return "ProdutoCadastradoEvent{" +
                "produtoId='" + produtoId + '\'' +
                ", nomeProduto='" + nomeProduto + '\'' +
                ", occurredOn=" + occurredOn +
                '}';
    }
}