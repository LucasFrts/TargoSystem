package com.targosystem.varejo.seguranca.application.query;

import com.targosystem.varejo.seguranca.application.output.PapelOutput;
import com.targosystem.varejo.seguranca.domain.repository.PapelRepository;
import com.targosystem.varejo.seguranca.domain.repository.UsuarioRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListarPapeisQuery {

    private final PapelRepository papelRepository;

    public ListarPapeisQuery(PapelRepository papelRepository) {
        this.papelRepository = Objects.requireNonNull(papelRepository, "PapelRepository cannot be null");
    }

    public List<PapelOutput> execute() {
        return papelRepository.findAll().stream()
                .map(papel -> new PapelOutput(papel.getId(), papel.getNome(), papel.getDescricao()))
                .collect(Collectors.toList());
    }
}
