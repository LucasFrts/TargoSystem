package com.targosystem.varejo.fornecedores.application.output;

import com.targosystem.varejo.fornecedores.domain.model.Fornecedor;
import com.targosystem.varejo.fornecedores.domain.model.Endereco;
import com.targosystem.varejo.fornecedores.domain.model.Contato;

import java.time.LocalDateTime;

public record FornecedorOutput(
        String id,
        String nome,
        String cnpj,
        String emailContato,
        String telefoneContato,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
    public static FornecedorOutput fromDomain(Fornecedor fornecedor) {
        Endereco endereco = fornecedor.getEndereco();
        Contato contato = fornecedor.getContato();
        return new FornecedorOutput(
                fornecedor.getId().value(), // Acessa o valor do FornecedorId
                fornecedor.getNome(),
                fornecedor.getCnpj(),
                contato.email(),
                contato.telefone(),
                endereco.logradouro(),
                endereco.numero(),
                endereco.complemento(),
                endereco.bairro(),
                endereco.cidade(),
                endereco.estado(),
                endereco.cep(),
                fornecedor.isAtivo(),
                fornecedor.getDataCriacao(),
                fornecedor.getDataAtualizacao()
        );
    }
}