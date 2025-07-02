package com.targosystem.varejo.seguranca.application.usecases;

import com.targosystem.varejo.seguranca.application.input.AtualizarUsuarioInput;
import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;
import com.targosystem.varejo.seguranca.domain.model.Papel;
import com.targosystem.varejo.seguranca.domain.model.Usuario;
import com.targosystem.varejo.seguranca.domain.model.UsuarioId;
import com.targosystem.varejo.seguranca.domain.repository.PapelRepository;
import com.targosystem.varejo.seguranca.domain.repository.UsuarioRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AtualizarUsuarioUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AtualizarUsuarioUseCase.class);

    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;

    public AtualizarUsuarioUseCase(UsuarioRepository usuarioRepository, PapelRepository papelRepository) {
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository, "UsuarioRepository cannot be null");
        this.papelRepository = Objects.requireNonNull(papelRepository, "PapelRepository cannot be null");
    }

    public UsuarioOutput execute(AtualizarUsuarioInput input) {
        logger.info("Attempting to update user with ID: {}", input.id());

        Objects.requireNonNull(input.id(), "User ID for update cannot be null");

        Usuario usuarioExistente = usuarioRepository.findById(UsuarioId.from(input.id()))
                .orElseThrow(() -> new DomainException("User with ID " + input.id() + " not found."));

        usuarioExistente.atualizarInformacoes(input.nomeCompleto(), input.email());

        if (input.ativo() != null) {
            if (input.ativo()) {
                usuarioExistente.ativar();
            } else {
                usuarioExistente.inativar();
            }
        }

        if (input.papeisNomes() != null) {
            Set<Papel> novosPapeis = new HashSet<>();
            for (String papelNome : input.papeisNomes()) {
                Papel papel = papelRepository.findByNome(papelNome)
                        .orElseThrow(() -> new DomainException("Role not found: " + papelNome));
                novosPapeis.add(papel);
            }

            Set<Papel> papeisParaRemover = usuarioExistente.getPapeis().stream()
                    .filter(p -> !novosPapeis.contains(p))
                    .collect(Collectors.toSet());
            papeisParaRemover.forEach(usuarioExistente::removerPapel);

            novosPapeis.stream()
                    .filter(p -> !usuarioExistente.getPapeis().contains(p))
                    .forEach(usuarioExistente::adicionarPapel);
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
        logger.info("User {} (ID: {}) updated successfully.", usuarioAtualizado.getUsername(), usuarioAtualizado.getId().getValue());

        return UsuarioOutput.from(usuarioAtualizado);
    }
}