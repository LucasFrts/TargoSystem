package com.targosystem.varejo.vendas.application.output;

import com.targosystem.varejo.vendas.domain.model.Venda;
import com.targosystem.varejo.clientes.application.output.ClienteOutput; // Importe ClienteOutput

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record VendaOutput(
        String id,
        ClienteOutput cliente, // Cliente como ClienteOutput
        List<ItemVendaOutput> itens,
        String valorTotal, // String para formatação
        String valorDesconto, // String para formatação
        String valorFinal, // String para formatação
        String status,
        LocalDateTime dataVenda,
        LocalDateTime dataAtualizacao
) {
    // O método de conversão de domínio para output DTO agora é 'from'
    public static VendaOutput from(Venda venda) {
        List<ItemVendaOutput> itensOutput = venda.getItens().stream()
                .map(ItemVendaOutput::from)
                .collect(Collectors.toList());

        return new VendaOutput(
                venda.getId().value(),
                ClienteOutput.from(venda.getCliente()), // Mapear Cliente de domínio para ClienteOutput
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