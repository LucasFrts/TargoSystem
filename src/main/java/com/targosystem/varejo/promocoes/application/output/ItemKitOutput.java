package com.targosystem.varejo.promocoes.application.output;

import com.targosystem.varejo.promocoes.domain.model.KitPromocional;
import com.targosystem.varejo.promocoes.domain.model.ItemKit;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record ItemKitOutput(String produtoId, int quantidade) {
    public static ItemKitOutput fromDomain(ItemKit item) {
        return new ItemKitOutput(item.getProdutoId(), item.getQuantidade());
    }
}
