package com.targosystem.varejo.promocoes.application.output;

import com.targosystem.varejo.promocoes.domain.model.KitPromocional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record KitPromocionalOutput(
        String id,
        String nome,
        String descricao,
        BigDecimal precoFixoKit,
        List<ItemKitOutput> itens
) {
    public static KitPromocionalOutput fromDomain(KitPromocional kit) {
        return new KitPromocionalOutput(
                kit.getId(),
                kit.getNome(),
                kit.getDescricao(),
                kit.getPrecoFixoKit(),
                kit.getItens().stream()
                        .map(ItemKitOutput::fromDomain)
                        .collect(Collectors.toUnmodifiableList())
        );
    }
}
