package com.targosystem.varejo.vendas.infra.persistence.entity;

import com.targosystem.varejo.vendas.domain.model.Venda;
import com.targosystem.varejo.vendas.domain.model.VendaId;
import com.targosystem.varejo.clientes.infra.persistence.entity.ClienteJpaEntity; // Importe ClienteJpaEntity
import com.targosystem.varejo.clientes.domain.model.Cliente; // Importe Cliente do domínio

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "vendas")
public class VendaJpaEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY) // Many VendaJpaEntity to One ClienteJpaEntity
    @JoinColumn(name = "cliente_id", nullable = false) // Coluna de FK para a tabela de clientes
    private ClienteJpaEntity cliente; // Referência à entidade JPA do Cliente

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVendaJpaEntity> itens = new ArrayList<>(); // Inicializar para evitar NullPointerException

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "valor_desconto", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDesconto;

    @Column(name = "valor_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorFinal;

    @Column(name = "status_venda", nullable = false, length = 20)
    private String status;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    protected VendaJpaEntity() {}

    // Construtor para JPA (ajustado para ClienteJpaEntity)
    public VendaJpaEntity(String id, ClienteJpaEntity cliente,
                          BigDecimal valorTotal, BigDecimal valorDesconto, BigDecimal valorFinal,
                          String status, LocalDateTime dataVenda, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.cliente = Objects.requireNonNull(cliente, "ClienteJpaEntity cannot be null.");
        this.valorTotal = valorTotal;
        this.valorDesconto = valorDesconto;
        this.valorFinal = valorFinal;
        this.status = status;
        this.dataVenda = dataVenda;
        this.dataAtualizacao = dataAtualizacao;
        // A lista de itens será populada via addIten ou setItens
    }

    public static VendaJpaEntity fromDomain(Venda venda, ClienteJpaEntity clienteJpaEntity) {
        // Primeiro, crie a instância de VendaJpaEntity
        VendaJpaEntity vendaJpa = new VendaJpaEntity(
                venda.getId().value(),
                clienteJpaEntity, // Passe o ClienteJpaEntity aqui
                venda.getValorTotal(),
                venda.getValorDesconto(),
                venda.getValorFinal(),
                venda.getStatus(),
                venda.getDataVenda(),
                venda.getDataAtualizacao()
        );

        // Mapeie os itens de venda e associe-os à vendaJpa recém-criada
        // Use o helper method addIten para manter a bidirecionalidade
        venda.getItens().forEach(itemDomain -> {
            ItemVendaJpaEntity itemJpa = ItemVendaJpaEntity.fromDomain(itemDomain, vendaJpa);
            vendaJpa.addIten(itemJpa); // Adiciona e seta a referência venda
        });

        return vendaJpa;
    }


    public Venda toDomain() {
        return new Venda(
                new VendaId(this.id),
                this.cliente.toDomain(), // Converte ClienteJpaEntity para Cliente do domínio
                this.itens.stream()
                        .map(ItemVendaJpaEntity::toDomain)
                        .collect(Collectors.toList()),
                this.valorTotal,
                this.valorDesconto,
                this.valorFinal,
                this.status,
                this.dataVenda,
                this.dataAtualizacao
        );
    }

    // Getters and Setters (atualizados para ClienteJpaEntity e lista de itens)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public ClienteJpaEntity getCliente() { return cliente; }
    public void setCliente(ClienteJpaEntity cliente) { this.cliente = cliente; }
    public List<ItemVendaJpaEntity> getItens() { return itens; }
    public void setItens(List<ItemVendaJpaEntity> itens) { this.itens = itens; } // Cuidado com a bidirecionalidade aqui se usar diretamente
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }
    public BigDecimal getValorFinal() { return valorFinal; }
    public void setValorFinal(BigDecimal valorFinal) { this.valorFinal = valorFinal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    // Helper methods to manage bi-directional relationship (important for OneToMany/ManyToOne)
    public void addIten(ItemVendaJpaEntity item) {
        if (item == null) {
            return;
        }
        if (itens == null) {
            itens = new ArrayList<>();
        }
        itens.add(item);
        item.setVenda(this); // Set the parent VendaJpaEntity on the child item
    }

    public void removeIten(ItemVendaJpaEntity item) {
        if (item == null || itens == null) {
            return;
        }
        itens.remove(item);
        item.setVenda(null); // Clear the parent VendaJpaEntity on the child item
    }
}