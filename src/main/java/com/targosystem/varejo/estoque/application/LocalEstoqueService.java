package com.targosystem.varejo.estoque.application;

import com.targosystem.varejo.estoque.application.output.LocalEstoqueOutput;
import com.targosystem.varejo.estoque.domain.model.TipoLocal;
import com.targosystem.varejo.estoque.domain.repository.LocalEstoqueRepository;
import com.targosystem.varejo.estoque.domain.model.LocalEstoque;
import com.targosystem.varejo.fornecedores.domain.repository.FornecedorRepository;
import com.targosystem.varejo.fornecedores.domain.model.Fornecedor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalEstoqueService {

    private final LocalEstoqueRepository localEstoqueRepository;
    private final FornecedorRepository fornecedorRepository;

    public LocalEstoqueService(LocalEstoqueRepository localEstoqueRepository, FornecedorRepository fornecedorRepository) {
        this.localEstoqueRepository = Objects.requireNonNull(localEstoqueRepository, "LocalEstoqueRepository cannot be null.");
        this.fornecedorRepository = Objects.requireNonNull(fornecedorRepository, "FornecedorRepository cannot be null.");
    }

    public LocalEstoqueOutput criarLocalInterno(String nome) {
        LocalEstoque novoLocal = new LocalEstoque(nome, TipoLocal.INTERNO);
        return LocalEstoqueOutput.fromDomain(localEstoqueRepository.save(novoLocal));
    }

    public List<LocalEstoqueOutput> listarTodosLocais() {
        List<LocalEstoque> locais = localEstoqueRepository.findAll();
        return locais.stream()
                .map(LocalEstoqueOutput::fromDomain)
                .collect(Collectors.toList());
    }

    /**
     * Lista locais de estoque por tipo, incluindo fornecedores se o tipo for FORNECEDOR.
     */
    public List<LocalEstoqueOutput> listarLocaisPorTipo(TipoLocal tipo) {
        List<LocalEstoque> resultLocais = new ArrayList<>();

        resultLocais.addAll(localEstoqueRepository.findByTipo(tipo));

        return resultLocais.stream()
                .map(LocalEstoqueOutput::fromDomain)
                .collect(Collectors.toList());
    }

    // Removido o método 'listarLocaisDeOrigemOuDestino' pois 'listarLocaisPorTipo' agora o substitui com a lógica de fornecedores.
    // O EstoqueService chamará 'listarLocaisPorTipo(TipoLocal.FORNECEDOR)', 'listarLocaisPorTipo(TipoLocal.INTERNO)' ou 'listarLocaisPorTipo(TipoLocal.CLIENTE)'.

    public LocalEstoqueOutput buscarLocalPorId(String id) {
        return localEstoqueRepository.findById(id)
                .map(LocalEstoqueOutput::fromDomain)
                .orElseThrow(() -> new IllegalArgumentException("Local de estoque não encontrado com ID: " + id));
    }

    public void deletarLocal(String id) {
        localEstoqueRepository.delete(id);
    }

    public void sincronizarFornecedoresComoLocais() {
        List<Fornecedor> fornecedores = fornecedorRepository.findAll();
        for (Fornecedor fornecedor : fornecedores) {
            Optional<LocalEstoque> existingLocal = localEstoqueRepository.findById(fornecedor.getId().value());
            if (existingLocal.isEmpty()) {
                LocalEstoque localFornecedor = new LocalEstoque(fornecedor.getId().value(), fornecedor.getNome(), TipoLocal.FORNECEDOR);
                localEstoqueRepository.save(localFornecedor);
            } else {
                LocalEstoque local = existingLocal.get();
                local.atualizarInformacoes(fornecedor.getNome(), fornecedor.isAtivo());
                localEstoqueRepository.save(local);
            }
        }
    }
}