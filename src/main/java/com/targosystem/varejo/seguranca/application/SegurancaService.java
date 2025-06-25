// src/main/java/com/targosystem/varejo/seguranca/application/SegurancaService.java
package com.targosystem.varejo.seguranca.application;

import com.targosystem.varejo.seguranca.application.input.AtualizarUsuarioInput;
import com.targosystem.varejo.seguranca.application.input.CriarUsuarioInput;
import com.targosystem.varejo.seguranca.application.input.LoginInput;
import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;
import com.targosystem.varejo.seguranca.application.query.ListarUsuariosQuery;
import com.targosystem.varejo.seguranca.application.query.ObterUsuarioPorIdQuery;
import com.targosystem.varejo.seguranca.application.usecases.AtualizarUsuarioUseCase;
import com.targosystem.varejo.seguranca.application.usecases.CriarUsuarioUseCase;
import com.targosystem.varejo.seguranca.application.usecases.LoginUsuarioUseCase;
import java.util.List;
import java.util.Objects;

public class SegurancaService {

    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    private final LoginUsuarioUseCase loginUsuarioUseCase;
    private final ObterUsuarioPorIdQuery obterUsuarioPorIdQuery;
    private final ListarUsuariosQuery listarUsuariosQuery;

    public SegurancaService(
            CriarUsuarioUseCase criarUsuarioUseCase,
            AtualizarUsuarioUseCase atualizarUsuarioUseCase,
            LoginUsuarioUseCase loginUsuarioUseCase,
            ObterUsuarioPorIdQuery obterUsuarioPorIdQuery,
            ListarUsuariosQuery listarUsuariosQuery) {
        this.criarUsuarioUseCase = Objects.requireNonNull(criarUsuarioUseCase, "CriarUsuarioUseCase cannot be null");
        this.atualizarUsuarioUseCase = Objects.requireNonNull(atualizarUsuarioUseCase, "AtualizarUsuarioUseCase cannot be null");
        this.loginUsuarioUseCase = Objects.requireNonNull(loginUsuarioUseCase, "LoginUsuarioUseCase cannot be null");
        this.obterUsuarioPorIdQuery = Objects.requireNonNull(obterUsuarioPorIdQuery, "ObterUsuarioPorIdQuery cannot be null");
        this.listarUsuariosQuery = Objects.requireNonNull(listarUsuariosQuery, "ListarUsuariosQuery cannot be null");
    }

    public UsuarioOutput criarUsuario(CriarUsuarioInput input) {
        return criarUsuarioUseCase.execute(input);
    }

    public UsuarioOutput atualizarUsuario(AtualizarUsuarioInput input) {
        return atualizarUsuarioUseCase.execute(input);
    }

    public UsuarioOutput login(LoginInput input) {
        return loginUsuarioUseCase.execute(input);
    }

    public UsuarioOutput obterUsuarioPorId(String id) {
        return obterUsuarioPorIdQuery.execute(id);
    }

    public List<UsuarioOutput> listarUsuarios() {
        return listarUsuariosQuery.execute();
    }
}