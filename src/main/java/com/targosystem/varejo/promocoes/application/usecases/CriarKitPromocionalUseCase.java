package com.targosystem.varejo.promocoes.application.usecases;

import com.targosystem.varejo.promocoes.application.input.CriarKitPromocionalInput;
import com.targosystem.varejo.promocoes.application.output.KitPromocionalOutput;
import com.targosystem.varejo.promocoes.domain.model.KitPromocional;
import com.targosystem.varejo.promocoes.domain.model.ItemKit;
import com.targosystem.varejo.promocoes.domain.repository.KitPromocionalRepository;
import com.targosystem.varejo.shared.infra.EventPublisher; // Opcional, para eventos

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CriarKitPromocionalUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CriarKitPromocionalUseCase.class);

    private final KitPromocionalRepository kitPromocionalRepository;
    private final EventPublisher eventPublisher; // Opcional

    public CriarKitPromocionalUseCase(KitPromocionalRepository kitPromocionalRepository, EventPublisher eventPublisher) {
        this.kitPromocionalRepository = Objects.requireNonNull(kitPromocionalRepository, "KitPromocionalRepository cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null");
    }

    public KitPromocionalOutput execute(CriarKitPromocionalInput input) {
        logger.info("Attempting to create new promotional kit: {}", input.nome());

        // Mapear ItemKitInput para ItemKit de domínio
        List<ItemKit> itensDominio = input.itens().stream()
                .map(itemInput -> new ItemKit(itemInput.produtoId(), itemInput.quantidade()))
                .collect(Collectors.toList());

        KitPromocional novoKit = new KitPromocional(
                input.nome(),
                input.descricao(),
                input.precoFixoKit(),
                itensDominio
        );

        KitPromocional kitSalvo = kitPromocionalRepository.save(novoKit);
        logger.info("Promotional kit '{}' (ID: {}) created successfully.", kitSalvo.getNome(), kitSalvo.getId());

        // Opcional: Publicar evento KitPromocionalCriadoEvent
        // eventPublisher.publish(new KitPromocionalCriadoEvent(kitSalvo.getId(), kitSalvo.getNome()));

        return KitPromocionalOutput.fromDomain(kitSalvo);
    }
}