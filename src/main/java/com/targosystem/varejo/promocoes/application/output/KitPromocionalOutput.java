package com.targosystem.varejo.promocoes.application.output;

import com.targosystem.varejo.promocoes.domain.model.KitPromocional;
import com.targosystem.varejo.produtos.application.ProdutoService;
import com.targosystem.varejo.produtos.application.output.ProdutoOutput;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record KitPromocionalOutput(
        String id,
        String nome,
        String descricao,
        BigDecimal precoFixoKit,
        List<KitItemOutput> itens,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {

    public static KitPromocionalOutput from(KitPromocional kitPromocional, ProdutoService produtoService) {
        Objects.requireNonNull(kitPromocional, "KitPromocional domain object cannot be null.");
        Objects.requireNonNull(produtoService, "ProdutoService cannot be null for KitPromocionalOutput creation.");

        List<KitItemOutput> itensOutput = kitPromocional.getItens().stream()
                .map(itemKit -> {
                    ProdutoOutput produto = produtoService.obterProdutoPorId(itemKit.getProdutoId());
                    return KitItemOutput.from(itemKit, produto);
                })
                .collect(Collectors.toUnmodifiableList());

        return new KitPromocionalOutput(
                kitPromocional.getId(),
                kitPromocional.getNome(),
                kitPromocional.getDescricao(),
                kitPromocional.getPrecoFixoKit(),
                itensOutput,
                kitPromocional.getDataCriacao(),
                kitPromocional.getDataAtualizacao()
        );
    }

    public static KitPromocionalOutput from(KitPromocional kitPromocional) {
        Objects.requireNonNull(kitPromocional, "KitPromocional domain object cannot be null.");

        List<KitItemOutput> itensOutput = kitPromocional.getItens().stream()
                .map(KitItemOutput::from) // Chama o novo from(ItemKit) sem ProdutoOutput
                .collect(Collectors.toUnmodifiableList());

        return new KitPromocionalOutput(
                kitPromocional.getId(),
                kitPromocional.getNome(),
                kitPromocional.getDescricao(),
                kitPromocional.getPrecoFixoKit(),
                itensOutput,
                kitPromocional.getDataCriacao(),
                kitPromocional.getDataAtualizacao()
        );
    }
}