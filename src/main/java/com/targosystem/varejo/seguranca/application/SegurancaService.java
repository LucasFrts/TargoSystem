package com.targosystem.varejo.seguranca.application;

import com.targosystem.varejo.seguranca.application.input.CriarUsuarioInput;
import com.targosystem.varejo.seguranca.application.input.LoginInput;
import com.targosystem.varejo.seguranca.application.output.UsuarioOutput;
import com.targosystem.varejo.seguranca.application.output.PapelOutput;
import com.targosystem.varejo.seguranca.application.query.ListarUsuariosQuery;
import com.targosystem.varejo.seguranca.application.query.ObterUsuarioPorIdQuery;
import com.targosystem.varejo.seguranca.application.usecases.AtualizarUsuarioUseCase;
import com.targosystem.varejo.seguranca.application.usecases.CriarUsuarioUseCase;
import com.targosystem.varejo.seguranca.application.usecases.LoginUsuarioUseCase;
import com.targosystem.varejo.seguranca.domain.repository.PapelRepository;
import com.targosystem.varejo.shared.domain.DomainException;
import jakarta.persistence.EntityManager; // NOVO: Importar EntityManager
import jakarta.persistence.EntityTransaction; // NOVO: Importar EntityTransaction
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SegurancaService {

    private static final Logger logger = LoggerFactory.getLogger(SegurancaService.class);

    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    private final LoginUsuarioUseCase loginUsuarioUseCase;
    private final ObterUsuarioPorIdQuery obterUsuarioPorIdQuery;
    private final ListarUsuariosQuery listarUsuariosQuery;
    private final PapelRepository papelRepository;
    private final EntityManager entityManager; // NOVO: Campo para EntityManager

    public SegurancaService(CriarUsuarioUseCase criarUsuarioUseCase,
                            AtualizarUsuarioUseCase atualizarUsuarioUseCase,
                            LoginUsuarioUseCase loginUsuarioUseCase,
                            ObterUsuarioPorIdQuery obterUsuarioPorIdQuery,
                            ListarUsuariosQuery listarUsuariosQuery,
                            PapelRepository papelRepository,
                            EntityManager entityManager) { // NOVO: Receber EntityManager no construtor
        this.criarUsuarioUseCase = Objects.requireNonNull(criarUsuarioUseCase, "CriarUsuarioUseCase cannot be null");
        this.atualizarUsuarioUseCase = Objects.requireNonNull(atualizarUsuarioUseCase, "AtualizarUsuarioUseCase cannot be null");
        this.loginUsuarioUseCase = Objects.requireNonNull(loginUsuarioUseCase, "LoginUsuarioUseCase cannot be null");
        this.obterUsuarioPorIdQuery = Objects.requireNonNull(obterUsuarioPorIdQuery, "ObterUsuarioPorIdQuery cannot be null");
        this.listarUsuariosQuery = Objects.requireNonNull(listarUsuariosQuery, "ListarUsuariosQuery cannot be null");
        this.papelRepository = Objects.requireNonNull(papelRepository, "PapelRepository cannot be null");
        this.entityManager = Objects.requireNonNull(entityManager, "EntityManager cannot be null"); // NOVO: Atribuir EM
    }

    public UsuarioOutput criarUsuario(CriarUsuarioInput input) throws DomainException {
        EntityTransaction transaction = entityManager.getTransaction(); // Gerencia a transação aqui
        try {
            transaction.begin();
            UsuarioOutput output = criarUsuarioUseCase.execute(input); // Chama o Use Case
            transaction.commit();
            return output;
        } catch (DomainException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e; // Re-lança exceções de domínio
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Erro inesperado ao criar usuário no service: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao criar usuário: " + e.getMessage(), e); // Lança RuntimeException
        }
    }

    public UsuarioOutput atualizarUsuario(String id){
        // Implemente a lógica de atualização aqui
        throw new UnsupportedOperationException("Atualizar usuário não implementado.");
    }

    public UsuarioOutput loginUsuario(LoginInput input) throws DomainException {
        // O login é uma operação de leitura/consulta, geralmente não precisa de transação de escrita,
        // mas pode precisar de transação de leitura se seu provedor JPA exigir ou se houver Lazy Loading.
        // Se houver atualização de ultimoLogin, precisará de transação.
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            UsuarioOutput output = loginUsuarioUseCase.execute(input);
            transaction.commit(); // Comita para salvar ultimoLogin
            return output;
        } catch (DomainException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Erro inesperado durante o login: {}", e.getMessage(), e);
            throw new RuntimeException("Falha no login: " + e.getMessage(), e);
        }
    }

    public UsuarioOutput obterUsuarioPorId(String id) {
        // Operações de consulta (findAll, findById) normalmente não precisam de transação de escrita.
        // Se houver Lazy Loading de coleções, pode ser necessário manter o EntityManager aberto
        // durante a vida do objeto de domínio, ou usar uma transação de leitura.
        return obterUsuarioPorIdQuery.execute(id);
    }

    public List<UsuarioOutput> listarTodosUsuarios() {
        return listarUsuariosQuery.execute();
    }

    public List<PapelOutput> listarPapeis() {
        return papelRepository.findAll().stream()
                .map(papel -> new PapelOutput(papel.getId(), papel.getNome(), papel.getDescricao()))
                .collect(Collectors.toList());
    }
}