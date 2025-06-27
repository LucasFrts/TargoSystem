package com.targosystem.varejo.promocoes.application.query;

import com.targosystem.varejo.promocoes.application.output.PromocaoOutput;
import com.targosystem.varejo.promocoes.domain.repository.PromocaoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListarPromocoesAtivasQuery {

    private static final Logger logger = LoggerFactory.getLogger(ListarPromocoesAtivasQuery.class);

    private final PromocaoRepository promocaoRepository;

    public ListarPromocoesAtivasQuery(PromocaoRepository promocaoRepository) {
        this.promocaoRepository = Objects.requireNonNull(promocaoRepository, "PromocaoRepository cannot be null");
    }

    public List<PromocaoOutput> execute() {
        LocalDateTime now = LocalDateTime.now();
        logger.debug("Executing ListarPromocoesAtivasQuery to fetch active promotions as of: {}", now);
        try {
            // Busca promoções ativas no momento atual
            return promocaoRepository.findActivePromotions(now).stream()
                    .map(PromocaoOutput::from) // Assumindo que PromocaoOutput tem um método from(Promocao)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving active promotions: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao listar promoções ativas.", e);
        }
    }
}