package com.targosystem.varejo.shared.infra;

@FunctionalInterface
public interface EventListener {
    void onEvent(Object event);
}
