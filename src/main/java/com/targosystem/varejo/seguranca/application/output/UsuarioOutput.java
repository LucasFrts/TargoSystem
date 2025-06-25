package com.targosystem.varejo.seguranca.application.output;

import com.targosystem.varejo.seguranca.domain.model.Papel;
import com.targosystem.varejo.seguranca.domain.model.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record UsuarioOutput(
        String id,
        String username,
        String nomeCompleto,
        String email,
        boolean ativo,
        LocalDateTime dataCadastro,
        LocalDateTime ultimoLogin,
        List<String> papeis
) {
    public static UsuarioOutput from(Usuario usuario) {
        return new UsuarioOutput(
                usuario.getId().getValue(),
                usuario.getUsername(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.isAtivo(),
                usuario.getDataCadastro(),
                usuario.getUltimoLogin(),
                usuario.getPapeis().stream()
                        .map(Papel::getNome)
                        .collect(Collectors.toList())
        );
    }
}