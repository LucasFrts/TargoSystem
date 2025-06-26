package com.targosystem.varejo.fornecedores.application.usecases;

import com.targosystem.varejo.fornecedores.application.input.AtualizarFornecedorInput;
import com.targosystem.varejo.fornecedores.application.output.FornecedorOutput;
import com.targosystem.varejo.fornecedores.domain.model.Fornecedor;
import com.targosystem.varejo.fornecedores.domain.model.FornecedorId; // NOVO
import com.targosystem.varejo.fornecedores.domain.model.Contato; // NOVO
import com.targosystem.varejo.fornecedores.domain.model.Endereco;
import com.targosystem.varejo.fornecedores.domain.repository.FornecedorRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import com.targosystem.varejo.shared.infra.EventPublisher;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtualizarFornecedorUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AtualizarFornecedorUseCase.class);

    private final FornecedorRepository fornecedorRepository;
    private final EventPublisher eventPublisher;

    public AtualizarFornecedorUseCase(FornecedorRepository fornecedorRepository, EventPublisher eventPublisher) {
        this.fornecedorRepository = Objects.requireNonNull(fornecedorRepository, "FornecedorRepository cannot be null.");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null.");
    }

    public FornecedorOutput execute(AtualizarFornecedorInput input) {
        logger.info("Tentando atualizar fornecedor com ID: {}", input.id());

        FornecedorId fornecedorId = new FornecedorId(input.id());
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new DomainException("Fornecedor não encontrado com ID: " + input.id()));

        Endereco novoEndereco = new Endereco(
                input.logradouro(),
                input.numero(),
                input.complemento(),
                input.bairro(),
                input.cidade(),
                input.estado(),
                input.cep()
        );
        Contato novoContato = new Contato(input.emailContato(), input.telefoneContato());

        fornecedor.atualizarInformacoes(
                input.nome(),
                novoContato,
                novoEndereco
        );

        Fornecedor fornecedorSalvo = fornecedorRepository.save(fornecedor);
        logger.info("Fornecedor atualizado com sucesso. ID: {}", fornecedorSalvo.getId().value());

        return FornecedorOutput.fromDomain(fornecedorSalvo);
    }
}