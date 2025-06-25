package com.targosystem.varejo.promocoes.application.usecases;

import com.targosystem.varejo.promocoes.application.input.AtualizarPromocaoInput;
import com.targosystem.varejo.promocoes.application.output.PromocaoOutput;
import com.targosystem.varejo.promocoes.domain.model.Promocao;
import com.targosystem.varejo.promocoes.domain.repository.PromocaoRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher; // Se for usar eventos

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtualizarPromocaoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AtualizarPromocaoUseCase.class);

    private final PromocaoRepository promocaoRepository;
    private final EventPublisher eventPublisher; // Opcional

    public AtualizarPromocaoUseCase(PromocaoRepository promocaoRepository, EventPublisher eventPublisher) {
        this.promocaoRepository = Objects.requireNonNull(promocaoRepository, "PromocaoRepository cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null");
    }

    public PromocaoOutput execute(AtualizarPromocaoInput input) {
        logger.info("Attempting to update promotion with ID: {}", input.promocaoId());

        Promocao promocao = promocaoRepository.findById(input.promocaoId())
                .orElseThrow(() -> new DomainException("Promoção não encontrada com ID: " + input.promocaoId()));

        input.nome().ifPresent(promocao::setNome);
        input.tipoDesconto().ifPresent(promocao::setTipoDesconto);
        input.valorDesconto().ifPresent(promocao::setValorDesconto);
        input.dataInicio().ifPresent(promocao::setDataInicio);
        input.dataFim().ifPresent(promocao::setDataFim);
        input.ativa().ifPresent(ativa -> {
            if (ativa) {
                promocao.ativar();
            } else {
                promocao.inativar();
            }
        });

        Promocao promocaoAtualizada = promocaoRepository.save(promocao);
        logger.info("Promotion '{}' (ID: {}) updated successfully.", promocaoAtualizada.getNome(), promocaoAtualizada.getId());

        // Opcional: Publicar evento PromocaoAtualizadaEvent
        // eventPublisher.publish(new PromocaoAtualizadaEvent(promocaoAtualizada.getId(), promocaoAtualizada.getNome()));

        return PromocaoOutput.fromDomain(promocaoAtualizada);
    }
}