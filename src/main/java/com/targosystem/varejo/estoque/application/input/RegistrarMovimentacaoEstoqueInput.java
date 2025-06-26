// src/main/java/com/targosystem/varejo/estoque/application/input/RegistrarMovimentacaoEstoqueInput.java
package com.targosystem.varejo.estoque.application.input;

import com.targosystem.varejo.estoque.domain.model.TipoMovimentacao;

public record RegistrarMovimentacaoEstoqueInput(
        String produtoId,
        int quantidade,
        TipoMovimentacao tipoMovimentacao,
        String motivo,
        LoteInput lote,
        LocalizacaoArmazenamentoInput localizacao
) {}