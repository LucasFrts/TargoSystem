package com.targosystem.varejo.seguranca.application.query;

import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;
import com.targosystem.varejo.seguranca.domain.model.UsuarioId;
import com.targosystem.varejo.seguranca.domain.repository.UsuarioRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;

public class ObterUsuarioPorIdQuery {

    private final UsuarioRepository usuarioRepository;

    public ObterUsuarioPorIdQuery(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository, "UsuarioRepository cannot be null");
    }

    public UsuarioOutput execute(String id) {
        Objects.requireNonNull(id, "User ID cannot be null");
        return usuarioRepository.findById(UsuarioId.from(id))
                .map(UsuarioOutput::from)
                .orElseThrow(() -> new DomainException("User with ID " + id + " not found."));
    }
}