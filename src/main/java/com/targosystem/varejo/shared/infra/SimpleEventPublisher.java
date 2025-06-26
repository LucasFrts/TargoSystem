// src/main/java/com/targosystem/varejo/shared/infra/SimpleEventPublisher.java
package com.targosystem.varejo.shared.infra;

import com.targosystem.varejo.shared.domain.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Uma implementação simples em memória de um publicador de eventos de domínio.
 * Suporta o registro de múltiplos listeners para diferentes tipos de eventos.
 */
public class SimpleEventPublisher implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(SimpleEventPublisher.class);
    
    private final Map<Class<? extends DomainEvent>, List<Consumer<DomainEvent>>> listeners = new HashMap<>();

    @Override
    public <T extends DomainEvent> void publish(T event) {
        Objects.requireNonNull(event, "Event cannot be null");
        logger.info("Publishing event: {}", event.getClass().getSimpleName());

        // Obtém a lista de listeners para o tipo de evento específico.
        // O cast é seguro porque a chave do mapa é Class<? extends DomainEvent>.
        List<Consumer<DomainEvent>> eventListeners = listeners.get(event.getClass());

        if (eventListeners != null) {
            for (Consumer<DomainEvent> listener : eventListeners) {
                try {
                    listener.accept(event);
                    logger.debug("Event {} handled by listener {}.", event.getClass().getSimpleName(), listener.getClass().getSimpleName());
                } catch (Exception e) {
                    logger.error("Error handling event {} by listener {}: {}", event.getClass().getSimpleName(), listener.getClass().getSimpleName(), e.getMessage(), e);
                }
            }
        } else {
            logger.warn("No listeners registered for event type: {}", event.getClass().getSimpleName());
        }
    }

    @Override
    public <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<? super T> listener) {
        Objects.requireNonNull(eventType, "Event type cannot be null");
        Objects.requireNonNull(listener, "Listener cannot be null");

        // Get or create the list of consumers for this event type.
        List<Consumer<DomainEvent>> eventConsumers = listeners.computeIfAbsent(eventType, k -> new ArrayList<>());

        // This is the critical line. We perform an explicit unchecked cast.
        // This tells the compiler that we are confident this Consumer<? super T>
        // is compatible with Consumer<DomainEvent> for the purpose of adding it
        // to our list.
        @SuppressWarnings("unchecked") // Suppress the warning because we know it's safe
        Consumer<DomainEvent> castedListener = (Consumer<DomainEvent>) listener;
        eventConsumers.add(castedListener);

        logger.info("Subscribed listener {} for event type: {}", listener.getClass().getSimpleName(), eventType.getSimpleName());
    }
}