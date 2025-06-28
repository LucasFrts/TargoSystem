
INSERT INTO papeis (id, nome, descricao) VALUES
(1, 'ADMINISTRADOR', 'Acesso completo e irrestrito ao sistema.'),
(2, 'GERENTE', 'Gerenciamento de módulos de negócio (produtos, vendas, estoque).'),
(3, 'VENDEDOR', 'Realiza vendas, consulta produtos e clientes.'),
(4, 'ESTOQUISTA', 'Gerencia entradas e saídas de produtos no estoque.'),
(5, 'CAIXA', 'Operação de caixa e fechamento de vendas.');
-- Local Interno: Loja Principal
INSERT INTO locais_estoque (id, ativo, data_atualizacao, data_criacao, nome, tipo)
VALUES ('loc-interna-loja', 1, NOW(), NOW(), 'Loja Principal', 'INTERNO');

-- Local Interno: Centro de Distribuição (Logística)
INSERT INTO locais_estoque (id, ativo, data_atualizacao, data_criacao, nome, tipo)
VALUES ('loc-interna-cd', 1, NOW(), NOW(), 'Centro de Distribuição', 'INTERNO');

-- Local de Estoque: Fornecedor Atacadão ABC
INSERT INTO locais_estoque (id, ativo, data_atualizacao, data_criacao, nome, tipo)
VALUES ('loc-fornecedor-abc', 1, NOW(), NOW(), 'Atacadão ABC Ltda. (Fornecedor)', 'FORNECEDOR');

-- Local de Estoque: Fornecedor Distribuidora XYZ
INSERT INTO locais_estoque (id, ativo, data_atualizacao, data_criacao, nome, tipo)
VALUES ('loc-fornecedor-xyz', 1, NOW(), NOW(), 'Distribuidora XYZ S.A. (Fornecedor)', 'FORNECEDOR');