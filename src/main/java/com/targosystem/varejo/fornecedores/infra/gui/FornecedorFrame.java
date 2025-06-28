package com.targosystem.varejo.fornecedores.infra.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.ParseException;
import javax.swing.text.MaskFormatter;

public class FornecedorFrame extends JPanel {

    private JTabbedPane tabbedPane;

    // --- Painel de Fornecedores ---
    private JTable fornecedoresTable;
    private DefaultTableModel fornecedoresTableModel; // Este será substituído pelo modelo customizado no Controller
    private JButton btnNovoFornecedor;
    private JButton btnEditarFornecedor;
    private JButton btnInativarFornecedor;
    private JButton btnAtualizarFornecedores;
    private JButton btnVerEntregas; // Novo botão para ver entregas

    // --- Painel de Entregas ---
    private JTable entregasTable;
    private DefaultTableModel entregasTableModel;
    private JButton btnRegistrarEntrega;
    private JButton btnRegistrarRecebimento;
    private JButton btnAvaliarEntrega;
    private JButton btnVoltarFornecedores; // Botão para voltar à lista de fornecedores
    private JLabel lblFornecedorSelecionado; // Para mostrar qual fornecedor as entregas pertencem

    public FornecedorFrame() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        // --- Configurar Painel de Gerenciamento de Fornecedores ---
        JPanel fornecedorPanel = new JPanel(new BorderLayout(10, 10));
        fornecedorPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Gerenciamento de Fornecedores", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        // Inicializa o table model aqui, mas ele será substituído pelo customizado no Controller
        // É importante que o número de colunas e a ordem inicial reflitam o que será usado no Controller.
        // As colunas devem ser: ID, Nome, CNPJ, Email Contato, Telefone Contato, Logradouro, Número, Complemento, Bairro, Cidade, Estado, CEP, Ativo
        fornecedoresTableModel = new DefaultTableModel(
                new Object[]{"ID", "Nome", "CNPJ", "Email Contato", "Telefone Contato",
                        "Logradouro", "Número", "Complemento", "Bairro", "Cidade",
                        "Estado", "CEP", "Ativo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Células da tabela não editáveis
            }
            // OBS: getColumnClass será sobrescrito pelo modelo customizado no FornecedorController.
            // Manter este aqui apenas para a inicialização.
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // A coluna "Ativo" agora está no índice 12 (contando do 0)
                if (columnIndex == 12) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        fornecedoresTable = new JTable(fornecedoresTableModel);
        fornecedoresTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Ocultar algumas colunas do endereço para não poluir demais a visualização, mas manter no modelo
        fornecedoresTable.getColumnModel().getColumn(5).setMinWidth(0);
        fornecedoresTable.getColumnModel().getColumn(5).setMaxWidth(0);
        fornecedoresTable.getColumnModel().getColumn(5).setWidth(0);
        fornecedoresTable.getColumnModel().getColumn(6).setMinWidth(0);
        fornecedoresTable.getColumnModel().getColumn(6).setMaxWidth(0);
        fornecedoresTable.getColumnModel().getColumn(6).setWidth(0);
        fornecedoresTable.getColumnModel().getColumn(7).setMinWidth(0);
        fornecedoresTable.getColumnModel().getColumn(7).setMaxWidth(0);
        fornecedoresTable.getColumnModel().getColumn(7).setWidth(0);
        fornecedoresTable.getColumnModel().getColumn(8).setMinWidth(0);
        fornecedoresTable.getColumnModel().getColumn(8).setMaxWidth(0);
        fornecedoresTable.getColumnModel().getColumn(8).setWidth(0);
        fornecedoresTable.getColumnModel().getColumn(9).setMinWidth(0);
        fornecedoresTable.getColumnModel().getColumn(9).setMaxWidth(0);
        fornecedoresTable.getColumnModel().getColumn(9).setWidth(0);
        fornecedoresTable.getColumnModel().getColumn(10).setMinWidth(0);
        fornecedoresTable.getColumnModel().getColumn(10).setMaxWidth(0);
        fornecedoresTable.getColumnModel().getColumn(10).setWidth(0);
        fornecedoresTable.getColumnModel().getColumn(11).setMinWidth(0);
        fornecedoresTable.getColumnModel().getColumn(11).setMaxWidth(0);
        fornecedoresTable.getColumnModel().getColumn(11).setWidth(0);


        JScrollPane fornecedorScrollPane = new JScrollPane(fornecedoresTable);
        fornecedorPanel.add(fornecedorScrollPane, BorderLayout.CENTER);

        JPanel fornecedorButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnNovoFornecedor = new JButton("Novo Fornecedor");
        btnEditarFornecedor = new JButton("Editar Fornecedor");
        btnInativarFornecedor = new JButton("Inativar Fornecedor");
        btnAtualizarFornecedores = new JButton("Atualizar Lista");
        btnVerEntregas = new JButton("Ver Entregas");

        fornecedorButtonPanel.add(btnNovoFornecedor);
        fornecedorButtonPanel.add(btnEditarFornecedor);
        fornecedorButtonPanel.add(btnInativarFornecedor);
        fornecedorButtonPanel.add(btnAtualizarFornecedores);
        fornecedorButtonPanel.add(btnVerEntregas);

        btnEditarFornecedor.setEnabled(false);
        btnInativarFornecedor.setEnabled(false);
        btnVerEntregas.setEnabled(false); // Desabilita até que um fornecedor seja selecionado

        fornecedorPanel.add(fornecedorButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Fornecedores", fornecedorPanel);

        // --- Configurar Painel de Gerenciamento de Entregas ---
        JPanel entregasPanel = new JPanel(new BorderLayout(10, 10));
        entregasPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Entregas do Fornecedor", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        JPanel entregasHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblFornecedorSelecionado = new JLabel("Fornecedor: Nenhum selecionado");
        lblFornecedorSelecionado.setFont(new Font("Arial", Font.BOLD, 12));
        entregasHeaderPanel.add(lblFornecedorSelecionado);
        entregasPanel.add(entregasHeaderPanel, BorderLayout.NORTH);

        // Colunas para a tabela de entregas, conforme o FornecedorController
        entregasTableModel = new DefaultTableModel(new Object[]{"ID Entrega", "Fornecedor ID", "Nº Pedido Compra", "Data Prevista", "Data Realização", "Status", "Qtde Itens", "Avaliação", "Comentário"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        entregasTable = new JTable(entregasTableModel);
        entregasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane entregasScrollPane = new JScrollPane(entregasTable);
        entregasPanel.add(entregasScrollPane, BorderLayout.CENTER);

        JPanel entregasButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnRegistrarEntrega = new JButton("Registrar Nova Entrega");
        btnRegistrarRecebimento = new JButton("Registrar Recebimento");
        btnAvaliarEntrega = new JButton("Avaliar Entrega");
        btnVoltarFornecedores = new JButton("Voltar aos Fornecedores");

        entregasButtonPanel.add(btnRegistrarEntrega);
        entregasButtonPanel.add(btnRegistrarRecebimento);
        entregasButtonPanel.add(btnAvaliarEntrega);
        entregasButtonPanel.add(btnVoltarFornecedores);

        btnRegistrarEntrega.setEnabled(false); // Desabilita inicialmente, habilitado ao selecionar fornecedor
        btnRegistrarRecebimento.setEnabled(false); // Desabilita até que uma entrega seja selecionada
        btnAvaliarEntrega.setEnabled(false); // Desabilita até que uma entrega seja selecionada

        entregasPanel.add(entregasButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Entregas", entregasPanel);

        // Define a aba de fornecedores como a aba inicial
        tabbedPane.setSelectedIndex(0);
        // Desabilita a aba de entregas inicialmente, será habilitada quando um fornecedor for selecionado e o botão "Ver Entregas" for clicado
        tabbedPane.setEnabledAt(1, false);
    }

    // --- Getters para componentes do Painel de Fornecedores ---
    public JTable getFornecedoresTable() { return fornecedoresTable; }
    // O FornecedorController irá chamar setFornecedoresTableModel para atribuir o modelo customizado.
    // getFornecedoresTableModel pode ser usado para obter o modelo atual (que pode ser o customizado).
    public DefaultTableModel getFornecedoresTableModel() { return (DefaultTableModel) fornecedoresTable.getModel(); }

    // NOVO MÉTODO: Setter para o DefaultTableModel (necessário para o FornecedorController)
    public void setFornecedoresTableModel(DefaultTableModel model) {
        this.fornecedoresTable.setModel(model);
        // Ocultar novamente as colunas de endereço após o modelo ser atualizado
        this.fornecedoresTable.getColumnModel().getColumn(5).setMinWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(5).setMaxWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(5).setWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(6).setMinWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(6).setMaxWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(6).setWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(7).setMinWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(7).setMaxWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(7).setWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(8).setMinWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(8).setMaxWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(8).setWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(9).setMinWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(9).setMaxWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(9).setWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(10).setMinWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(10).setMaxWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(10).setWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(11).setMinWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(11).setMaxWidth(0);
        this.fornecedoresTable.getColumnModel().getColumn(11).setWidth(0);
    }


    public JButton getBtnNovoFornecedor() { return btnNovoFornecedor; }
    public JButton getBtnEditarFornecedor() { return btnEditarFornecedor; }
    public JButton getBtnInativarFornecedor() { return btnInativarFornecedor; }
    public JButton getBtnAtualizarFornecedores() { return btnAtualizarFornecedores; }
    public JButton getBtnVerEntregas() { return btnVerEntregas; }

    // --- Getters para componentes do Painel de Entregas ---
    public JTable getEntregasTable() { return entregasTable; }
    public DefaultTableModel getEntregasTableModel() { return entregasTableModel; }
    public JButton getBtnRegistrarEntrega() { return btnRegistrarEntrega; }
    public JButton getBtnRegistrarRecebimento() { return btnRegistrarRecebimento; }
    public JButton getBtnAvaliarEntrega() { return btnAvaliarEntrega; }
    public JButton getBtnVoltarFornecedores() { return btnVoltarFornecedores; }
    public JLabel getLblFornecedorSelecionado() { return lblFornecedorSelecionado; }

    public JTabbedPane getTabbedPane() { return tabbedPane; }

    // Métodos para limpar tabelas
    public void clearFornecedoresTable() { fornecedoresTableModel.setRowCount(0); }
    public void clearEntregasTable() { entregasTableModel.setRowCount(0); }


    /**
     * Exibe um diálogo para cadastro ou edição de fornecedor.
     * @param isNew true para novo, false para edição.
     * @param id ID do fornecedor (para edição).
     * @param nome Nome do fornecedor.
     * @param cnpj CNPJ do fornecedor.
     * @param emailContato Email de contato.
     * @param telefoneContato Telefone de contato.
     * @param logradouro Logradouro do endereço.
     * @param numero Número do endereço.
     * @param complemento Complemento do endereço.
     * @param bairro Bairro do endereço.
     * @param cidade Cidade do endereço.
     * @param estado Estado do endereço.
     * @param cep CEP do endereço.
     * @param ativo Status de ativo/inativo.
     * @return array de String com os dados ou null se cancelar.
     * [id, nome, cnpj, emailContato, telefoneContato, logradouro, numero, complemento, bairro, cidade, estado, cep, ativo(boolean como string)]
     */
    public String[] showFornecedorDialog(boolean isNew, String id, String nome, String cnpj,
                                         String emailContato, String telefoneContato,
                                         String logradouro, String numero, String complemento,
                                         String bairro, String cidade, String estado, String cep,
                                         boolean ativo) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtId = new JTextField(id);
        txtId.setEditable(false);

        JTextField txtNome = new JTextField(nome, 30);
        JFormattedTextField txtCnpj;
        JTextField txtEmailContato = new JTextField(emailContato, 25);
        JFormattedTextField txtTelefoneContato;

        JTextField txtLogradouro = new JTextField(logradouro, 25);
        JTextField txtNumero = new JTextField(numero, 10);
        JTextField txtComplemento = new JTextField(complemento, 15);
        JTextField txtBairro = new JTextField(bairro, 20);
        JTextField txtCidade = new JTextField(cidade, 20);
        JTextField txtEstado = new JTextField(estado, 5);
        JFormattedTextField txtCep;

        JCheckBox chkAtivo = new JCheckBox("Ativo", ativo);

        try {
            MaskFormatter cnpjMask = new MaskFormatter("##.###.###/####-##");
            cnpjMask.setPlaceholderCharacter('_');
            txtCnpj = new JFormattedTextField(cnpjMask);
            txtCnpj.setText(cnpj);

            MaskFormatter phoneMask = new MaskFormatter("(##)#####-####");
            phoneMask.setPlaceholderCharacter('_');
            txtTelefoneContato = new JFormattedTextField(phoneMask);
            txtTelefoneContato.setText(telefoneContato);

            MaskFormatter cepMask = new MaskFormatter("#####-###");
            cepMask.setPlaceholderCharacter('_');
            txtCep = new JFormattedTextField(cepMask);
            txtCep.setText(cep);

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Erro ao criar máscaras de formato: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            txtCnpj = new JFormattedTextField(cnpj);
            txtTelefoneContato = new JFormattedTextField(telefoneContato);
            txtCep = new JFormattedTextField(cep);
        }

        int y = 0;
        if (!isNew) {
            gbc.gridx = 0; gbc.gridy = y; panel.add(new JLabel("ID:"), gbc);
            gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtId, gbc);
            y++;
        }

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtNome, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("CNPJ:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtCnpj, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("Email Contato:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtEmailContato, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("Telefone Contato:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtTelefoneContato, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("Logradouro:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtLogradouro, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("Número:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtNumero, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("Complemento:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtComplemento, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("Bairro:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtBairro, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("Cidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtCidade, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("Estado (UF):"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtEstado, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; panel.add(new JLabel("CEP:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtCep, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST; panel.add(chkAtivo, gbc);
        y++;

        int result = JOptionPane.showConfirmDialog(this, panel,
                (isNew ? "Cadastrar Novo Fornecedor" : "Editar Fornecedor"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String idToReturn = isNew ? null : txtId.getText().trim();
            // Remova caracteres de máscara antes de retornar
            String cleanedCnpj = txtCnpj.getText().replaceAll("[^0-9]", "");
            String cleanedTelefone = txtTelefoneContato.getText().replaceAll("[^0-9]", "");
            String cleanedCep = txtCep.getText().replaceAll("[^0-9]", "");

            return new String[]{
                    idToReturn,
                    txtNome.getText().trim(),
                    cleanedCnpj,
                    txtEmailContato.getText().trim(),
                    cleanedTelefone,
                    txtLogradouro.getText().trim(),
                    txtNumero.getText().trim(),
                    txtComplemento.getText().trim(),
                    txtBairro.getText().trim(),
                    txtCidade.getText().trim(),
                    txtEstado.getText().trim(),
                    cleanedCep,
                    String.valueOf(chkAtivo.isSelected())
            };
        }
        return null;
    }

    /**
     * Exibe um diálogo para registrar uma nova entrega.
     * Agora coleta: numeroPedidoCompra, dataPrevistaEntrega, quantidadeItens, observacoes.
     * @param fornecedorId O ID do fornecedor ao qual a entrega pertence.
     * @return array de String com os dados da entrega ou null se cancelar.
     * [numeroPedidoCompra, dataPrevistaEntrega (AAAA-MM-DD), quantidadeItens, observacoes]
     */
    public String[] showRegistrarEntregaDialog(String fornecedorId) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNumeroPedidoCompra = new JTextField(15);
        JTextField txtDataPrevistaEntrega = new JTextField(10);
        JTextField txtQuantidadeItens = new JTextField(5);
        JTextArea txtObservacoes = new JTextArea(3, 20);
        txtObservacoes.setLineWrap(true);
        txtObservacoes.setWrapStyleWord(true);
        JScrollPane scrollObservacoes = new JScrollPane(txtObservacoes);

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; panel.add(new JLabel("Fornecedor ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; JTextField lblFornecedorId = new JTextField(fornecedorId); lblFornecedorId.setEditable(false); panel.add(lblFornecedorId, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; panel.add(new JLabel("Nº Pedido Compra:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtNumeroPedidoCompra, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; panel.add(new JLabel("Data Prevista Entrega (AAAA-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtDataPrevistaEntrega, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; panel.add(new JLabel("Quantidade Itens:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtQuantidadeItens, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; panel.add(new JLabel("Observações:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH; panel.add(scrollObservacoes, gbc);
        y++;

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Registrar Nova Entrega", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return new String[]{
                    txtNumeroPedidoCompra.getText().trim(),
                    txtDataPrevistaEntrega.getText().trim(),
                    txtQuantidadeItens.getText().trim(),
                    txtObservacoes.getText().trim()
            };
        }
        return null;
    }

    /**
     * Exibe um diálogo para registrar o recebimento de uma entrega.
     * @param entregaId ID da entrega.
     * @return Data de recebimento (yyyy-MM-dd) ou null se cancelar.
     */
    public String showRegistrarRecebimentoDialog(String entregaId) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtDataRecebimento = new JTextField(10); // Formato YYYY-MM-DD

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; panel.add(new JLabel("Entrega ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; JTextField lblEntregaId = new JTextField(entregaId); lblEntregaId.setEditable(false); panel.add(lblEntregaId, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; panel.add(new JLabel("Data Recebimento (AAAA-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(txtDataRecebimento, gbc);
        y++;

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Registrar Recebimento de Entrega", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return txtDataRecebimento.getText().trim();
        }
        return null;
    }

    /**
     * Exibe um diálogo para avaliar uma entrega.
     * @param entregaId ID da entrega.
     * @param avaliacaoExistente Avaliação atual (1-5), se houver.
     * @param observacoesExistente Observações atuais, se houver.
     * @return array de String com os dados ou null se cancelar.
     * [avaliacao (1-5), observacoes]
     */
    public String[] showAvaliarEntregaDialog(String entregaId, Integer avaliacaoExistente, String observacoesExistente) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] ratings = {"1", "2", "3", "4", "5"};
        JComboBox<String> cmbAvaliacao = new JComboBox<>(ratings);
        if (avaliacaoExistente != null) {
            cmbAvaliacao.setSelectedItem(String.valueOf(avaliacaoExistente));
        }

        JTextArea txtObservacoes = new JTextArea(3, 20);
        txtObservacoes.setLineWrap(true);
        txtObservacoes.setWrapStyleWord(true);
        txtObservacoes.setText(observacoesExistente != null ? observacoesExistente : "");
        JScrollPane scrollObservacoes = new JScrollPane(txtObservacoes);

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; panel.add(new JLabel("Entrega ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; JTextField lblEntregaId = new JTextField(entregaId); lblEntregaId.setEditable(false); panel.add(lblEntregaId, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; panel.add(new JLabel("Avaliação (1-5):"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; panel.add(cmbAvaliacao, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; panel.add(new JLabel("Observações:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH; panel.add(scrollObservacoes, gbc);
        y++;

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Avaliar Entrega", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return new String[]{
                    (String) cmbAvaliacao.getSelectedItem(),
                    txtObservacoes.getText().trim()
            };
        }
        return null;
    }
}