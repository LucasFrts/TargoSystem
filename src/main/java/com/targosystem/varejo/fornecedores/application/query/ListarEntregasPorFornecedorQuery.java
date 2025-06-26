package com.targosystem.varejo.fornecedores.application.query;

import com.targosystem.varejo.fornecedores.application.output.EntregaFornecedorOutput;
import com.targosystem.varejo.fornecedores.domain.model.FornecedorId;
import com.targosystem.varejo.fornecedores.domain.repository.EntregaFornecedorRepository;
import com.targosystem.varejo.shared.domain.DomainException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListarEntregasPorFornecedorQuery {

    private static final Logger logger = LoggerFactory.getLogger(ListarEntregasPorFornecedorQuery.class);

    private final EntregaFornecedorRepository entregaRepository;

    public ListarEntregasPorFornecedorQuery(EntregaFornecedorRepository entregaRepository) {
        this.entregaRepository = Objects.requireNonNull(entregaRepository, "EntregaFornecedorRepository cannot be null.");
    }

    public List<EntregaFornecedorOutput> execute(String fornecedorIdString) {
        logger.info("Listando entregas para fornecedor ID: {}", fornecedorIdString);
        FornecedorId fornecedorId = new FornecedorId(fornecedorIdString); // Converte para FornecedorId
        List<EntregaFornecedorOutput> entregas = entregaRepository.findByFornecedorId(fornecedorId).stream()
                .map(EntregaFornecedorOutput::fromDomain)
                .collect(Collectors.toList());

        if (entregas.isEmpty()) {
            logger.warn("Nenhuma entrega encontrada para o fornecedor ID: {}", fornecedorIdString);
            // Opcional: jogar exceção se quiser que seja um erro não encontrar entregas
            // throw new DomainException("Nenhuma entrega encontrada para o fornecedor com ID: " + fornecedorIdString);
        }
        return entregas;
    }
}