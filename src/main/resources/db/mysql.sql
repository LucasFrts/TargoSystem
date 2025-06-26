-- targo_system_schema.sql

-- Database: targo_system
-- Use: USE targo_system;

-- -----------------------------------------------------
-- Bounded Context: produtos
-- -----------------------------------------------------

-- Table: categorias
CREATE TABLE IF NOT EXISTS `categorias` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(100) NOT NULL UNIQUE,
    `descricao` TEXT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

-- Table: produtos
CREATE TABLE IF NOT EXISTS `produtos` (
    `id` VARCHAR(36) NOT NULL,
    `nome` VARCHAR(255) NOT NULL,
    `descricao` TEXT NULL,
    `codigo_barras` VARCHAR(50) NOT NULL UNIQUE,
    `categoria_id` INT NULL,
    `marca` VARCHAR(100) NULL,
    `preco_sugerido` DECIMAL(10, 2) NOT NULL,
    `ativo` BOOLEAN NOT NULL DEFAULT TRUE,
    `data_cadastro` DATETIME NOT NULL,
    `ultima_atualizacao` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_produtos_categoria_id`
        FOREIGN KEY (`categoria_id`)
        REFERENCES `categorias` (`id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Bounded Context: fornecedores
-- -----------------------------------------------------

-- Table: fornecedores
CREATE TABLE IF NOT EXISTS `fornecedores` (
    `id` VARCHAR(36) NOT NULL,
    `nome_fantasia` VARCHAR(255) NOT NULL,
    `razao_social` VARCHAR(255) NULL,
    `cnpj` VARCHAR(18) NOT NULL UNIQUE,
    `telefone` VARCHAR(20) NULL,
    `email` VARCHAR(255) NULL,
    `endereco` VARCHAR(500) NULL,
    `ativo` BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

-- Table: entregas_fornecedores
CREATE TABLE IF NOT EXISTS `entregas_fornecedores` (
    `id` VARCHAR(36) NOT NULL,
    `fornecedor_id` VARCHAR(36) NOT NULL,
    `data_hora_entrega` DATETIME NOT NULL,
    `nome_entregador` VARCHAR(255) NULL,
    `observacoes` TEXT NULL,
    `avaliacao` INT NULL,
    `data_avaliacao` DATETIME NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_entregas_fornecedores_fornecedor_id`
        FOREIGN KEY (`fornecedor_id`)
        REFERENCES `fornecedores` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Table: itens_entrega
CREATE TABLE IF NOT EXISTS `itens_entrega` (
    `id` VARCHAR(36) NOT NULL,
    `entrega_fornecedor_id` VARCHAR(36) NOT NULL,
    `produto_id` VARCHAR(36) NOT NULL,
    `lote_numero` VARCHAR(100) NOT NULL,
    `quantidade_entregue` INT NOT NULL,
    `data_vencimento_lote` DATE NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_itens_entrega_entrega_fornecedor_id`
        FOREIGN KEY (`entrega_fornecedor_id`)
        REFERENCES `entregas_fornecedores` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_itens_entrega_produto_id`
        FOREIGN KEY (`produto_id`)
        REFERENCES `produtos` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Bounded Context: estoque
-- -----------------------------------------------------

-- Table: estoque_items
CREATE TABLE IF NOT EXISTS `estoque_items` (
    `id` VARCHAR(36) NOT NULL,
    `produto_id` VARCHAR(36) NOT NULL,
    `lote_numero` VARCHAR(100) NOT NULL,
    `data_vencimento` DATE NULL,
    `quantidade_disponivel` INT NOT NULL,
    `quantidade_reservada` INT NOT NULL DEFAULT 0,
    `localizacao` VARCHAR(255) NULL,
    `data_entrada` DATETIME NOT NULL,
    `ultima_atualizacao` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_estoque_items_produto_id`
        FOREIGN KEY (`produto_id`)
        REFERENCES `produtos` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Table: movimentacoes_estoque
CREATE TABLE IF NOT EXISTS `movimentacoes_estoque` (
    `id` VARCHAR(36) NOT NULL,
    `produto_id` VARCHAR(36) NOT NULL,
    `lote_numero` VARCHAR(100) NULL,
    `tipo_movimentacao` VARCHAR(50) NOT NULL, -- 'ENTRADA', 'SAIDA', 'TRANSFERENCIA', 'DEVOLUCAO'
    `quantidade` INT NOT NULL,
    `data_hora` DATETIME NOT NULL,
    `origem` VARCHAR(255) NULL,
    `destino` VARCHAR(255) NULL,
    `usuario_responsavel_id` VARCHAR(36) NULL, -- FK para usuarios.id (definida no contexto de seguranca)
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_movimentacoes_estoque_produto_id`
        FOREIGN KEY (`produto_id`)
        REFERENCES `produtos` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
    -- CONSTRAINT `fk_movimentacoes_estoque_usuario_id` (Adicionada após a criação da tabela usuarios)
) ENGINE = InnoDB;

-- Table: niveis_minimos_estoque
CREATE TABLE IF NOT EXISTS `niveis_minimos_estoque` (
    `produto_id` VARCHAR(36) NOT NULL,
    `nivel_minimo` INT NOT NULL,
    `ultimo_alerta` DATETIME NULL,
    PRIMARY KEY (`produto_id`),
    CONSTRAINT `fk_niveis_minimos_estoque_produto_id`
        FOREIGN KEY (`produto_id`)
        REFERENCES `produtos` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Table: pedidos_reposicao
CREATE TABLE IF NOT EXISTS `pedidos_reposicao` (
    `id` VARCHAR(36) NOT NULL,
    `produto_id` VARCHAR(36) NOT NULL,
    `fornecedor_id` VARCHAR(36) NULL,
    `quantidade_solicitada` INT NOT NULL,
    `data_criacao` DATETIME NOT NULL,
    `status` VARCHAR(50) NOT NULL, -- 'PENDENTE', 'ENVIADO', 'RECEBIDO', 'CANCELADO'
    `data_envio` DATETIME NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_pedidos_reposicao_produto_id`
        FOREIGN KEY (`produto_id`)
        REFERENCES `produtos` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_pedidos_reposicao_fornecedor_id`
        FOREIGN KEY (`fornecedor_id`)
        REFERENCES `fornecedores` (`id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Table: reservas_estoque
CREATE TABLE IF NOT EXISTS `reservas_estoque` (
    `id` VARCHAR(36) NOT NULL,
    `produto_id` VARCHAR(36) NOT NULL,
    `quantidade` INT NOT NULL,
    `cliente_id` VARCHAR(36) NULL, -- Pode ser FK para uma futura tabela de clientes
    `data_reserva` DATETIME NOT NULL,
    `data_expiracao` DATETIME NULL,
    `status` VARCHAR(50) NOT NULL, -- 'ATIVA', 'UTILIZADA', 'CANCELADA'
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_reservas_estoque_produto_id`
        FOREIGN KEY (`produto_id`)
        REFERENCES `produtos` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Bounded Context: seguranca (definido primeiro para FKs posteriores)
-- -----------------------------------------------------

-- Table: usuarios
CREATE TABLE IF NOT EXISTS `usuarios` (
    `id` VARCHAR(36) NOT NULL,
    `username` VARCHAR(100) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `nome_completo` VARCHAR(255) NULL,
    `email` VARCHAR(255) UNIQUE NULL,
    `ativo` BOOLEAN NOT NULL DEFAULT TRUE,
    `data_cadastro` DATETIME NOT NULL,
    `ultimo_login` DATETIME NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

-- Table: papeis
CREATE TABLE IF NOT EXISTS `papeis` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(50) NOT NULL UNIQUE,
    `descricao` TEXT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

-- Table: usuario_papeis
CREATE TABLE IF NOT EXISTS `usuario_papeis` (
    `usuario_id` VARCHAR(36) NOT NULL,
    `papel_id` INT NOT NULL,
    PRIMARY KEY (`usuario_id`, `papel_id`),
    CONSTRAINT `fk_usuario_papeis_usuario_id`
        FOREIGN KEY (`usuario_id`)
        REFERENCES `usuarios` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_usuario_papeis_papel_id`
        FOREIGN KEY (`papel_id`)
        REFERENCES `papeis` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Adicionando FK para usuario_responsavel_id na tabela movimentacoes_estoque
ALTER TABLE `movimentacoes_estoque`
ADD CONSTRAINT `fk_movimentacoes_estoque_usuario_id`
FOREIGN KEY (`usuario_responsavel_id`)
REFERENCES `usuarios` (`id`)
ON DELETE SET NULL
ON UPDATE CASCADE;


-- -----------------------------------------------------
-- Bounded Context: vendas
-- -----------------------------------------------------

-- Table: vendas
CREATE TABLE IF NOT EXISTS `vendas` (
    `id` VARCHAR(36) NOT NULL,
    `cliente_id` VARCHAR(36) NULL, -- FK para clientes.id (se uma tabela de clientes for criada)
    `data_hora_venda` DATETIME NOT NULL,
    `valor_total` DECIMAL(10, 2) NOT NULL,
    `desconto_aplicado` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `status` VARCHAR(50) NOT NULL, -- 'COMPLETADA', 'CANCELADA', 'PENDENTE'
    `usuario_vendedor_id` VARCHAR(36) NULL, -- FK para usuarios.id
    `observacoes` TEXT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_vendas_usuario_vendedor_id`
        FOREIGN KEY (`usuario_vendedor_id`)
        REFERENCES `usuarios` (`id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Table: itens_venda
CREATE TABLE IF NOT EXISTS `itens_venda` (
    `id` VARCHAR(36) NOT NULL,
    `venda_id` VARCHAR(36) NOT NULL,
    `produto_id` VARCHAR(36) NOT NULL,
    `quantidade` INT NOT NULL,
    `preco_unitario` DECIMAL(10, 2) NOT NULL,
    `desconto_item` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `valor_total_item` DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_itens_venda_venda_id`
        FOREIGN KEY (`venda_id`)
        REFERENCES `vendas` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_itens_venda_produto_id`
        FOREIGN KEY (`produto_id`)
        REFERENCES `produtos` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Table: historico_precos_produtos
CREATE TABLE IF NOT EXISTS `historico_precos_produtos` (
    `id` VARCHAR(36) NOT NULL,
    `produto_id` VARCHAR(36) NOT NULL,
    `preco` DECIMAL(10, 2) NOT NULL,
    `data_inicio_vigencia` DATETIME NOT NULL,
    `data_fim_vigencia` DATETIME NULL,
    `motivo_ajuste` VARCHAR(255) NULL,
    `usuario_id` VARCHAR(36) NULL, -- FK para usuarios.id
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_historico_precos_produtos_produto_id`
        FOREIGN KEY (`produto_id`)
        REFERENCES `produtos` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_historico_precos_produtos_usuario_id`
        FOREIGN KEY (`usuario_id`)
        REFERENCES `usuarios` (`id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Table: devolucoes
CREATE TABLE IF NOT EXISTS `devolucoes` (
    `id` VARCHAR(36) NOT NULL,
    `venda_id` VARCHAR(36) NOT NULL,
    `data_hora_devolucao` DATETIME NOT NULL,
    `motivo` TEXT NULL,
    `valor_total_devolvido` DECIMAL(10, 2) NOT NULL,
    `status` VARCHAR(50) NOT NULL, -- 'PROCESSADA', 'PENDENTE'
    `usuario_id` VARCHAR(36) NULL, -- FK para usuarios.id
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_devolucoes_venda_id`
        FOREIGN KEY (`venda_id`)
        REFERENCES `vendas` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_devolucoes_usuario_id`
        FOREIGN KEY (`usuario_id`)
        REFERENCES `usuarios` (`id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Table: itens_devolucao
CREATE TABLE IF NOT EXISTS `itens_devolucao` (
    `id` VARCHAR(36) NOT NULL,
    `devolucao_id` VARCHAR(36) NOT NULL,
    `produto_id` VARCHAR(36) NOT NULL,
    `lote_numero` VARCHAR(100) NULL,
    `quantidade` INT NOT NULL,
    `preco_unitario_devolvido` DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_itens_devolucao_devolucao_id`
        FOREIGN KEY (`devolucao_id`)
        REFERENCES `devolucoes` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_itens_devolucao_produto_id`
        FOREIGN KEY (`produto_id`)
        REFERENCES `produtos` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Bounded Context: promocoes
-- -----------------------------------------------------

-- Table: promocoes
CREATE TABLE IF NOT EXISTS `promocoes` (
    `id` VARCHAR(36) NOT NULL,
    `nome` VARCHAR(255) NOT NULL,
    `descricao` TEXT NULL,
    `tipo_promocao` VARCHAR(50) NOT NULL, -- 'DESCONTO_ITEM', 'DESCONTO_TOTAL', 'KIT'
    `data_inicio` DATETIME NOT NULL,
    `data_fim` DATETIME NOT NULL,
    `valor_desconto` DECIMAL(10, 2) NULL,
    `percentual_desconto` DECIMAL(5, 2) NULL,
    `ativa` BOOLEAN NOT NULL DEFAULT TRUE,
    `criterio_min_quantidade` INT NULL,
    `criterio_valor_minimo` DECIMAL(10, 2) NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

-- Table: itens_promocao (para promoções que afetam itens específicos)
CREATE TABLE IF NOT EXISTS `itens_promocao` (
    `id` VARCHAR(36) NOT NULL,
    `promocao_id` VARCHAR(36) NOT NULL,
    `produto_id` VARCHAR(36) NOT NULL,
    `quantidade_requerida` INT NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_itens_promocao_promocao_id`
        FOREIGN KEY (`promocao_id`)
        REFERENCES `promocoes` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_itens_promocao_produto_id`
        FOREIGN KEY (`produto_id`)
        REFERENCES `produtos` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Table: kits_promocionais
CREATE TABLE IF NOT EXISTS `kits_promocionais` (
    `id` VARCHAR(36) NOT NULL,
    `nome_kit` VARCHAR(255) NOT NULL,
    `descricao_kit` TEXT NULL,
    `preco_kit` DECIMAL(10, 2) NOT NULL,
    `data_inicio` DATETIME NOT NULL,
    `data_fim` DATETIME NOT NULL,
    `ativo` BOOLEAN NOT NULL DEFAULT TRUE,
    `sugestao_automatica` BOOLEAN NOT NULL DEFAULT FALSE,
    `motivo_sugestao` TEXT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

-- Table: itens_kit
CREATE TABLE IF NOT EXISTS `itens_kit` (
    `id` VARCHAR(36) NOT NULL,
    `kit_promocional_id` VARCHAR(36) NOT NULL,
    `produto_id` VARCHAR(36) NOT NULL,
    `quantidade` INT NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_itens_kit_kit_promocional_id`
        FOREIGN KEY (`kit_promocional_id`)
        REFERENCES `kits_promocionais` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_itens_kit_produto_id`
        FOREIGN KEY (`produto_id`)
        REFERENCES `produtos` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Bounded Context: relatorios
-- -----------------------------------------------------

-- Table: dashboard_data (para dados de relatórios pré-computados, se necessário)
CREATE TABLE IF NOT EXISTS `dashboard_data` (
    `id` VARCHAR(36) NOT NULL,
    `tipo_metrica` VARCHAR(100) NOT NULL,
    `data_referencia` DATE NOT NULL,
    `valor_numerico` DECIMAL(18, 2) NULL,
    `valor_texto` TEXT NULL,
    `ultima_atualizacao` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE (`tipo_metrica`, `data_referencia`) -- Para métricas diárias, por exemplo
) ENGINE = InnoDB;

INSERT INTO papeis (id, nome, descricao) VALUES
(1, 'ADMINISTRADOR', 'Acesso completo e irrestrito ao sistema.'),
(2, 'GERENTE', 'Gerenciamento de módulos de negócio (produtos, vendas, estoque).'),
(3, 'VENDEDOR', 'Realiza vendas, consulta produtos e clientes.'),
(4, 'ESTOQUISTA', 'Gerencia entradas e saídas de produtos no estoque.'),
(5, 'CAIXA', 'Operação de caixa e fechamento de vendas.');
