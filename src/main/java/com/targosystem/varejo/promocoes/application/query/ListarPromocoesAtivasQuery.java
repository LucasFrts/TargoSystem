package com.targosystem.varejo.promocoes.application.query;

import com.targosystem.varejo.promocoes.application.output.PromocaoOutput;
import com.targosystem.varejo.promocoes.domain.repository.PromocaoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListarPromocoesAtivasQuery {

    private final PromocaoRepository promocaoRepository;

    public ListarPromocoesAtivasQuery(PromocaoRepository promocaoRepository) {
        this.promocaoRepository = Objects.requireNonNull(promocaoRepository, "PromocaoRepository cannot be null");
    }

    public List<PromocaoOutput> execute() {
        // Busca promoções ativas no momento atual
        return promocaoRepository.findActivePromotions(LocalDateTime.now()).stream()
                .map(PromocaoOutput::fromDomain)
                .collect(Collectors.toList());
    }
}