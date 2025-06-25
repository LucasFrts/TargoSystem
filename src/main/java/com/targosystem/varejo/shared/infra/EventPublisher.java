package com.targosystem.varejo.shared.infra;

import com.targosystem.varejo.shared.domain.DomainEvent;
import java.util.function.Consumer;

/**
 * Interface para publicação de eventos de domínio.
 * Define o contrato para quem deseja publicar eventos e para quem deseja se inscrever.
 */
public interface EventPublisher {
    <T extends DomainEvent> void publish(T event);

    <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<? super T> listener);
}