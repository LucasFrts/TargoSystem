package com.targosystem.varejo.seguranca.application.usecases;

import com.targosystem.varejo.seguranca.application.input.LoginInput;
import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;
import com.targosystem.varejo.seguranca.domain.model.Usuario;
import com.targosystem.varejo.seguranca.domain.service.AutenticadorUsuario;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class LoginUsuarioUseCase {

    private static final Logger logger = LoggerFactory.getLogger(LoginUsuarioUseCase.class);

    private final AutenticadorUsuario autenticadorUsuario;
    private final EventPublisher eventPublisher;

    public LoginUsuarioUseCase(AutenticadorUsuario autenticadorUsuario, EventPublisher eventPublisher) {
        this.autenticadorUsuario = Objects.requireNonNull(autenticadorUsuario, "AutenticadorUsuario cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null");
    }

    public UsuarioOutput execute(LoginInput input) {
        logger.info("Attempting login for username: {}", input.username());

        Objects.requireNonNull(input.username(), "Username cannot be null for login");
        Objects.requireNonNull(input.password(), "Password cannot be null for login");

        try {
            Usuario usuarioAutenticado = autenticadorUsuario.autenticar(input.username(), input.password());
            logger.info("User {} logged in successfully.", usuarioAutenticado.getUsername());

            return UsuarioOutput.from(usuarioAutenticado);
        } catch (DomainException e) {
            logger.warn("Login failed for username {}: {}", input.username(), e.getMessage());
            throw e;
        }
    }
}