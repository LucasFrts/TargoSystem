package com.targosystem.varejo.seguranca.domain.events;

import com.targosystem.varejo.shared.domain.DomainEvent;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Evento de domínio que sinaliza que um novo usuário foi criado.
 */
public class UsuarioCriadoEvent implements DomainEvent {

    private final String usuarioId;
    private final String username;
    private final String nomeCompleto;
    private final LocalDateTime ocorreuEm;

    public UsuarioCriadoEvent(String usuarioId, String username, String nomeCompleto) {
        this.usuarioId = Objects.requireNonNull(usuarioId, "User ID cannot be null");
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.nomeCompleto = Objects.requireNonNull(nomeCompleto, "Full name cannot be null");
        this.ocorreuEm = LocalDateTime.now();
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    @Override
    public LocalDateTime getOcorreuEm() {
        return ocorreuEm;
    }

    @Override
    public String toString() {
        return "UsuarioCriadoEvent{" +
                "usuarioId='" + usuarioId + '\'' +
                ", username='" + username + '\'' +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", ocorreuEm=" + ocorreuEm +
                '}';
    }
}