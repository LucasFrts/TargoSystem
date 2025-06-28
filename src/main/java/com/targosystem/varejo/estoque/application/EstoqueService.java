package com.targosystem.varejo.estoque.application;

import com.targosystem.varejo.estoque.application.input.RegistrarMovimentacaoEstoqueInput;
import com.targosystem.varejo.estoque.application.output.EstoqueOutput;
import com.targosystem.varejo.estoque.application.output.LocalEstoqueOutput;
import com.targosystem.varejo.estoque.application.output.MovimentacaoEstoqueOutput;
import com.targosystem.varejo.estoque.application.output.ItemEstoqueOutput; // NOVO IMPORT
import com.targosystem.varejo.estoque.application.queries.ConsultarEstoquePorProdutoIdAndLocalEstoqueId;
import com.targosystem.varejo.estoque.application.queries.ConsultarEstoqueTotalPorProdutoIdQuery;
import com.targosystem.varejo.estoque.application.queries.ConsultarItensEstoquePorLocalIdQuery; // NOVO IMPORT
import com.targosystem.varejo.estoque.application.queries.ConsultarQuantidadeTotalProdutoEmLocalQuery; // NOVO IMPORT
import com.targosystem.varejo.estoque.application.usecases.RegistrarMovimentacaoEstoqueUseCase;
import com.targosystem.varejo.estoque.domain.model.TipoLocal; // NOVO IMPORT
import com.targosystem.varejo.produtos.application.ProdutoService;
import com.targosystem.varejo.produtos.application.output.ProdutoOutput; // IMPORT CORRETO DO PACOTE PRODUTOS
import com.targosystem.varejo.shared.domain.DomainException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EstoqueService {

    private final RegistrarMovimentacaoEstoqueUseCase registrarMovimentacaoEstoqueUseCase;
    private final ConsultarEstoquePorProdutoIdAndLocalEstoqueId consultarEstoquePorProdutoIdAndLocalEstoqueIdQuery;
    private final ConsultarEstoqueTotalPorProdutoIdQuery consultarEstoqueTotalPorProdutoIdQuery;
    private final ConsultarItensEstoquePorLocalIdQuery consultarItensEstoquePorLocalIdQuery; // NOVO
    private final ConsultarQuantidadeTotalProdutoEmLocalQuery consultarQuantidadeTotalProdutoEmLocalQuery; // NOVO
    private final LocalEstoqueService localEstoqueService;
    private final ProdutoService produtoService; // Já existente

    public EstoqueService(
            ConsultarEstoquePorProdutoIdAndLocalEstoqueId consultarEstoquePorProdutoIdAndLocalEstoqueIdQuery,
            ConsultarEstoqueTotalPorProdutoIdQuery consultarEstoqueTotalPorProdutoIdQuery,
            ConsultarItensEstoquePorLocalIdQuery consultarItensEstoquePorLocalIdQuery, // Injetar
            ConsultarQuantidadeTotalProdutoEmLocalQuery consultarQuantidadeTotalProdutoEmLocalQuery, // Injetar
            RegistrarMovimentacaoEstoqueUseCase registrarMovimentacaoEstoqueUseCase,
            LocalEstoqueService localEstoqueService,
            ProdutoService produtoService
    ) {
        this.consultarEstoquePorProdutoIdAndLocalEstoqueIdQuery = Objects.requireNonNull(consultarEstoquePorProdutoIdAndLocalEstoqueIdQuery, "ConsultarEstoquePorProdutoIdAndLocalEstoqueIdQuery cannot be null.");
        this.consultarEstoqueTotalPorProdutoIdQuery = Objects.requireNonNull(consultarEstoqueTotalPorProdutoIdQuery, "ConsultarEstoqueTotalPorProdutoIdQuery cannot be null.");
        this.consultarItensEstoquePorLocalIdQuery = Objects.requireNonNull(consultarItensEstoquePorLocalIdQuery, "ConsultarItensEstoquePorLocalIdQuery cannot be null."); // NOVO
        this.consultarQuantidadeTotalProdutoEmLocalQuery = Objects.requireNonNull(consultarQuantidadeTotalProdutoEmLocalQuery, "ConsultarQuantidadeTotalProdutoEmLocalQuery cannot be null."); // NOVO
        this.registrarMovimentacaoEstoqueUseCase = Objects.requireNonNull(registrarMovimentacaoEstoqueUseCase, "RegistrarMovimentacaoEstoqueUseCase cannot be null.");
        this.localEstoqueService = Objects.requireNonNull(localEstoqueService, "LocalEstoqueService cannot be null.");
        this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService cannot be null.");
    }

    public MovimentacaoEstoqueOutput registrarMovimentacao(RegistrarMovimentacaoEstoqueInput input) throws DomainException {
        return registrarMovimentacaoEstoqueUseCase.execute(input);
    }

    public EstoqueOutput consultarEstoquePorProdutoIdELocal(String produtoId, String localEstoqueId) throws DomainException {
        return consultarEstoquePorProdutoIdAndLocalEstoqueIdQuery.execute(produtoId, localEstoqueId);
    }

    public EstoqueOutput consultarEstoqueTotalPorProdutoId(String produtoId) throws DomainException {
        return consultarEstoqueTotalPorProdutoIdQuery.execute(produtoId);
    }

    /**
     * NOVO MÉTODO: Lista locais de estoque por um tipo específico.
     * Delegates to LocalEstoqueService.
     */
    public List<LocalEstoqueOutput> listarLocaisEstoquePorTipo(TipoLocal tipo) {
        return localEstoqueService.listarLocaisPorTipo(tipo);
    }

    public LocalEstoqueOutput buscarLocalEstoquePorId(String localId) {
        return localEstoqueService.buscarLocalPorId(localId);
    }

    // Os métodos buscarProdutosPorNomeOuCodigoBarras e buscarProdutoPorId
    // já estão delegando corretamente para ProdutoService
    // e usando o ProdutoOutput do pacote produtos.application.output.
    public List<ProdutoOutput> buscarProdutosPorNomeOuCodigoBarras(String termoBusca) {
        return produtoService.buscarProdutosAtivosPorNomeOuCodigo(termoBusca); // Não precisa de stream.map se o output for o mesmo
    }

    public ProdutoOutput buscarProdutoPorId(String produtoId) {
        return produtoService.obterProdutoPorId(produtoId); // Não precisa de fromProdutoServiceOutput se o output for o mesmo
    }

    /**
     * NOVO MÉTODO: Consulta os itens de estoque para um LocalEstoque específico.
     * Utiliza uma nova Query.
     */
    public List<ItemEstoqueOutput> consultarItensEstoquePorLocalId(String localEstoqueId) throws DomainException {
        return consultarItensEstoquePorLocalIdQuery.execute(localEstoqueId);
    }

    /**
     * NOVO MÉTODO: Consulta a quantidade total de um produto em um LocalEstoque específico.
     * Utiliza uma nova Query.
     */
    public long consultarQuantidadeTotalProdutoEmLocal(String produtoId, String localEstoqueId) throws DomainException {
        return consultarQuantidadeTotalProdutoEmLocalQuery.execute(produtoId, localEstoqueId);
    }
}