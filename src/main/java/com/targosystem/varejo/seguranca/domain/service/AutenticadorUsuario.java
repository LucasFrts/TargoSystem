package com.targosystem.varejo.seguranca.domain.service;

import com.targosystem.varejo.seguranca.domain.model.Usuario;
import com.targosystem.varejo.seguranca.domain.repository.UsuarioRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import java.util.Objects;

/**
 * Serviço de domínio responsável por autenticar usuários.
 */
public class AutenticadorUsuario {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncryptor passwordEncryptor;

    public AutenticadorUsuario(UsuarioRepository usuarioRepository, PasswordEncryptor passwordEncryptor) {
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository, "UsuarioRepository cannot be null");
        this.passwordEncryptor = Objects.requireNonNull(passwordEncryptor, "PasswordEncryptor cannot be null");
    }

    /**
     * Autentica um usuário.
     * @param username O nome de usuário.
     * @param rawPassword A senha em texto plano fornecida pelo usuário.
     * @return O objeto Usuario autenticado.
     * @throws DomainException Se o usuário não for encontrado ou a senha for inválida.
     */
    public Usuario autenticar(String username, String rawPassword) {
        Objects.requireNonNull(username, "Username cannot be null for authentication");
        Objects.requireNonNull(rawPassword, "Password cannot be null for authentication");

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new DomainException("Invalid credentials."));

        if (!usuario.isAtivo()) {
            throw new DomainException("User account is inactive.");
        }

        if (!passwordEncryptor.matches(rawPassword, usuario.getPasswordHash())) {
            throw new DomainException("Invalid credentials.");
        }

        usuario.registrarUltimoLogin();
        usuarioRepository.save(usuario);

        return usuario;
    }
}