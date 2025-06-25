package com.targosystem.varejo.seguranca.application.query;

import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;
import com.targosystem.varejo.seguranca.domain.repository.UsuarioRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListarUsuariosQuery {

    private final UsuarioRepository usuarioRepository;

    public ListarUsuariosQuery(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository, "UsuarioRepository cannot be null");
    }

    public List<UsuarioOutput> execute() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioOutput::from)
                .collect(Collectors.toList());
    }
}