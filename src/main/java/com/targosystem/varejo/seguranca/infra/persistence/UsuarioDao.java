package com.targosystem.varejo.seguranca.infra.persistence;

import com.targosystem.varejo.seguranca.domain.model.Papel;
import com.targosystem.varejo.seguranca.domain.model.Usuario;
import com.targosystem.varejo.seguranca.domain.model.UsuarioId;
import com.targosystem.varejo.seguranca.domain.repository.UsuarioRepository;
import com.targosystem.varejo.seguranca.infra.persistence.entity.PapelJpaEntity;
import com.targosystem.varejo.seguranca.infra.persistence.entity.UsuarioJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

public class UsuarioDao implements UsuarioRepository {

    private final EntityManager entityManager;

    public UsuarioDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Usuario> findById(UsuarioId id) {
        UsuarioJpaEntity entity = entityManager.find(UsuarioJpaEntity.class, id.getValue());
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        try {
            TypedQuery<UsuarioJpaEntity> query = entityManager.createQuery(
                    "SELECT u FROM UsuarioJpaEntity u WHERE u.username = :username", UsuarioJpaEntity.class);
            query.setParameter("username", username);
            return Optional.of(query.getSingleResult()).map(this::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Usuario> findAll() {
        TypedQuery<UsuarioJpaEntity> query = entityManager.createQuery(
                "SELECT u FROM UsuarioJpaEntity u", UsuarioJpaEntity.class);
        return query.getResultList().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioJpaEntity entity = toJpaEntity(usuario);
        if (entityManager.find(UsuarioJpaEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        return toDomain(entity);
    }

    @Override
    public void delete(UsuarioId id) {
        UsuarioJpaEntity entity = entityManager.find(UsuarioJpaEntity.class, id.getValue());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM UsuarioJpaEntity u WHERE u.username = :username", Long.class);
        query.setParameter("username", username);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) return false;
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM UsuarioJpaEntity u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }
    
    private Usuario toDomain(UsuarioJpaEntity entity) {
        if (entity == null) return null;
        Set<Papel> papeis = entity.getPapeis().stream()
                .map(p -> new Papel(p.getId(), p.getNome(), p.getDescricao()))
                .collect(Collectors.toSet());

        return new Usuario(
                UsuarioId.from(entity.getId()),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getNomeCompleto(),
                entity.getEmail(),
                entity.isAtivo(),
                entity.getDataCadastro(),
                entity.getUltimoLogin(),
                papeis
        );
    }

    private UsuarioJpaEntity toJpaEntity(Usuario domain) {
        if (domain == null) return null;

        Set<PapelJpaEntity> jpaPapeis = domain.getPapeis().stream()
                .map(p -> entityManager.find(PapelJpaEntity.class, p.getId())) // Busca o PapelJpaEntity pelo ID
                .filter(Objects::nonNull) // Garante que o papel existe no DB
                .collect(Collectors.toSet());

        UsuarioJpaEntity entity = new UsuarioJpaEntity();
        entity.setId(domain.getId().getValue());
        entity.setUsername(domain.getUsername());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setNomeCompleto(domain.getNomeCompleto());
        entity.setEmail(domain.getEmail());
        entity.setAtivo(domain.isAtivo());
        entity.setDataCadastro(domain.getDataCadastro());
        entity.setUltimoLogin(domain.getUltimoLogin());
        entity.setPapeis(jpaPapeis);
        return entity;
    }
}