package com.targosystem.varejo.fornecedores.application.usecases;

import com.targosystem.varejo.fornecedores.application.input.CriarFornecedorInput;
import com.targosystem.varejo.fornecedores.application.output.FornecedorOutput;
import com.targosystem.varejo.fornecedores.domain.model.Fornecedor;
import com.targosystem.varejo.fornecedores.domain.model.FornecedorId; // NOVO
import com.targosystem.varejo.fornecedores.domain.model.Contato; // NOVO
import com.targosystem.varejo.fornecedores.domain.model.Endereco;
import com.targosystem.varejo.fornecedores.domain.repository.FornecedorRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CriarFornecedorUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CriarFornecedorUseCase.class);

    private final FornecedorRepository fornecedorRepository;
    private final EventPublisher eventPublisher;

    public CriarFornecedorUseCase(FornecedorRepository fornecedorRepository, EventPublisher eventPublisher) {
        this.fornecedorRepository = Objects.requireNonNull(fornecedorRepository, "FornecedorRepository cannot be null.");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public FornecedorOutput execute(CriarFornecedorInput input) {
        logger.info("Tentando criar novo fornecedor com CNPJ: {}", input.cnpj());

        Optional<Fornecedor> existingFornecedor = fornecedorRepository.findByCnpj(input.cnpj());
        if (existingFornecedor.isPresent()) {
            throw new DomainException("Fornecedor com CNPJ " + input.cnpj() + " já cadastrado.");
        }

        Endereco endereco = new Endereco(
                input.logradouro(),
                input.numero(),
                input.complemento(),
                input.bairro(),
                input.cidade(),
                input.estado(),
                input.cep()
        );
        Contato contato = new Contato(input.emailContato(), input.telefoneContato()); // NOVO

        Fornecedor novoFornecedor = new Fornecedor(
                input.nome(),
                input.cnpj(),
                contato, // NOVO
                endereco
        );

        Fornecedor fornecedorSalvo = fornecedorRepository.save(novoFornecedor);
        logger.info("Fornecedor criado com sucesso. ID: {}", fornecedorSalvo.getId().value()); // Usa .value()

        return FornecedorOutput.fromDomain(fornecedorSalvo);
    }
}