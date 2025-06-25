package com.targosystem.varejo.seguranca.domain.listeners;

import com.targosystem.varejo.seguranca.domain.events.UsuarioCriadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Consumer;

/**
 * Listener para logar a criação de um novo usuário.
 */
public class UsuarioCriadoLoggerListener implements Consumer<UsuarioCriadoEvent> {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioCriadoLoggerListener.class);

    @Override
    public void accept(UsuarioCriadoEvent event) {
        logger.info("EVENTO [UsuarioCriado]: Usuário '{}' (ID: {}) criado com sucesso em {}.",
                event.getUsername(), event.getUsuarioId(), event.getOcorreuEm());
    }
}