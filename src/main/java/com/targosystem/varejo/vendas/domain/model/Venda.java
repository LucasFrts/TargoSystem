package com.targosystem.varejo.vendas.domain.model;

import com.targosystem.varejo.shared.domain.AggregateRoot;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.clientes.domain.model.Cliente;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Venda implements AggregateRoot {
    private VendaId id;
    private Cliente cliente;
    private List<ItemVenda> itens;
    private BigDecimal valorTotal;
    private BigDecimal valorDesconto;
    private BigDecimal valorFinal;
    private String status; // Ex: CRIADA, CONCLUIDA, CANCELADA
    private LocalDateTime dataVenda;
    private LocalDateTime dataAtualizacao;

    public Venda(Cliente cliente, List<ItemVenda> itens, BigDecimal valorDesconto) {
        this.id = VendaId.generate();
        setCliente(cliente);
        this.itens = new ArrayList<>();
        setItens(itens);
        this.valorDesconto = BigDecimal.ZERO;
        calcularTotais();
        this.status = "CRIADA";
        this.dataVenda = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();

        if (valorDesconto != null && valorDesconto.compareTo(BigDecimal.ZERO) > 0) {
            aplicarDesconto(valorDesconto);
        }
    }

    public Venda(VendaId id, Cliente cliente, List<ItemVenda> itens,
                 BigDecimal valorTotal, BigDecimal valorDesconto, BigDecimal valorFinal,
                 String status, LocalDateTime dataVenda, LocalDateTime dataAtualizacao) {
        this.id = Objects.requireNonNull(id, "ID da venda não pode ser nulo.");
        this.cliente = Objects.requireNonNull(cliente, "Cliente da venda não pode ser nulo.");
        this.itens = new ArrayList<>();
        setItens(itens);
        this.valorTotal = Objects.requireNonNull(valorTotal, "Valor total não pode ser nulo.");
        this.valorDesconto = Objects.requireNonNull(valorDesconto, "Valor desconto não pode ser nulo.");
        this.valorFinal = Objects.requireNonNull(valorFinal, "Valor final não pode ser nulo.");
        this.status = Objects.requireNonNull(status, "Status da venda não pode ser nulo.");
        this.dataVenda = Objects.requireNonNull(dataVenda, "Data da venda não pode ser nula.");
        this.dataAtualizacao = Objects.requireNonNull(dataAtualizacao, "Data de atualização não pode ser nula.");
    }

    // Getters
    public VendaId getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public List<ItemVenda> getItens() { return Collections.unmodifiableList(itens); }
    public BigDecimal getValorTotal() { return valorTotal; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public BigDecimal getValorFinal() { return valorFinal; }
    public String getStatus() { return status; }
    public LocalDateTime getDataVenda() { return dataVenda; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    // Setters com validação (se aplicável, para comportamentos de domínio)
    private void setCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "O cliente da venda não pode ser nulo.");
        this.cliente = cliente;
    }

    private void setItens(List<ItemVenda> novosItens) {
        Objects.requireNonNull(novosItens, "A lista de itens não pode ser nula.");
        if (novosItens.isEmpty()) {
            throw new DomainException("Uma venda deve ter pelo menos um item.");
        }
        this.itens.clear();
        novosItens.forEach(this::addItem);
        calcularTotais();
    }

    public void aplicarDesconto(BigDecimal novoValorDesconto) {
        Objects.requireNonNull(novoValorDesconto, "O valor do desconto não pode ser nulo.");
        if (novoValorDesconto.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Valor de desconto não pode ser negativo.");
        }
        if (!"CRIADA".equals(this.status)) { // Example business rule: Only apply discount to a 'CRIADA' sale
            throw new DomainException("Não é possível aplicar desconto em venda com status: " + this.status);
        }

        this.valorDesconto = novoValorDesconto;
        calcularTotais();
        this.dataAtualizacao = LocalDateTime.now();
    }

    
    public void addItem(ItemVenda item) {
        Objects.requireNonNull(item, "Item de venda não pode ser nulo.");
        this.itens.add(item);
        calcularTotais();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void removeItem(ItemVenda item) {
        if (this.itens.remove(item)) {
            calcularTotais();
            this.dataAtualizacao = LocalDateTime.now();
        } else {
            throw new DomainException("Item não encontrado na venda.");
        }
    }

    public void concluirVenda() {
        if (!"CRIADA".equals(this.status)) {
            throw new DomainException("A venda só pode ser concluída se estiver no status CRIADA.");
        }
        this.status = "CONCLUIDA";
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void cancelarVenda() {
        if ("CONCLUIDA".equals(this.status)) {
            throw new DomainException("Venda concluída não pode ser cancelada.");
        }
        this.status = "CANCELADA";
        this.dataAtualizacao = LocalDateTime.now();
    }

    private void calcularTotais() {
        BigDecimal subtotal = itens.stream()
                .map(ItemVenda::getPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.valorTotal = subtotal;
        this.valorFinal = subtotal.subtract(this.valorDesconto);

        if (this.valorFinal.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Valor final da venda não pode ser negativo após o desconto.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venda venda = (Venda) o;
        return Objects.equals(id, venda.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}