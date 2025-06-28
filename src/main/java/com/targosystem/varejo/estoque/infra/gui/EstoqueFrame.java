package com.targosystem.varejo.estoque.infra.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate; // Para trabalhar com datas
import java.time.format.DateTimeParseException; // Para parse de datas
import java.util.List;
import java.util.Objects;
import com.targosystem.varejo.estoque.domain.model.TipoMovimentacao;
import com.targosystem.varejo.estoque.application.output.LocalEstoqueOutput;
import com.targosystem.varejo.estoque.application.output.ItemEstoqueOutput;

public class EstoqueFrame extends JPanel { // Agora estende JPanel

    // --- Componentes para Consulta ---
    private JTextField txtProdutoIdConsulta;
    private JButton btnConsultarEstoque;
    private JTextArea txtAreaResultadoConsulta;

    // --- Componentes para Movimentação ---
    private JComboBox<TipoMovimentacao> cmbTipoMovimentacao;
    private JButton btnRegistrarMovimentacao;

    // --- Componentes para LocalEstoque Comboboxes e Motivo Geral ---
    private JLabel lblLocalOrigemMovimentacao;
    private JComboBox<LocalEstoqueOutput> cmbLocalOrigemMovimentacao;
    private JLabel lblLocalDestinoMovimentacao;
    private JComboBox<LocalEstoqueOutput> cmbLocalDestinoMovimentacao;
    private JLabel lblMotivoMovimentacao;
    private JTextField txtMotivoMovimentacao;

    // --- Botão para Adicionar Produtos na Movimentação ---
    private JButton btnAdicionarProdutoMovimentacao;

    // --- Tabela para exibir itens adicionados para movimentação ---
    private JTable tblItensMovimentacao;
    private DefaultTableModel modelItensMovimentacao;
    private JButton btnRemoverItemMovimentacao;

    // --- Componentes antigos de Lote, Localização e Transferência (agora obsoletos no layout principal) ---
    // Mantidos como variáveis de instância (sempre invisíveis) para evitar NPE se um getter for chamado por engano,
    // mas NÃO serão adicionados ao layout principal da movimentação.
    // Eles seriam usados apenas dentro do modal de seleção de produto, se necessário.
    private JLabel lblNumeroLote; // Agora interno ao ProductSelectionDialog
    private JTextField txtNumeroLote; // Agora interno ao ProductSelectionDialog
    private JLabel lblDataFabricacaoLote; // Agora interno ao ProductSelectionDialog
    private JFormattedTextField txtDataFabricacaoLote; // Agora interno ao ProductSelectionDialog
    private JLabel lblDataValidadeLote; // Agora interno ao ProductSelectionDialog
    private JFormattedTextField txtDataValidadeLote; // Agora interno ao ProductSelectionDialog

    private JLabel lblLocalizacaoEntrada; // Agora interno ao ProductSelectionDialog
    private JLabel lblCorredorEntrada; // Agora interno ao ProductSelectionDialog
    private JTextField txtCorredorEntrada; // Agora interno ao ProductSelectionDialog
    private JLabel lblPrateleiraEntrada; // Agora interno ao ProductSelectionDialog
    private JTextField txtPrateleiraEntrada; // Agora interno ao ProductSelectionDialog
    private JLabel lblNivelEntrada; // Agora interno ao ProductSelectionDialog
    private JTextField txtNivelEntrada; // Agora interno ao ProductSelectionDialog

    // Campos de transferência antigos que não são mais usados ou exibidos
    private JLabel lblLocalOrigemOld;
    private JTextField txtLocalOrigemOld;
    private JLabel lblLocalDestinoOld;
    private JLabel lblCorredorDestino;
    private JTextField txtCorredorDestino;
    private JLabel lblPrateleiraDestino;
    private JTextField txtPrateleiraDestino;
    private JLabel lblNivelDestino;
    private JTextField txtNivelDestino;

    // --- Componentes para as Abas de Estoque por Local ---
    private JTabbedPane tabbedPaneEstoques;

