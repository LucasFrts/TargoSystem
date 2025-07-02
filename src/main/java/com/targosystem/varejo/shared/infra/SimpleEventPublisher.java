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

        List<Consumer<DomainEvent>> eventConsumers = listeners.computeIfAbsent(eventType, k -> new ArrayList<>());

        @SuppressWarnings("unchecked") // Suppress the warning because we know it's safe
        Consumer<DomainEvent> castedListener = (Consumer<DomainEvent>) listener;
        eventConsumers.add(castedListener);

        logger.info("Subscribed listener {} for event type: {}", listener.getClass().getSimpleName(), eventType.getSimpleName());
    }
}