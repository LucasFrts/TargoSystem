package com.targosystem.varejo.seguranca.application.usecases;

import com.targosystem.varejo.seguranca.application.input.CriarUsuarioInput;
import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;
import com.targosystem.varejo.seguranca.domain.events.UsuarioCriadoEvent;
import com.targosystem.varejo.seguranca.domain.model.Papel;
import com.targosystem.varejo.seguranca.domain.model.Usuario;
import com.targosystem.varejo.seguranca.domain.repository.PapelRepository;
import com.targosystem.varejo.seguranca.domain.repository.UsuarioRepository;
import com.targosystem.varejo.seguranca.domain.service.PasswordEncryptor;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CriarUsuarioUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CriarUsuarioUseCase.class);

    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;
    private final PasswordEncryptor passwordEncryptor;
    private final EventPublisher eventPublisher;

    // Remover EntityManagerFactory do construtor
    public CriarUsuarioUseCase(UsuarioRepository usuarioRepository, PapelRepository papelRepository, PasswordEncryptor passwordEncryptor, EventPublisher eventPublisher) {
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository, "UsuarioRepository cannot be null");
        this.papelRepository = Objects.requireNonNull(papelRepository, "PapelRepository cannot be null");
        this.passwordEncryptor = Objects.requireNonNull(passwordEncryptor, "PasswordEncryptor cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null");
    }

    public UsuarioOutput execute(CriarUsuarioInput input) {
        logger.info("Attempting to create new user with username: {}", input.username());

        Objects.requireNonNull(input.username(), "Username cannot be null");
        Objects.requireNonNull(input.password(), "Password cannot be null");

        if (input.username().isBlank()) {
            throw new DomainException("Username cannot be empty");
        }
        if (input.password().isBlank()) {
            throw new DomainException("Password cannot be empty");
        }

        // Sem blocos try-catch e transação aqui
        // Usar os repositórios injetados diretamente
        if (usuarioRepository.existsByUsername(input.username())) {
            logger.warn("User with username {} already exists. Aborting creation.", input.username());
            throw new DomainException("User with this username already exists.");
        }
        if (input.email() != null && !input.email().isBlank() && usuarioRepository.existsByEmail(input.email())) {
            logger.warn("User with email {} already exists. Aborting creation.", input.email());
            throw new DomainException("User with this email already exists.");
        }

        String hashedPassword = passwordEncryptor.encrypt(input.password());

        Usuario novoUsuario = new Usuario(
                input.username(),
                hashedPassword,
                input.nomeCompleto(),
                input.email()
        );

        Set<Papel> papeisDoUsuario = new HashSet<>();
        if (input.papeisNomes() != null) {
            for (String papelNome : input.papeisNomes()) {
                Papel papel = papelRepository.findByNome(papelNome)
                        .orElseThrow(() -> new DomainException("Role not found: " + papelNome));
                papeisDoUsuario.add(papel);
            }
        }
        papeisDoUsuario.forEach(novoUsuario::adicionarPapel);

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario); // Continua chamando o save do repo
        logger.info("User {} (ID: {}) created successfully.", usuarioSalvo.getUsername(), usuarioSalvo.getId().getValue());

        eventPublisher.publish(new UsuarioCriadoEvent(usuarioSalvo.getId().getValue(), usuarioSalvo.getUsername(), usuarioSalvo.getNomeCompleto()));

        return UsuarioOutput.from(usuarioSalvo);
    }
}