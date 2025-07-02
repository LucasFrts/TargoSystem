package com.targosystem.varejo.vendas.application.output;

import com.targosystem.varejo.vendas.domain.model.Venda;
import com.targosystem.varejo.clientes.application.output.ClienteOutput;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record VendaOutput(
        String id,
        ClienteOutput cliente,
        List<ItemVendaOutput> itens,
        String valorTotal,
        String valorDesconto,
        String valorFinal,
        String status,
        LocalDateTime dataVenda,
        LocalDateTime dataAtualizacao
) {
    
    public static VendaOutput from(Venda venda) {
        List<ItemVendaOutput> itensOutput = venda.getItens().stream()
                .map(ItemVendaOutput::from)
                .collect(Collectors.toList());

        return new VendaOutput(
                venda.getId().value(),
                ClienteOutput.from(venda.getCliente()),
                itensOutput,
                venda.getValorTotal().toPlainString(),
                venda.getValorDesconto().toPlainString(),
                venda.getValorFinal().toPlainString(),
                venda.getStatus(),
                venda.getDataVenda(),
                venda.getDataAtualizacao()
        );
    }
}