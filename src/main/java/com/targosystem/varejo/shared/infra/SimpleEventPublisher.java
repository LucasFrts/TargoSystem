package com.targosystem.varejo.shared.infra;

import com.targosystem.varejo.shared.domain.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Uma implementação simples em memória de um publicador de eventos de domínio.
 * Suporta o registro de múltiplos listeners para diferentes tipos de eventos.
 */
public class SimpleEventPublisher implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(SimpleEventPublisher.class);

    // Mapeia o tipo de evento (classe) para uma lista de consumidores (listeners)
    private final Map<Class<? extends DomainEvent>, List<Consumer<DomainEvent>>> listeners = new HashMap<>();

    @Override
    public <T extends DomainEvent> void publish(T event) {
        Objects.requireNonNull(event, "Event cannot be null");
        logger.info("Publishing event: {}", event.getClass().getSimpleName());

        // Procura por listeners registrados para o tipo exato do evento
        List<Consumer<DomainEvent>> eventListeners = listeners.get(event.getClass());

        if (eventListeners != null) {
            for (Consumer<DomainEvent> listener : eventListeners) {
                try {
                    // Executa cada listener, passando o evento
                    listener.accept(event);
                    logger.debug("Event {} handled by listener {}.", event.getClass().getSimpleName(), listener.getClass().getSimpleName());
                } catch (Exception e) {
                    logger.error("Error handling event {} by listener {}: {}", event.getClass().getSimpleName(), listener.getClass().getSimpleName(), e.getMessage(), e);
                    // Em um sistema real, você pode ter uma estratégia de re-tentativa ou Dead Letter Queue aqui
                }
            }
        } else {
            logger.warn("No listeners registered for event type: {}", event.getClass().getSimpleName());
        }
    }

    /**
     * Registra um listener (Consumer) para um tipo específico de evento.
     *
     * @param eventType A classe do evento de domínio que o listener deseja receber.
     * @param listener  O Consumer que irá processar o evento.
     * @param <T>       O tipo do evento, que deve estender DomainEvent.
     */
    public <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<T> listener) {
        Objects.requireNonNull(eventType, "Event type cannot be null");
        Objects.requireNonNull(listener, "Listener cannot be null");

        // Adiciona o listener à lista correspondente ao tipo de evento
        // Usamos um cast aqui porque o mapa armazena Consumer<DomainEvent>,
        // mas o método subscribe recebe Consumer<T extends DomainEvent>
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                .add((Consumer<DomainEvent>) listener);
        logger.info("Subscribed listener {} for event type: {}", listener.getClass().getSimpleName(), eventType.getSimpleName());
    }
}