    public EstoqueFrame() {
        setLayout(new BorderLayout());

        // --- Painel Superior para Consulta ---
        JPanel panelConsulta = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelConsulta.setBorder(BorderFactory.createTitledBorder("Consultar Estoque por Produto"));
        txtProdutoIdConsulta = new JTextField(20);
        btnConsultarEstoque = new JButton("Consultar");
        txtAreaResultadoConsulta = new JTextArea(5, 40);
        txtAreaResultadoConsulta.setEditable(false);
        JScrollPane scrollPaneConsulta = new JScrollPane(txtAreaResultadoConsulta);

        panelConsulta.add(new JLabel("ID do Produto:"));
        panelConsulta.add(txtProdutoIdConsulta);
        panelConsulta.add(btnConsultarEstoque);
        panelConsulta.add(scrollPaneConsulta);

        // --- Painel Central para Movimentação ---
        JPanel panelMovimentacao = new JPanel(new GridBagLayout());
        panelMovimentacao.setBorder(BorderFactory.createTitledBorder("Registrar Movimentação de Estoque"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0: Tipo de Movimentação e Motivo (lado a lado)
        gbc.gridx = 0; gbc.gridy = 0; panelMovimentacao.add(new JLabel("Tipo de Movimentação:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        cmbTipoMovimentacao = new JComboBox<>(TipoMovimentacao.values());
        cmbTipoMovimentacao.setSelectedItem(TipoMovimentacao.ENTRADA);
        panelMovimentacao.add(cmbTipoMovimentacao, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; lblMotivoMovimentacao = new JLabel("Motivo:"); panelMovimentacao.add(lblMotivoMovimentacao, gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0; txtMotivoMovimentacao = new JTextField(30); panelMovimentacao.add(txtMotivoMovimentacao, gbc);
        gbc.gridwidth = 1;

        // Linha 1: Botão "Selecionar Produto(s)"
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 5;
        btnAdicionarProdutoMovimentacao = new JButton("Adicionar Produto à Movimentação");
        panelMovimentacao.add(btnAdicionarProdutoMovimentacao, gbc);
        gbc.gridwidth = 1;

        // Tabela de itens da movimentação (AGORA COM COLUNAS ADICIONAIS PARA LOTE E LOCALIZAÇÃO)
        String[] columnNames = {"Produto ID", "Nome Produto", "Quantidade", "Nº Lote", "Data Fab.", "Data Val.", "Corredor", "Prateleira", "Nível"};
        modelItensMovimentacao = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblItensMovimentacao = new JTable(modelItensMovimentacao);
        tblItensMovimentacao.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite selecionar uma linha
        JScrollPane scrollPaneItensMovimentacao = new JScrollPane(tblItensMovimentacao);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 5; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.5;
        panelMovimentacao.add(scrollPaneItensMovimentacao, gbc);
        gbc.gridwidth = 1;

        // Botão Remover Item
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 5; gbc.fill = GridBagConstraints.HORIZONTAL;
        btnRemoverItemMovimentacao = new JButton("Remover Item Selecionado");
        panelMovimentacao.add(btnRemoverItemMovimentacao, gbc);
        gbc.gridwidth = 1;

        // Linha 4: Local de Origem (ComboBox)
        gbc.gridx = 0; gbc.gridy = 4; lblLocalOrigemMovimentacao = new JLabel("Local Origem:"); panelMovimentacao.add(lblLocalOrigemMovimentacao, gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 4; cmbLocalOrigemMovimentacao = new JComboBox<>();
        cmbLocalOrigemMovimentacao.setRenderer(new LocalEstoqueOutputRenderer());
        panelMovimentacao.add(cmbLocalOrigemMovimentacao, gbc);
        gbc.gridwidth = 1;

        // Linha 5: Local de Destino (ComboBox)
        gbc.gridx = 0; gbc.gridy = 5; lblLocalDestinoMovimentacao = new JLabel("Local Destino:"); panelMovimentacao.add(lblLocalDestinoMovimentacao, gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 4; cmbLocalDestinoMovimentacao = new JComboBox<>();
        cmbLocalDestinoMovimentacao.setRenderer(new LocalEstoqueOutputRenderer());
        panelMovimentacao.add(cmbLocalDestinoMovimentacao, gbc);
        gbc.gridwidth = 1;

        // Botão Registrar Movimentação
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 5; gbc.anchor = GridBagConstraints.CENTER;
        btnRegistrarMovimentacao = new JButton("Registrar Movimentação");
        panelMovimentacao.add(btnRegistrarMovimentacao, gbc);


        // --- JTabbedPane principal para organizar as abas ---
        JTabbedPane mainTabbedPane = new JTabbedPane();
        mainTabbedPane.addTab("Consulta de Estoque", panelConsulta);
        mainTabbedPane.addTab("Movimentação de Estoque", panelMovimentacao);

        // --- Painel Inferior para Abas de Estoque por Local (agora uma aba do mainTabbedPane) ---
        tabbedPaneEstoques = new JTabbedPane();
        mainTabbedPane.addTab("Estoques por Local", tabbedPaneEstoques);

        add(mainTabbedPane, BorderLayout.CENTER);


        // Altura preferencial para os painéis
        panelMovimentacao.setPreferredSize(new Dimension(800, 450)); // Ajustada para o novo layout

        // Inicializar os componentes antigos (que agora serão usados no ProductSelectionDialog ou são obsoletos no main layout)
        // Apenas para evitar NullPointerExceptions nos getters, se ainda forem chamados por alguma razão.
        // E para serem usados dentro do ProductSelectionDialog (se você mover a lógica para lá).
        try {
            lblNumeroLote = new JLabel("Nº Lote:"); txtNumeroLote = new JTextField(15);
            lblDataFabricacaoLote = new JLabel("Data Fab. (AAAA-MM-DD):"); txtDataFabricacaoLote = new JFormattedTextField(new MaskFormatter("####-##-##")); txtDataFabricacaoLote.setColumns(10);
            lblDataValidadeLote = new JLabel("Data Val. (AAAA-MM-DD):"); txtDataValidadeLote = new JFormattedTextField(new MaskFormatter("####-##-##")); txtDataValidadeLote.setColumns(10);
            lblLocalizacaoEntrada = new JLabel("Localização Detalhada:");
            lblCorredorEntrada = new JLabel("Corredor:"); txtCorredorEntrada = new JTextField(10);
            lblPrateleiraEntrada = new JLabel("Prateleira:"); txtPrateleiraEntrada = new JTextField(10);
            lblNivelEntrada = new JLabel("Nível:"); txtNivelEntrada = new JTextField(10);
        } catch (ParseException e) {
            System.err.println("Erro ao criar MaskFormatter para datas: " + e.getMessage());
            // Fallback para JTextField simples em caso de erro
            txtDataFabricacaoLote = new JFormattedTextField();
            txtDataValidadeLote = new JFormattedTextField();
        }

        lblLocalOrigemOld = new JLabel(""); txtLocalOrigemOld = new JTextField(0);
        lblLocalDestinoOld = new JLabel("");
        lblCorredorDestino = new JLabel(""); txtCorredorDestino = new JTextField(0);
        lblPrateleiraDestino = new JLabel(""); txtPrateleiraDestino = new JTextField(0);
        lblNivelDestino = new JLabel(""); txtNivelDestino = new JTextField(0);
    }

    // --- Custom Renderer para LocalEstoqueOutput ---
    private static class LocalEstoqueOutputRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof LocalEstoqueOutput) {
                setText(((LocalEstoqueOutput) value).nome());
            } else if (value == null) {
                setText("Selecione...");
            } else {
                setText(value.toString());
            }
            return this;
        }
    }

    // --- Getters para Componentes de Consulta ---
    public JTextField getTxtProdutoIdConsulta() { return txtProdutoIdConsulta; }
    public JButton getBtnConsultarEstoque() { return btnConsultarEstoque; }
    public JTextArea getTxtAreaResultadoConsulta() { return txtAreaResultadoConsulta; }

    // --- Getters para Componentes de Movimentação ---
    public JComboBox<TipoMovimentacao> getCmbTipoMovimentacao() { return cmbTipoMovimentacao; }
    public JButton getBtnRegistrarMovimentacao() { return btnRegistrarMovimentacao; }

    // --- Getters para os ComboBoxes de LocalEstoque e Motivo ---
    public JLabel getLblLocalOrigemMovimentacao() { return lblLocalOrigemMovimentacao; }
    public JComboBox<LocalEstoqueOutput> getCmbLocalOrigemMovimentacao() { return cmbLocalOrigemMovimentacao; }
    public JLabel getLblLocalDestinoMovimentacao() { return lblLocalDestinoMovimentacao; }
    public JComboBox<LocalEstoqueOutput> getCmbLocalDestinoMovimentacao() { return cmbLocalDestinoMovimentacao; }
    public JLabel getLblMotivoMovimentacao() { return lblMotivoMovimentacao; }
    public JTextField getTxtMotivoMovimentacao() { return txtMotivoMovimentacao; }

    // --- Getters para o botão de adicionar produto e tabela de itens ---
    public JButton getBtnAdicionarProdutoMovimentacao() { return btnAdicionarProdutoMovimentacao; }
    public DefaultTableModel getModelItensMovimentacao() { return modelItensMovimentacao; }
    public JTable getTblItensMovimentacao() { return tblItensMovimentacao; }
    public JButton getBtnRemoverItemMovimentacao() { return btnRemoverItemMovimentacao; }

    // --- Getters para o JTabbedPane ---
    public JTabbedPane getTabbedPaneEstoques() { return tabbedPaneEstoques; }

    // --- Getters para componentes que agora podem estar no modal (apenas para consistência) ---
    public JTextField getTxtNumeroLote() { return txtNumeroLote; }
    public JFormattedTextField getTxtDataFabricacaoLote() { return txtDataFabricacaoLote; }
    public JFormattedTextField getTxtDataValidadeLote() { return txtDataValidadeLote; }
    public JTextField getTxtCorredorEntrada() { return txtCorredorEntrada; }
    public JTextField getTxtPrateleiraEntrada() { return txtPrateleiraEntrada; }
    public JTextField getTxtNivelEntrada() { return txtNivelEntrada; }

    // --- Getters para componentes antigos de Lote (continuam invisíveis, apenas para evitar NPE nos getters) ---
    public JLabel getLblNumeroLote() { return lblNumeroLote; }
    public JLabel getLblDataFabricacaoLote() { return lblDataFabricacaoLote; }
    public JLabel getLblDataValidadeLote() { return lblDataValidadeLote; }

    // --- Getters para componentes de Localização de Entrada (continuam invisíveis, apenas para evitar NPE nos getters) ---
    public JLabel getLblLocalizacaoEntrada() { return lblLocalizacaoEntrada; }
    public JLabel getLblCorredorEntrada() { return lblCorredorEntrada; }
    public JLabel getLblPrateleiraEntrada() { return lblPrateleiraEntrada; }
    public JLabel getLblNivelEntrada() { return lblNivelEntrada; }

    // --- Getters para componentes antigos de Transferência (continuam invisíveis, apenas para evitar NPE nos getters) ---
    public JLabel getLblLocalOrigemOld() { return lblLocalOrigemOld; }
    public JTextField getTxtLocalOrigemOld() { return txtLocalOrigemOld; }
    public JLabel getLblLocalDestinoOld() { return lblLocalDestinoOld; }
    public JLabel getLblCorredorDestino() { return lblCorredorDestino; }
    public JTextField getTxtCorredorDestino() { return txtCorredorDestino; }
    public JTextField getLblPrateleiraDestino() { return txtPrateleiraDestino; }
    public JTextField getTxtNivelDestino() { return txtNivelDestino; }

    // --- Método para limpar campos de movimentação ---
    public void clearMovimentacaoFields() {
        txtMotivoMovimentacao.setText("");
        modelItensMovimentacao.setRowCount(0); // Limpa a tabela de itens
        // Os campos de lote e localização detalhada não estão mais aqui para serem limpos diretamente.
    }

    /**
     * Atualiza a visibilidade dos campos de movimentação
     * com base no tipo de movimentação selecionado.
     * OBS: Campos de lote e localização detalhada AGORA são controlados dentro do ProductSelectionDialog.
     * @param tipoMovimentacao O tipo de movimentação atual.
     */
    public void updateMovimentacaoFieldsVisibility(TipoMovimentacao tipoMovimentacao) {
        lblLocalOrigemMovimentacao.setVisible(true);
        cmbLocalOrigemMovimentacao.setVisible(true);
        lblLocalDestinoMovimentacao.setVisible(true);
        cmbLocalDestinoMovimentacao.setVisible(true);
        lblMotivoMovimentacao.setVisible(true);
        txtMotivoMovimentacao.setVisible(true);

        btnAdicionarProdutoMovimentacao.setVisible(true);
        getTblItensMovimentacao().setVisible(true);
        if (getTblItensMovimentacao().getParent() != null && getTblItensMovimentacao().getParent().getParent() instanceof JScrollPane) {
            getTblItensMovimentacao().getParent().getParent().setVisible(true);
        }
        btnRemoverItemMovimentacao.setVisible(true);

        // Adaptação dos rótulos de origem e destino
        if (tipoMovimentacao == null) {
            lblLocalOrigemMovimentacao.setText("Local Origem:");
            lblLocalDestinoMovimentacao.setText("Local Destino:");
            return;
        }

        switch (tipoMovimentacao) {
            case ENTRADA:
                lblLocalOrigemMovimentacao.setText("Fornecedor (Origem):");
                lblLocalDestinoMovimentacao.setText("Depósito (Destino):");
                break;
            case SAIDA:
                lblLocalOrigemMovimentacao.setText("Depósito (Origem):");
                lblLocalDestinoMovimentacao.setText("Cliente (Destino):");
                break;
            case TRANSFERENCIA:
                lblLocalOrigemMovimentacao.setText("Depósito Origem:");
                lblLocalDestinoMovimentacao.setText("Depósito Destino:");
                break;
        }

        revalidate();
        repaint();
    }

    /**
     * Popula um JComboBox com uma lista de LocalEstoqueOutput.
     * @param comboBox O JComboBox a ser populado.
     * @param locais A lista de LocalEstoqueOutput.
     */
    public void popularComboBoxLocalEstoque(JComboBox<LocalEstoqueOutput> comboBox, List<LocalEstoqueOutput> locais) {
        comboBox.removeAllItems();
        if (locais != null && !locais.isEmpty()) {
            for (LocalEstoqueOutput local : locais) {
                comboBox.addItem(local);
            }
        } else {
            comboBox.addItem(new LocalEstoqueOutput("", "Nenhum Local Disponível", null, false, null, null));
            comboBox.setSelectedItem(null);
        }
    }

    /**
     * Adiciona um item à tabela de itens da movimentação.
     * AGORA RECEBE UM OBJETO ProductInputData COMPLETO.
     * @param productData Dados completos do produto a ser adicionado.
     */
    public void adicionarItemMovimentacao(ProductInputData productData) {
        modelItensMovimentacao.addRow(new Object[]{
                productData.produtoId,
                productData.produtoNome,
                productData.quantidade,
                productData.numeroLote,
                productData.dataFabricacaoLote != null ? productData.dataFabricacaoLote.toString() : "",
                productData.dataValidadeLote != null ? productData.dataValidadeLote.toString() : "",
                productData.corredor,
                productData.prateleira,
                productData.nivel
        });
    }

    /**
     * Limpa as abas de estoque existentes e adiciona novas.
     * @param locaisInternos Lista de LocalEstoqueOutput do tipo INTERNO.
     * @param itensPorLocal Mapa onde a chave é o localEstoqueId e o valor é uma lista de ItemEstoqueOutput.
     */
    public void atualizarAbasDeEstoque(List<LocalEstoqueOutput> locaisInternos, java.util.Map<String, List<ItemEstoqueOutput>> itensPorLocal) {
        tabbedPaneEstoques.removeAll();

        if (locaisInternos.isEmpty()) {
            tabbedPaneEstoques.addTab("Estoque", new JLabel("Nenhum local de estoque interno encontrado.", SwingConstants.CENTER));
            return;
        }

        for (LocalEstoqueOutput local : locaisInternos) {
            JPanel panelLocalEstoque = new JPanel(new BorderLayout());
            String[] columnNames = {"Produto ID", "Nome Produto", "Nº Série/Lote", "Quantidade", "Data Fab.", "Data Val.", "Corredor", "Prateleira", "Nível"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            panelLocalEstoque.add(scrollPane, BorderLayout.CENTER);

            List<ItemEstoqueOutput> itens = itensPorLocal.getOrDefault(local.id(), java.util.Collections.emptyList());
            for (ItemEstoqueOutput item : itens) {
                String produtoNome = "Produto " + item.produtoId();
                tableModel.addRow(new Object[]{
                        item.produtoId(),
                        produtoNome,
                        item.numeroLote() != null ? item.numeroLote() : "N/A",
                        item.quantidade(),
                        item.dataFabricacaoLote() != null ? item.dataFabricacaoLote().toString() : "N/A",
                        item.dataValidadeLote() != null ? item.dataValidadeLote().toString() : "N/A",
                        item.corredorLocalizacao() != null ? item.corredorLocalizacao() : "N/A",
                        item.prateleiraLocalizacao() != null ? item.prateleiraLocalizacao() : "N/A",
                        item.nivelLocalizacao() != null ? item.nivelLocalizacao() : "N/A"
                });
            }
            tabbedPaneEstoques.addTab(local.nome(), panelLocalEstoque);
        }
    }

    /**
     * Classe para encapsular os dados de entrada de um produto
     * para adicionar à movimentação, incluindo detalhes de lote e localização.
     */
    public static class ProductInputData {
        public final String produtoId;
        public final String produtoNome;
        public final int quantidade;
        public final String numeroLote;
        public final LocalDate dataFabricacaoLote;
        public final LocalDate dataValidadeLote;
        public final String corredor;
        public final String prateleira;
        public final String nivel;

        public ProductInputData(String produtoId, String produtoNome, int quantidade,
                                String numeroLote, LocalDate dataFabricacaoLote, LocalDate dataValidadeLote,
                                String corredor, String prateleira, String nivel) {
            this.produtoId = Objects.requireNonNull(produtoId, "Produto ID cannot be null.");
            this.produtoNome = Objects.requireNonNull(produtoNome, "Produto Name cannot be null.");
            if (quantidade <= 0) {
                throw new IllegalArgumentException("Quantity must be positive.");
            }
            this.quantidade = quantidade;
            this.numeroLote = numeroLote;
            this.dataFabricacaoLote = dataFabricacaoLote;
            this.dataValidadeLote = dataValidadeLote;
            this.corredor = corredor;
            this.prateleira = prateleira;
            this.nivel = nivel;
        }
    }

    /**
     * Exibe um JDialog para o usuário selecionar um produto e inserir seus detalhes
     * de quantidade, lote e localização.
     *
     * @param parentFrame O JFrame pai para centralizar o diálogo.
     * @param tipoMovimentacao O tipo de movimentação atual para adaptar a UI (e.g., mostrar lote/localização apenas para ENTRADA).
     * @param initialProductId O ID do produto pré-selecionado no modal anterior.
     * @param initialProductName O nome do produto pré-selecionado no modal anterior.
     * @param initialQuantity A quantidade pré-selecionada no modal anterior.
     * @return Um objeto ProductInputData com os detalhes inseridos, ou null se o diálogo for cancelado.
     */
    public ProductInputData showProductSelectionDialog(Frame parentFrame, TipoMovimentacao tipoMovimentacao,
                                                       String initialProductId, String initialProductName, int initialQuantity) {
        JDialog dialog = new JDialog(parentFrame, "Detalhes do Produto para Movimentação", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(450, 450); // Tamanho ajustado para mais campos
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(parentFrame); // Centraliza no frame pai

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtProdutoId = new JTextField(initialProductId);
        txtProdutoId.setEditable(false); // ID não editável, vem da seleção anterior
        JTextField txtProdutoNome = new JTextField(initialProductName);
        txtProdutoNome.setEditable(false); // Nome não editável, vem da seleção anterior
        JSpinner spQuantidade = new JSpinner(new SpinnerNumberModel(initialQuantity, 1, 99999, 1));
        spQuantidade.setEnabled(false); // Quantidade não editável, vem da seleção anterior

        JTextField txtNumeroLoteInput = new JTextField(15);
        JFormattedTextField txtDataFabInput = null;
        JFormattedTextField txtDataValInput = null;
        JTextField txtCorredorInput = new JTextField(10);
        JTextField txtPrateleiraInput = new JTextField(10);
        JTextField txtNivelInput = new JTextField(10);

        // Configurar máscaras para datas
        try {
            txtDataFabInput = new JFormattedTextField(new MaskFormatter("####-##-##"));
            txtDataFabInput.setColumns(10);
            txtDataValInput = new JFormattedTextField(new MaskFormatter("####-##-##"));
            txtDataValInput.setColumns(10);
        } catch (ParseException e) {
            System.err.println("Erro ao criar MaskFormatter no dialog: " + e.getMessage());
            txtDataFabInput = new JFormattedTextField();
            txtDataValInput = new JFormattedTextField();
        }

        // Adicionar componentes ao diálogo
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("ID do Produto:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; dialog.add(txtProdutoId, gbc);

        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Nome do Produto:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; dialog.add(txtProdutoNome, gbc);

        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; dialog.add(spQuantidade, gbc);

        // Campos de Lote e Localização (visíveis apenas para ENTRADA)
        boolean showLoteAndLocation = (tipoMovimentacao == TipoMovimentacao.ENTRADA);

        gbc.gridx = 0; gbc.gridy = row; JLabel lblLoteNum = new JLabel("Nº do Lote:"); lblLoteNum.setVisible(showLoteAndLocation); dialog.add(lblLoteNum, gbc);
        gbc.gridx = 1; gbc.gridy = row++; txtNumeroLoteInput.setVisible(showLoteAndLocation); dialog.add(txtNumeroLoteInput, gbc);

        gbc.gridx = 0; gbc.gridy = row; JLabel lblDataFab = new JLabel("Data Fab. (AAAA-MM-DD):"); lblDataFab.setVisible(showLoteAndLocation); dialog.add(lblDataFab, gbc);
        gbc.gridx = 1; gbc.gridy = row++; txtDataFabInput.setVisible(showLoteAndLocation); dialog.add(txtDataFabInput, gbc);

        gbc.gridx = 0; gbc.gridy = row; JLabel lblDataVal = new JLabel("Data Val. (AAAA-MM-DD):"); lblDataVal.setVisible(showLoteAndLocation); dialog.add(lblDataVal, gbc);
        gbc.gridx = 1; gbc.gridy = row++; txtDataValInput.setVisible(showLoteAndLocation); dialog.add(txtDataValInput, gbc);

        // Separador para localização
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; JSeparator separator = new JSeparator(); separator.setVisible(showLoteAndLocation); dialog.add(separator, gbc); gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; JLabel lblLocDetalhada = new JLabel("Localização Detalhada (Opcional):"); lblLocDetalhada.setVisible(showLoteAndLocation); dialog.add(lblLocDetalhada, gbc); gbc.gridwidth = 1;


        gbc.gridx = 0; gbc.gridy = row; JLabel lblCorredor = new JLabel("Corredor:"); lblCorredor.setVisible(showLoteAndLocation); dialog.add(lblCorredor, gbc);
        gbc.gridx = 1; gbc.gridy = row++; txtCorredorInput.setVisible(showLoteAndLocation); dialog.add(txtCorredorInput, gbc);

        gbc.gridx = 0; gbc.gridy = row; JLabel lblPrateleira = new JLabel("Prateleira:"); lblPrateleira.setVisible(showLoteAndLocation); dialog.add(lblPrateleira, gbc);
        gbc.gridx = 1; gbc.gridy = row++; txtPrateleiraInput.setVisible(showLoteAndLocation); dialog.add(txtPrateleiraInput, gbc);

        gbc.gridx = 0; gbc.gridy = row; JLabel lblNivel = new JLabel("Nível:"); lblNivel.setVisible(showLoteAndLocation); dialog.add(lblNivel, gbc);
        gbc.gridx = 1; gbc.gridy = row++; txtNivelInput.setVisible(showLoteAndLocation); dialog.add(txtNivelInput, gbc);


        ProductInputData[] result = new ProductInputData[1]; // Usado para retornar o resultado

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("Confirmar"); // Alterado para "Confirmar"
        JButton btnCancel = new JButton("Cancelar");

        JFormattedTextField finalTxtDataFabInput = txtDataFabInput;
        JFormattedTextField finalTxtDataValInput = txtDataValInput;
        btnOk.addActionListener(e -> {
            try {
                // ProdutoId, Nome e Quantidade já vêm preenchidos e não são editáveis aqui
                String produtoId = txtProdutoId.getText().trim();
                String produtoNome = txtProdutoNome.getText().trim();
                int quantidade = (int) spQuantidade.getValue();

                // Validação mínima, embora os campos já venham preenchidos e não editáveis
                if (produtoId.isEmpty() || produtoNome.isEmpty() || quantidade <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Erro: Informações de produto inválidas.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String numeroLote = showLoteAndLocation ? txtNumeroLoteInput.getText().trim() : "";
                LocalDate dataFabricacao = null;
                LocalDate dataValidade = null;

                if (showLoteAndLocation) {
                    String fabDateStr = finalTxtDataFabInput.getText().trim().replace(" ", "");
                    String valDateStr = finalTxtDataValInput.getText().trim().replace(" ", "");

                    // Validação de formato de data e conversão
                    if (!fabDateStr.isEmpty()) {
                        try {
                            dataFabricacao = LocalDate.parse(fabDateStr);
                        } catch (DateTimeParseException ex) {
                            JOptionPane.showMessageDialog(dialog, "Formato de Data de Fabricação inválido. Use AAAA-MM-DD.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    if (!valDateStr.isEmpty()) {
                        try {
                            dataValidade = LocalDate.parse(valDateStr);
                        } catch (DateTimeParseException ex) {
                            JOptionPane.showMessageDialog(dialog, "Formato de Data de Validade inválido. Use AAAA-MM-DD.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }

                String corredor = showLoteAndLocation ? txtCorredorInput.getText().trim() : "";
                String prateleira = showLoteAndLocation ? txtPrateleiraInput.getText().trim() : "";
                String nivel = showLoteAndLocation ? txtNivelInput.getText().trim() : "";

                result[0] = new ProductInputData(
                        produtoId, produtoNome, quantidade,
                        numeroLote, dataFabricacao, dataValidade,
                        corredor, prateleira, nivel
                );
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao adicionar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> {
            result[0] = null; // Indica cancelamento
            dialog.dispose();
        });

        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setVisible(true);

        return result[0];
    }

    // --- Método main para testar a UI do EstoqueFrame (apenas para teste visual) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Teste EstoqueFrame (JPanel)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            EstoqueFrame estoqueFrame = new EstoqueFrame();
            frame.add(estoqueFrame);

            // Simular uma chamada para popular os comboboxes (para teste visual)
            estoqueFrame.popularComboBoxLocalEstoque(estoqueFrame.getCmbLocalOrigemMovimentacao(),
                    List.of(new LocalEstoqueOutput("F001", "Fornecedor Alpha", null, false, null, null),
                            new LocalEstoqueOutput("F002", "Fornecedor Beta", null, false, null, null)));
            estoqueFrame.popularComboBoxLocalEstoque(estoqueFrame.getCmbLocalDestinoMovimentacao(),
                    List.of(new LocalEstoqueOutput("D001", "Depósito Principal", null, false, null, null),
                            new LocalEstoqueOutput("D002", "Depósito Secundário", null, false, null, null)));

            // Simular atualização de visibilidade (inicia com ENTRADA)
            estoqueFrame.updateMovimentacaoFieldsVisibility(TipoMovimentacao.ENTRADA);

            // Adicionar ActionListener ao botão "Adicionar Produto" para testar o modal
            estoqueFrame.getBtnAdicionarProdutoMovimentacao().addActionListener(e -> {
                // Simulação de SelectedProductInfo vindo do SelectProductModal
                String dummyProductId = "PROD123";
                String dummyProductName = "Produto Teste XYZ";
                int dummyQuantity = 5;

                // Agora passamos esses dados para o showProductSelectionDialog
                ProductInputData data = estoqueFrame.showProductSelectionDialog(frame,
                        estoqueFrame.getCmbTipoMovimentacao().getItemAt(estoqueFrame.getCmbTipoMovimentacao().getSelectedIndex()),
                        dummyProductId, dummyProductName, dummyQuantity);
                if (data != null) {
                    estoqueFrame.adicionarItemMovimentacao(data);
                    JOptionPane.showMessageDialog(frame, "Produto adicionado à tabela:\n" +
                            "ID: " + data.produtoId + "\n" +
                            "Nome: " + data.produtoNome + "\n" +
                            "Qtd: " + data.quantidade + "\n" +
                            "Lote: " + data.numeroLote + "\n" +
                            "Fab: " + data.dataFabricacaoLote + "\n" +
                            "Val: " + data.dataValidadeLote + "\n" +
                            "Loc: " + data.corredor + "/" + data.prateleira + "/" + data.nivel, "Produto Adicionado", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Seleção de produto cancelada.", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
                }
            });


            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}