package com.targosystem.varejo.seguranca.domain.repository;

import com.targosystem.varejo.seguranca.domain.model.Usuario;
import com.targosystem.varejo.seguranca.domain.model.UsuarioId;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> findById(UsuarioId id);
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findAll();
    Usuario save(Usuario usuario); // Persiste ou atualiza
    void delete(UsuarioId id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}