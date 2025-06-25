package com.targosystem.varejo.produtos.domain.listeners;

import com.targosystem.varejo.produtos.domain.events.ProdutoCadastradoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Um listener de exemplo que apenas loga o evento de Produto Cadastrado.
 * Em um cenário real, poderia, por exemplo, enviar uma notificação,
 * atualizar um sistema de BI, ou disparar uma sincronização.
 */
public class ProdutoCadastradoLoggerListener implements Consumer<ProdutoCadastradoEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoCadastradoLoggerListener.class);

    @Override
    public void accept(ProdutoCadastradoEvent event) {
        logger.info("ProdutoCadastradoLoggerListener: Evento recebido para Produto ID '{}', Nome: '{}' em {}.",
                event.getProdutoId(), event.getNomeProduto(), event.getOcorreuEm());
        // Aqui você adicionaria a lógica de negócio real que precisa reagir ao evento.
        // Por exemplo:
        // - Enviar um email de notificação para a equipe de marketing
        // - Atualizar um índice de busca de produtos
        // - Inserir um registro em uma tabela de auditoria
    }
}