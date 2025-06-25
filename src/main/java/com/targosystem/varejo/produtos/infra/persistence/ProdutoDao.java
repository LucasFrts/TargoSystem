package com.targosystem.varejo.produtos.infra.persistence;

import com.targosystem.varejo.produtos.domain.model.Categoria;
import com.targosystem.varejo.produtos.domain.model.Produto;
import com.targosystem.varejo.produtos.domain.model.ProdutoId;
import com.targosystem.varejo.produtos.domain.repository.ProdutoRepository;
import com.targosystem.varejo.produtos.infra.persistence.entity.CategoriaJpaEntity;
import com.targosystem.varejo.produtos.infra.persistence.entity.ProdutoJpaEntity;
import com.targosystem.varejo.shared.domain.Price;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Exemplo usando JPA, mas pode ser JDBC puro, Jooq, etc.
public class ProdutoDao implements ProdutoRepository {

    private final EntityManager entityManager;

    public ProdutoDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Produto> findById(ProdutoId id) {
        ProdutoJpaEntity entity = entityManager.find(ProdutoJpaEntity.class, id.getValue());
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<Produto> findByCodigoBarras(String codigoBarras) {
        try {
            TypedQuery<ProdutoJpaEntity> query = entityManager.createQuery(
                    "SELECT p FROM ProdutoJpaEntity p WHERE p.codigoBarras = :codigoBarras", ProdutoJpaEntity.class);
            query.setParameter("codigoBarras", codigoBarras);
            return Optional.of(query.getSingleResult()).map(this::toDomain);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Produto> findAll() {
        TypedQuery<ProdutoJpaEntity> query = entityManager.createQuery(
                "SELECT p FROM ProdutoJpaEntity p", ProdutoJpaEntity.class);
        return query.getResultList().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Produto save(Produto produto) {
        ProdutoJpaEntity entity = toJpaEntity(produto);
        if (entityManager.find(ProdutoJpaEntity.class, entity.getId()) == null) {
            entityManager.persist(entity); // Nova entidade
        } else {
            entityManager.merge(entity); // Entidade existente
        }
        // Retorna a entidade de domínio atualizada (importante para IDs gerados ou outras modificações)
        return toDomain(entity);
    }

    @Override
    public void delete(ProdutoId id) {
        ProdutoJpaEntity entity = entityManager.find(ProdutoJpaEntity.class, id.getValue());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public boolean existsByCodigoBarras(String codigoBarras) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(p) FROM ProdutoJpaEntity p WHERE p.codigoBarras = :codigoBarras", Long.class);
        query.setParameter("codigoBarras", codigoBarras);
        return query.getSingleResult() > 0;
    }

    // --- Mappers entre Domínio e JPA Entity ---
    private Produto toDomain(ProdutoJpaEntity entity) {
        if (entity == null) return null;
        Categoria domainCategoria = (entity.getCategoria() != null) ?
                new Categoria(entity.getCategoria().getId(), entity.getCategoria().getNome(), entity.getCategoria().getDescricao()) : null;

        return new Produto(
                ProdutoId.from(entity.getId()),
                entity.getNome(),
                entity.getDescricao(),
                entity.getCodigoBarras(),
                domainCategoria,
                entity.getMarca(),
                Price.of(entity.getPrecoSugerido()),
                entity.isAtivo(),
                entity.getDataCadastro(),
                entity.getUltimaAtualizacao()
        );
    }

    private ProdutoJpaEntity toJpaEntity(Produto domain) {
        if (domain == null) return null;
        // Assume que a Categoria já existe no banco ou foi criada/persistida
        CategoriaJpaEntity jpaCategoria = (domain.getCategoria() != null && domain.getCategoria().getId() != null) ?
                entityManager.find(CategoriaJpaEntity.class, domain.getCategoria().getId()) : null;
        // Se a categoria no domínio foi criada sem ID (new Categoria(nome, desc)),
        // ela deve ser persistida antes que o produto possa referenciá-la.
        // O UseCase de CadastrarProduto já cuida disso.

        ProdutoJpaEntity entity = new ProdutoJpaEntity();
        entity.setId(domain.getId().getValue());
        entity.setNome(domain.getNome());
        entity.setDescricao(domain.getDescricao());
        entity.setCodigoBarras(domain.getCodigoBarras());
        entity.setCategoria(jpaCategoria); // Referência à entidade JPA de Categoria
        entity.setMarca(domain.getMarca());
        entity.setPrecoSugerido(domain.getPrecoSugerido().getValue());
        entity.setAtivo(domain.isAtivo());
        entity.setDataCadastro(domain.getDataCadastro());
        entity.setUltimaAtualizacao(domain.getUltimaAtualizacao());
        return entity;
    }
}