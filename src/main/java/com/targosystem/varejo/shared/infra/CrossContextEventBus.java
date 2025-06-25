package com.targosystem.varejo.shared.infra;

public interface CrossContextEventBus {
    void publish(String topic, Object event);
    void subscribe(String topic, EventListener listener);
}
