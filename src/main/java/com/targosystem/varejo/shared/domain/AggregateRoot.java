package com.targosystem.varejo.shared.domain;

/**
 * Interface marcadora para Entidades Raiz de Agregado.
 * No Domain-Driven Design, a Aggregate Root garante a consistência
 * de um cluster de objetos de domínio, controlando o acesso a eles.
 * Todas as referências externas ao agregado devem ser feitas apenas
 * para a Aggregate Root.
 */
public interface AggregateRoot {
}