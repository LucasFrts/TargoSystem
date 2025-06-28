package com.targosystem.varejo.fornecedores.infra.gui;

import com.targosystem.varejo.fornecedores.application.FornecedorService;
import com.targosystem.varejo.fornecedores.application.input.AtualizarFornecedorInput;
import com.targosystem.varejo.fornecedores.application.input.AvaliarEntregaFornecedorInput;
import com.targosystem.varejo.fornecedores.application.input.CriarFornecedorInput;
import com.targosystem.varejo.fornecedores.application.input.InativarFornecedorInput;
import com.targosystem.varejo.fornecedores.application.input.RegistrarEntregaFornecedorInput;
import com.targosystem.varejo.fornecedores.application.input.RegistrarRecebimentoEntregaInput;
import com.targosystem.varejo.fornecedores.application.output.EntregaFornecedorOutput;
import com.targosystem.varejo.fornecedores.application.output.FornecedorOutput;
import com.targosystem.varejo.shared.domain.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

public class FornecedorController {

    private static final Logger logger = LoggerFactory.getLogger(FornecedorController.class);

    private final FornecedorService fornecedorService;
    private final FornecedorFrame fornecedorFrame;

    private String currentFornecedorIdForEntregas = null;

    public FornecedorController(FornecedorService fornecedorService, FornecedorFrame fornecedorFrame) {
        this.fornecedorService = Objects.requireNonNull(fornecedorService, "FornecedorService cannot be null.");
        this.fornecedorFrame = Objects.requireNonNull(fornecedorFrame, "FornecedorFrame cannot be null.");

        // --- Listeners para o Painel de Fornecedores ---
        this.fornecedorFrame.getBtnAtualizarFornecedores().addActionListener(e -> listarTodosFornecedores());
        this.fornecedorFrame.getBtnNovoFornecedor().addActionListener(e -> criarNovoFornecedor());
        this.fornecedorFrame.getBtnEditarFornecedor().addActionListener(e -> editarFornecedorSelecionado());
        this.fornecedorFrame.getBtnInativarFornecedor().addActionListener(e -> inativarFornecedorSelecionado());
        this.fornecedorFrame.getBtnVerEntregas().addActionListener(e -> verEntregasFornecedorSelecionado());

        // Listener para seleção de linha na tabela de fornecedores
        this.fornecedorFrame.getFornecedoresTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean hasSelection = fornecedorFrame.getFornecedoresTable().getSelectedRow() != -1;
                    fornecedorFrame.getBtnEditarFornecedor().setEnabled(hasSelection);
                    fornecedorFrame.getBtnInativarFornecedor().setEnabled(hasSelection);
                    fornecedorFrame.getBtnVerEntregas().setEnabled(hasSelection);
                }
            }
        });

        // --- Listeners para o Painel de Entregas ---
        this.fornecedorFrame.getBtnRegistrarEntrega().addActionListener(e -> registrarNovaEntrega());
        this.fornecedorFrame.getBtnRegistrarRecebimento().addActionListener(e -> registrarRecebimentoEntregaSelecionada());
        this.fornecedorFrame.getBtnAvaliarEntrega().addActionListener(e -> avaliarEntregaSelecionada());
        this.fornecedorFrame.getBtnVoltarFornecedores().addActionListener(e -> voltarParaFornecedores());

        // Listener para seleção de linha na tabela de entregas
        this.fornecedorFrame.getEntregasTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean hasSelection = fornecedorFrame.getEntregasTable().getSelectedRow() != -1;
                    fornecedorFrame.getBtnRegistrarRecebimento().setEnabled(hasSelection);
                    fornecedorFrame.getBtnAvaliarEntrega().setEnabled(hasSelection);
                }
            }
        });

        // Listener para mudança de aba
        this.fornecedorFrame.getTabbedPane().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (fornecedorFrame.getTabbedPane().getSelectedIndex() == 0) { // Se voltou para a aba de Fornecedores
                    listarTodosFornecedores(); // Atualiza a lista de fornecedores
                    currentFornecedorIdForEntregas = null; // Reseta o ID do fornecedor de entregas
                    fornecedorFrame.getLblFornecedorSelecionado().setText("Fornecedor: Nenhum selecionado");
                }
            }
        });

        // Inicializa a lista de fornecedores ao carregar o controller
        listarTodosFornecedores();
    }

    public void listarTodosFornecedores() {
        logger.info("Listando todos os fornecedores...");
        fornecedorFrame.clearFornecedoresTable();

        try {
            List<FornecedorOutput> fornecedores = fornecedorService.listarTodosFornecedores();

            // CORREÇÃO AQUI: Criar um DefaultTableModel customizado
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    // Retorna a classe correta para a coluna "Ativo"
                    if (columnIndex == 12) { // Índice da coluna "Ativo" (0-based)
                        return Boolean.class;
                    }
                    // Para as outras colunas, assume-se String.
                    // Você pode adicionar mais verificações se tiver outras colunas de tipos específicos.
                    return super.getColumnClass(columnIndex);
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    // Torna todas as células não editáveis diretamente pela UI da tabela
                    return false;
                }
            };

            // Definir os identificadores das colunas no novo modelo
            model.setColumnIdentifiers(new Object[]{"ID", "Nome", "CNPJ", "Email Contato", "Telefone Contato",
                    "Logradouro", "Número", "Complemento", "Bairro", "Cidade",
                    "Estado", "CEP", "Ativo"});

            for (FornecedorOutput fornecedor : fornecedores) {
                model.addRow(new Object[]{
                        fornecedor.id(),
                        fornecedor.nome(),
                        fornecedor.cnpj(),
                        fornecedor.emailContato(),
                        fornecedor.telefoneContato(),
                        fornecedor.logradouro(),
                        fornecedor.numero(),
                        fornecedor.complemento(),
                        fornecedor.bairro(),
                        fornecedor.cidade(),
                        fornecedor.estado(),
                        fornecedor.cep(),
                        fornecedor.ativo() // Passando o Boolean diretamente
                });
            }

            // Atribuir o modelo customizado à tabela
            fornecedorFrame.setFornecedoresTableModel(model); // Certifique-se de que FornecedorFrame tem este setter

            logger.info("Total de {} fornecedores carregados.", fornecedores.size());
        } catch (DomainException e) {
            logger.error("Erro de domínio ao listar fornecedores: {}", e.getMessage());
            JOptionPane.showMessageDialog(fornecedorFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao listar fornecedores", e);
            JOptionPane.showMessageDialog(fornecedorFrame, "Erro inesperado ao listar fornecedores.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarNovoFornecedor() {
        logger.info("Abrindo diálogo para criar novo fornecedor...");
        // Passando valores default/nulos para todos os campos para um novo registro
        // A ordem do formData deve corresponder à ordem que showFornecedorDialog retorna.
        String[] formData = fornecedorFrame.showFornecedorDialog(true, null, null, null, null, null,
                null, null, null, null, null, null, null, true);

        if (formData != null) {
            try {
                CriarFornecedorInput input = new CriarFornecedorInput(
                        formData[1], // nome (índice 1 pois 0 seria o ID que é null para novo)
                        formData[2], // cnpj
                        formData[3], // emailContato
                        formData[4], // telefoneContato
                        formData[5], // logradouro
                        formData[6], // numero
                        formData[7], // complemento
                        formData[8], // bairro
                        formData[9], // cidade
                        formData[10], // estado
                        formData[11], // cep
                        Boolean.parseBoolean(formData[12]) // ativo
                );
                FornecedorOutput novoFornecedor = fornecedorService.criarFornecedor(input);
                JOptionPane.showMessageDialog(fornecedorFrame, "Fornecedor '" + novoFornecedor.nome() + "' criado com sucesso! ID: " + novoFornecedor.id(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                listarTodosFornecedores();
            } catch (DomainException e) {
                logger.error("Erro de domínio ao criar fornecedor: {}", e.getMessage());
                JOptionPane.showMessageDialog(fornecedorFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.error("Erro inesperado ao criar fornecedor", e);
                JOptionPane.showMessageDialog(fornecedorFrame, "Erro inesperado ao criar fornecedor.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            logger.info("Criação de fornecedor cancelada.");
        }
    }

    private void editarFornecedorSelecionado() {
        int selectedRow = fornecedorFrame.getFornecedoresTable().getSelectedRow();
        if (selectedRow != -1) {
            String id = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 0);
            String nome = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 1);
            String cnpj = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 2);
            String emailContato = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 3);
            String telefoneContato = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 4);
            String logradouro = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 5);
            String numero = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 6);
            String complemento = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 7);
            String bairro = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 8);
            String cidade = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 9);
            String estado = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 10);
            String cep = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 11);
            Boolean ativo = (Boolean) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 12);

            logger.info("Abrindo diálogo para editar fornecedor ID: {}", id);
            String[] formData = fornecedorFrame.showFornecedorDialog(false, id, nome, cnpj, emailContato, telefoneContato, logradouro, numero, complemento, bairro, cidade, estado, cep, ativo);

            if (formData != null) {
                try {
                    AtualizarFornecedorInput input = new AtualizarFornecedorInput(
                            formData[0], // id
                            formData[1], // nome
                            formData[2], // cnpj
                            formData[3], // emailContato
                            formData[4], // telefoneContato
                            formData[5], // logradouro
                            formData[6], // numero
                            formData[7], // complemento
                            formData[8], // bairro
                            formData[9], // cidade
                            formData[10], // estado
                            formData[11], // cep
                            Boolean.parseBoolean(formData[12]) // ativo
                    );
                    FornecedorOutput fornecedorAtualizado = fornecedorService.atualizarFornecedor(input);
                    JOptionPane.showMessageDialog(fornecedorFrame, "Fornecedor '" + fornecedorAtualizado.nome() + "' atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    listarTodosFornecedores();
                } catch (DomainException e) {
                    logger.error("Erro de domínio ao atualizar fornecedor: {}", e.getMessage());
                    JOptionPane.showMessageDialog(fornecedorFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    logger.error("Erro inesperado ao atualizar fornecedor", e);
                    JOptionPane.showMessageDialog(fornecedorFrame, "Erro inesperado ao atualizar fornecedor.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                logger.info("Edição de fornecedor cancelada.");
            }
        } else {
            JOptionPane.showMessageDialog(fornecedorFrame, "Nenhum fornecedor selecionado para edição.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void inativarFornecedorSelecionado() {
        int selectedRow = fornecedorFrame.getFornecedoresTable().getSelectedRow();
        if (selectedRow != -1) {
            String id = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 0);
            String nome = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 1);
            Boolean ativo = (Boolean) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 12);

            if (!ativo) {
                JOptionPane.showMessageDialog(fornecedorFrame, "Fornecedor já está inativo.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(fornecedorFrame,
                    "Tem certeza que deseja inativar o fornecedor '" + nome + "' (ID: " + id + ")?",
                    "Confirmar Inativação", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    logger.info("Inativando fornecedor ID: {}", id);
                    InativarFornecedorInput input = new InativarFornecedorInput(id);
                    FornecedorOutput fornecedorInativado = fornecedorService.inativarFornecedor(input);
                    JOptionPane.showMessageDialog(fornecedorFrame, "Fornecedor '" + fornecedorInativado.nome() + "' inativado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    listarTodosFornecedores();
                } catch (DomainException e) {
                    logger.error("Erro de domínio ao inativar fornecedor: {}", e.getMessage());
                    JOptionPane.showMessageDialog(fornecedorFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    logger.error("Erro inesperado ao inativar fornecedor", e);
                    JOptionPane.showMessageDialog(fornecedorFrame, "Erro inesperado ao inativar fornecedor.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(fornecedorFrame, "Nenhum fornecedor selecionado para inativar.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void verEntregasFornecedorSelecionado() {
        int selectedRow = fornecedorFrame.getFornecedoresTable().getSelectedRow();
        if (selectedRow != -1) {
            String fornecedorId = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 0);
            String nome = (String) fornecedorFrame.getFornecedoresTableModel().getValueAt(selectedRow, 1);

            currentFornecedorIdForEntregas = fornecedorId;
            fornecedorFrame.getLblFornecedorSelecionado().setText("Fornecedor: " + nome + " (ID: " + fornecedorId + ")");

            listarEntregasPorFornecedor(fornecedorId);
            fornecedorFrame.getTabbedPane().setSelectedIndex(1);
            fornecedorFrame.getTabbedPane().setEnabledAt(1, true);
            fornecedorFrame.getBtnRegistrarEntrega().setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(fornecedorFrame, "Selecione um fornecedor para ver as entregas.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void listarEntregasPorFornecedor(String fornecedorId) {
        logger.info("Listando entregas para o fornecedor ID: {}", fornecedorId);
        fornecedorFrame.clearEntregasTable();

        try {
            List<EntregaFornecedorOutput> entregas = fornecedorService.listarEntregasPorFornecedor(fornecedorId);
            DefaultTableModel model = fornecedorFrame.getEntregasTableModel();
            model.setColumnIdentifiers(new Object[]{"ID Entrega", "Fornecedor ID", "Nº Pedido Compra", "Data Prevista", "Data Realização", "Status", "Qtde Itens", "Avaliação", "Comentário"});

            for (EntregaFornecedorOutput entrega : entregas) {
                model.addRow(new Object[]{
                        entrega.id(),
                        entrega.fornecedorId(),
                        entrega.numeroPedidoCompra(),
                        entrega.dataPrevistaEntrega() != null ? entrega.dataPrevistaEntrega().toString() : "",
                        entrega.dataRealizacaoEntrega() != null ? entrega.dataRealizacaoEntrega().toString() : "Pendente",
                        entrega.status(),
                        entrega.quantidadeItens(),
                        entrega.avaliacaoNota() != null ? entrega.avaliacaoNota() : "N/A",
                        entrega.avaliacaoComentario() != null ? entrega.avaliacaoComentario() : ""
                });
            }
            logger.info("Total de {} entregas carregadas para o fornecedor ID: {}", entregas.size(), fornecedorId);
        } catch (DomainException e) {
            logger.error("Erro de domínio ao listar entregas: {}", e.getMessage());
            JOptionPane.showMessageDialog(fornecedorFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("Erro inesperado ao listar entregas para o fornecedor ID: {}", fornecedorId, e);
            JOptionPane.showMessageDialog(fornecedorFrame, "Erro inesperado ao listar entregas.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarNovaEntrega() {
        if (currentFornecedorIdForEntregas == null) {
            JOptionPane.showMessageDialog(fornecedorFrame, "Selecione um fornecedor primeiro para registrar uma entrega.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        logger.info("Abrindo diálogo para registrar nova entrega para fornecedor ID: {}", currentFornecedorIdForEntregas);

        String[] formData = fornecedorFrame.showRegistrarEntregaDialog(currentFornecedorIdForEntregas);

        if (formData != null) {
            try {
                String numeroPedidoCompra = formData[0];
                LocalDate dataPrevistaEntrega = LocalDate.parse(formData[1]);
                int quantidadeItens = Integer.parseInt(formData[2]);
                String observacoes = formData[3];

                RegistrarEntregaFornecedorInput input = new RegistrarEntregaFornecedorInput(
                        currentFornecedorIdForEntregas,
                        numeroPedidoCompra,
                        dataPrevistaEntrega,
                        quantidadeItens,
                        observacoes
                );

                EntregaFornecedorOutput novaEntrega = fornecedorService.registrarEntrega(input);
                JOptionPane.showMessageDialog(fornecedorFrame, "Entrega ID: " + novaEntrega.id() + " registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                listarEntregasPorFornecedor(currentFornecedorIdForEntregas);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(fornecedorFrame, "Formato de data inválido para Data Prevista Entrega. Use AAAA-MM-DD.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(fornecedorFrame, "Quantidade de Itens inválida. Digite um número inteiro.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            } catch (DomainException e) {
                logger.error("Erro de domínio ao registrar entrega: {}", e.getMessage());
                JOptionPane.showMessageDialog(fornecedorFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.error("Erro inesperado ao registrar entrega", e);
                JOptionPane.showMessageDialog(fornecedorFrame, "Erro inesperado ao registrar entrega.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            logger.info("Registro de entrega cancelado.");
        }
    }


    private void registrarRecebimentoEntregaSelecionada() {
        int selectedRow = fornecedorFrame.getEntregasTable().getSelectedRow();
        if (selectedRow != -1) {
            String entregaId = (String) fornecedorFrame.getEntregasTableModel().getValueAt(selectedRow, 0);
            String statusAtual = (String) fornecedorFrame.getEntregasTableModel().getValueAt(selectedRow, 5);

            if (!"PEDIDO_REALIZADO".equals(statusAtual) && !"EM_TRANSPORTE".equals(statusAtual)) {
                JOptionPane.showMessageDialog(fornecedorFrame, "Entrega não está em um status que permita o recebimento.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            logger.info("Abrindo diálogo para registrar recebimento da entrega ID: {}", entregaId);
            String dataRecebimentoStr = fornecedorFrame.showRegistrarRecebimentoDialog(entregaId);

            if (dataRecebimentoStr != null) {
                try {
                    LocalDate dataRecebimento = LocalDate.parse(dataRecebimentoStr);
                    RegistrarRecebimentoEntregaInput input = new RegistrarRecebimentoEntregaInput(
                            entregaId,
                            dataRecebimento
                    );
                    EntregaFornecedorOutput entregaAtualizada = fornecedorService.registrarRecebimentoEntrega(input);
                    JOptionPane.showMessageDialog(fornecedorFrame, "Recebimento da Entrega ID: " + entregaAtualizada.id() + " registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    listarEntregasPorFornecedor(currentFornecedorIdForEntregas);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(fornecedorFrame, "Formato de data inválido. Use AAAA-MM-DD.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                } catch (DomainException e) {
                    logger.error("Erro de domínio ao registrar recebimento: {}", e.getMessage());
                    JOptionPane.showMessageDialog(fornecedorFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    logger.error("Erro inesperado ao registrar recebimento da entrega", e);
                    JOptionPane.showMessageDialog(fornecedorFrame, "Erro inesperado ao registrar recebimento.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                logger.info("Registro de recebimento cancelado.");
            }
        } else {
            JOptionPane.showMessageDialog(fornecedorFrame, "Nenhuma entrega selecionada para registrar recebimento.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void avaliarEntregaSelecionada() {
        int selectedRow = fornecedorFrame.getEntregasTable().getSelectedRow();
        if (selectedRow != -1) {
            String entregaId = (String) fornecedorFrame.getEntregasTableModel().getValueAt(selectedRow, 0);
            String statusAtual = (String) fornecedorFrame.getEntregasTableModel().getValueAt(selectedRow, 5);
            Integer avaliacaoExistente = null;
            Object avaliacaoObj = fornecedorFrame.getEntregasTableModel().getValueAt(selectedRow, 7);
            if (avaliacaoObj instanceof Integer) {
                avaliacaoExistente = (Integer) avaliacaoObj;
            } else if (avaliacaoObj instanceof String && !"N/A".equals(avaliacaoObj)) {
                try {
                    avaliacaoExistente = Integer.parseInt((String) avaliacaoObj);
                } catch (NumberFormatException e) {
                    logger.warn("Não foi possível converter a avaliação '{}' para Integer.", avaliacaoObj);
                }
            }

            String observacoesExistente = (String) fornecedorFrame.getEntregasTableModel().getValueAt(selectedRow, 8);


            if (!"ENTREGUE".equals(statusAtual) && !"RECEBIDO_PARCIAL".equals(statusAtual)) {
                JOptionPane.showMessageDialog(fornecedorFrame, "Entrega não está em um status que permita avaliação.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            logger.info("Abrindo diálogo para avaliar entrega ID: {}", entregaId);
            String[] formData = fornecedorFrame.showAvaliarEntregaDialog(entregaId, avaliacaoExistente, observacoesExistente);

            if (formData != null) {
                try {
                    Integer avaliacao = Integer.parseInt(formData[0]);
                    String observacoes = formData[1];

                    AvaliarEntregaFornecedorInput input = new AvaliarEntregaFornecedorInput(
                            entregaId,
                            avaliacao,
                            observacoes
                    );
                    EntregaFornecedorOutput entregaAtualizada = fornecedorService.avaliarEntrega(input);
                    JOptionPane.showMessageDialog(fornecedorFrame, "Entrega ID: " + entregaAtualizada.id() + " avaliada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    listarEntregasPorFornecedor(currentFornecedorIdForEntregas);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(fornecedorFrame, "Avaliação deve ser um número entre 1 e 5.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                } catch (DomainException e) {
                    logger.error("Erro de domínio ao avaliar entrega: {}", e.getMessage());
                    JOptionPane.showMessageDialog(fornecedorFrame, "Erro de domínio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    logger.error("Erro inesperado ao avaliar entrega", e);
                    JOptionPane.showMessageDialog(fornecedorFrame, "Erro inesperado ao avaliar entrega.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                logger.info("Avaliação de entrega cancelada.");
            }
        } else {
            JOptionPane.showMessageDialog(fornecedorFrame, "Nenhuma entrega selecionada para avaliação.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void voltarParaFornecedores() {
        fornecedorFrame.getTabbedPane().setSelectedIndex(0);
        fornecedorFrame.getTabbedPane().setEnabledAt(1, false);
        fornecedorFrame.getBtnRegistrarEntrega().setEnabled(false);
        currentFornecedorIdForEntregas = null;
        fornecedorFrame.getLblFornecedorSelecionado().setText("Fornecedor: Nenhum selecionado");
        fornecedorFrame.clearEntregasTable();
        listarTodosFornecedores();
    }
}