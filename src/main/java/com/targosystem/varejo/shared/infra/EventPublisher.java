// src/main/java/com/targosystem/varejo/shared/infra/EventPublisher.java
package com.targosystem.varejo.shared.infra;

import com.targosystem.varejo.shared.domain.DomainEvent;

/**
 * Interface para publicação de eventos de domínio.
 * Define o contrato para quem deseja publicar eventos.
 */
public interface EventPublisher {
    <T extends DomainEvent> void publish(T event);
}