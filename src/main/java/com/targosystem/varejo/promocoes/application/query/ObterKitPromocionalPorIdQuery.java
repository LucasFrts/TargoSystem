package com.targosystem.varejo.promocoes.application.query;

import com.targosystem.varejo.promocoes.application.output.KitPromocionalOutput;
import com.targosystem.varejo.promocoes.domain.repository.KitPromocionalRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;

public class ObterKitPromocionalPorIdQuery {

    private final KitPromocionalRepository kitPromocionalRepository;

    public ObterKitPromocionalPorIdQuery(KitPromocionalRepository kitPromocionalRepository) {
        this.kitPromocionalRepository = Objects.requireNonNull(kitPromocionalRepository, "KitPromocionalRepository cannot be null");
    }

    public KitPromocionalOutput execute(String id) {
        return kitPromocionalRepository.findById(id)
                .map(KitPromocionalOutput::fromDomain)
                .orElseThrow(() -> new DomainException("Kit Promocional não encontrado com ID: " + id));
    }
}