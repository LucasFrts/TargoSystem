package com.targosystem.varejo.promocoes.application.query;

import com.targosystem.varejo.promocoes.application.output.PromocaoOutput;
import com.targosystem.varejo.promocoes.domain.repository.PromocaoRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;
import java.util.Optional;

public class ObterPromocaoPorIdQuery {

    private final PromocaoRepository promocaoRepository;

    public ObterPromocaoPorIdQuery(PromocaoRepository promocaoRepository) {
        this.promocaoRepository = Objects.requireNonNull(promocaoRepository, "PromocaoRepository cannot be null");
    }

    public PromocaoOutput execute(String id) {
        return promocaoRepository.findById(id)
                .map(PromocaoOutput::fromDomain)
                .orElseThrow(() -> new DomainException("Promoção não encontrada com ID: " + id));
    }
}