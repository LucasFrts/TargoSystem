package com.targosystem.varejo.shared.infra;

public interface EventPublisher {
    void publish(Object event);
}
