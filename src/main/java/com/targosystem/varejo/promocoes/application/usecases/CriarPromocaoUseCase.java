package com.targosystem.varejo.promocoes.application.usecases;

import com.targosystem.varejo.promocoes.application.input.CriarPromocaoInput;
import com.targosystem.varejo.promocoes.application.output.PromocaoOutput;
import com.targosystem.varejo.promocoes.domain.model.Promocao;
import com.targosystem.varejo.promocoes.domain.repository.PromocaoRepository;
import com.targosystem.varejo.shared.infra.EventPublisher; // Se for usar eventos

import java.math.BigDecimal;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CriarPromocaoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CriarPromocaoUseCase.class);

    private final PromocaoRepository promocaoRepository;
    private final EventPublisher eventPublisher; // Opcional, para publicar eventos

    public CriarPromocaoUseCase(PromocaoRepository promocaoRepository, EventPublisher eventPublisher) {
        this.promocaoRepository = Objects.requireNonNull(promocaoRepository, "PromocaoRepository cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null");
    }

    public PromocaoOutput execute(CriarPromocaoInput input) {
        logger.info("Attempting to create new promotion: {}", input.nome());

        // Validações de input (além das validações de domínio no construtor)
        if (input.nome().isBlank()) {
            throw new IllegalArgumentException("Nome da promoção não pode ser vazio.");
        }
        if (input.valorDesconto().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor de desconto não pode ser negativo.");
        }
        // Mais validações conforme necessário (ex: nome único, sobreposição de datas)

        Promocao novaPromocao = new Promocao(
                input.nome(),
                input.tipoDesconto(),
                input.valorDesconto(),
                input.dataInicio(),
                input.dataFim()
        );

        Promocao promocaoSalva = promocaoRepository.save(novaPromocao);
        logger.info("Promotion '{}' (ID: {}) created successfully.", promocaoSalva.getNome(), promocaoSalva.getId());

        // Opcional: Publicar evento de domínio (ex: PromocaoCriadaEvent)
        // eventPublisher.publish(new PromocaoCriadaEvent(promocaoSalva.getId(), promocaoSalva.getNome()));

        return PromocaoOutput.fromDomain(promocaoSalva);
    }
}