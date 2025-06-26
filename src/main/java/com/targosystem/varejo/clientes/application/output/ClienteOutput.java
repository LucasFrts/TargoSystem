package com.targosystem.varejo.clientes.application.output;

import com.targosystem.varejo.clientes.domain.model.Cliente;
import java.time.LocalDateTime;

public record ClienteOutput(
        String id,
        String nome,
        String cpf,
        String email,
        String telefone,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
    public static ClienteOutput from(Cliente cliente) {
        return new ClienteOutput(
                cliente.getId().value(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getDataCriacao(),
                cliente.getDataAtualizacao()
        );
    }
}