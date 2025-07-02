package com.targosystem.varejo.promocoes.infra.gui;

import com.targosystem.varejo.promocoes.application.input.CriarPromocaoInput;
import com.targosystem.varejo.promocoes.application.input.AtualizarPromocaoInput;
import com.targosystem.varejo.promocoes.application.output.PromocaoOutput;
import com.targosystem.varejo.promocoes.domain.model.TipoDesconto;
import com.targosystem.varejo.produtos.application.output.ProdutoOutput;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PromocaoDialog extends JDialog {

    private JTextField idField;
    private JTextField nomeField;
    private JComboBox<String> tipoDescontoComboBox;
    private JTextField valorDescontoField;
    private JFormattedTextField dataInicioField;
    private JFormattedTextField dataFimField;
    private JCheckBox ativaCheckBox;
    private JButton saveButton;
    private JButton cancelButton;

    private JList<ProdutoOutput> produtosDisponiveisList;
    private DefaultListModel<ProdutoOutput> produtosDisponiveisModel;
    private JList<ProdutoOutput> produtosSelecionadosList;
    private DefaultListModel<ProdutoOutput> produtosSelecionadosModel;
    private JButton addProdutoButton;
    private JButton removeProdutoButton;

    private boolean saved = false;
    private String originalPromocaoId = null;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public PromocaoDialog(Frame owner, List<ProdutoOutput> todosProdutosDisponiveis) {
        super(owner, "Nova Promoção", true);
        setupUI(owner, todosProdutosDisponiveis);
        idField.setText("<Gerado Automaticamente>");
        ativaCheckBox.setSelected(true);
    }

    public PromocaoDialog(Frame owner, PromocaoOutput promocao, List<ProdutoOutput> todosProdutosDisponiveis) {
        super(owner, "Editar Promoção", true);
        this.originalPromocaoId = promocao.id();
        setupUI(owner, todosProdutosDisponiveis);
        loadPromocaoData(promocao);
    }

    private void setupUI(Frame owner, List<ProdutoOutput> todosProdutosDisponiveis) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JPanel basicInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints basicGbc = new GridBagConstraints();
        basicGbc.insets = new Insets(5, 5, 5, 5);
        basicGbc.anchor = GridBagConstraints.WEST;

        basicGbc.gridx = 0; basicGbc.gridy = 0; basicInfoPanel.add(new JLabel("ID:"), basicGbc);
        basicGbc.gridx = 1; basicGbc.fill = GridBagConstraints.HORIZONTAL; basicGbc.weightx = 1.0;
        idField = new JTextField(20);
        idField.setEditable(false);
        basicInfoPanel.add(idField, basicGbc);

        basicGbc.gridx = 0; basicGbc.gridy = 1; basicInfoPanel.add(new JLabel("Nome:"), basicGbc);
        basicGbc.gridx = 1;
        nomeField = new JTextField(20);
        basicInfoPanel.add(nomeField, basicGbc);

        basicGbc.gridx = 0; basicGbc.gridy = 2; basicInfoPanel.add(new JLabel("Tipo Desconto:"), basicGbc);
        basicGbc.gridx = 1;
        tipoDescontoComboBox = new JComboBox<>(new String[]{
                TipoDesconto.PERCENTUAL.name(),
                TipoDesconto.VALOR_FIXO.name()
        });
        basicInfoPanel.add(tipoDescontoComboBox, basicGbc);

        basicGbc.gridx = 0; basicGbc.gridy = 3; basicInfoPanel.add(new JLabel("Valor/Percentual:"), basicGbc);
        basicGbc.gridx = 1;
        valorDescontoField = new JTextField(10);
        basicInfoPanel.add(valorDescontoField, basicGbc);

        basicGbc.gridx = 0; basicGbc.gridy = 4; basicInfoPanel.add(new JLabel("Data Início (dd/MM/yyyy HH:mm):"), basicGbc);
        basicGbc.gridx = 1;
        dataInicioField = new JFormattedTextField(new DateFormatter(SIMPLE_DATE_FORMAT));
        dataInicioField.setColumns(15);
        dataInicioField.setValue(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        basicInfoPanel.add(dataInicioField, basicGbc);

        basicGbc.gridx = 0; basicGbc.gridy = 5; basicInfoPanel.add(new JLabel("Data Fim (dd/MM/yyyy HH:mm):"), basicGbc);
        basicGbc.gridx = 1;
        dataFimField = new JFormattedTextField(new DateFormatter(SIMPLE_DATE_FORMAT));
        dataFimField.setColumns(15);
        dataFimField.setValue(Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant()));
        basicInfoPanel.add(dataFimField, basicGbc);

        basicGbc.gridx = 0; basicGbc.gridy = 6;
        ativaCheckBox = new JCheckBox("Ativa");
        basicGbc.gridwidth = 2; basicGbc.anchor = GridBagConstraints.CENTER;
        basicInfoPanel.add(ativaCheckBox, basicGbc);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 0.5; gbc.weighty = 1.0;
        add(basicInfoPanel, gbc);

        JPanel produtosSelectionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints prodSelGbc = new GridBagConstraints();
        prodSelGbc.insets = new Insets(5, 5, 5, 5);
        prodSelGbc.fill = GridBagConstraints.BOTH;

        prodSelGbc.gridx = 0; prodSelGbc.gridy = 0; prodSelGbc.weightx = 0.5;
        produtosSelectionPanel.add(new JLabel("Produtos Disponíveis:"), prodSelGbc);

        prodSelGbc.gridx = 2; prodSelGbc.gridy = 0; prodSelGbc.weightx = 0.5;
        produtosSelectionPanel.add(new JLabel("Produtos na Promoção:"), prodSelGbc);

        prodSelGbc.gridx = 0; prodSelGbc.gridy = 1; prodSelGbc.weighty = 1.0;
        produtosDisponiveisModel = new DefaultListModel<>();
        produtosDisponiveisList = new JList<>(produtosDisponiveisModel);
        // Renderer para exibir ProdutoOutput de forma amigável
        produtosDisponiveisList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProdutoOutput produto) {
                    setText(produto.nome() + " (ID: " + produto.id() + ")");
                }
                return this;
            }
        });
        produtosDisponiveisList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        produtosSelectionPanel.add(new JScrollPane(produtosDisponiveisList), prodSelGbc);

        prodSelGbc.gridx = 1; prodSelGbc.gridy = 1; prodSelGbc.weightx = 0; prodSelGbc.fill = GridBagConstraints.NONE;
        JPanel transferButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints transferGbc = new GridBagConstraints();
        transferGbc.insets = new Insets(5, 2, 5, 2);
        transferGbc.gridx = 0; transferGbc.gridy = 0;
        addProdutoButton = new JButton(">");
        transferButtonPanel.add(addProdutoButton, transferGbc);
        transferGbc.gridy = 1;
        removeProdutoButton = new JButton("<");
        transferButtonPanel.add(removeProdutoButton, transferGbc);
        produtosSelectionPanel.add(transferButtonPanel, prodSelGbc);

        prodSelGbc.gridx = 2; prodSelGbc.gridy = 1; prodSelGbc.weighty = 1.0; prodSelGbc.fill = GridBagConstraints.BOTH;
        produtosSelecionadosModel = new DefaultListModel<>();
        produtosSelecionadosList = new JList<>(produtosSelecionadosModel);

        produtosSelecionadosList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProdutoOutput produto) {
                    setText(produto.nome() + " (ID: " + produto.id() + ")");
                }
                return this;
            }
        });
        produtosSelecionadosList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        produtosSelectionPanel.add(new JScrollPane(produtosSelecionadosList), prodSelGbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 0.5; gbc.weighty = 1.0;
        add(produtosSelectionPanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Salvar");
        cancelButton = new JButton("Cancelar");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST;
        add(buttonPanel, gbc);

        setProdutosDisponiveis(todosProdutosDisponiveis);

        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> onCancel());
        addProdutoButton.addActionListener(e -> addSelectedProducts());
        removeProdutoButton.addActionListener(e -> removeSelectedProducts());

        pack();
        setLocationRelativeTo(owner);
    }

    private void loadPromocaoData(PromocaoOutput promocao) {
        idField.setText(promocao.id());
        nomeField.setText(promocao.nome());
        tipoDescontoComboBox.setSelectedItem(promocao.tipoDesconto().name());
        valorDescontoField.setText(promocao.valorDesconto().toPlainString());
        ativaCheckBox.setSelected(promocao.ativa());

        try {
            dataInicioField.setValue(Date.from(promocao.dataInicio().atZone(ZoneId.systemDefault()).toInstant()));
            dataFimField.setValue(Date.from(promocao.dataFim().atZone(ZoneId.systemDefault()).toInstant()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar datas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }


        List<ProdutoOutput> produtosDisponiveisListTemp = new ArrayList<>();
        for (int i = 0; i < produtosDisponiveisModel.size(); i++) {
            produtosDisponiveisListTemp.add(produtosDisponiveisModel.getElementAt(i));
        }

        List<ProdutoOutput> produtosNaPromocao = produtosDisponiveisListTemp.stream()
                .filter(p -> promocao.produtoIds().contains(p.id()))
                .collect(Collectors.toList());
        setSelectedProdutos(produtosNaPromocao);
    }

    private void onSave() {
        if (nomeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome da promoção não pode ser vazio.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            new BigDecimal(valorDescontoField.getText().replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor/Percentual de desconto inválido. Use um número válido.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date dataInicioParsed = SIMPLE_DATE_FORMAT.parse(dataInicioField.getText());
            Date dataFimParsed = SIMPLE_DATE_FORMAT.parse(dataFimField.getText());

            if (dataInicioParsed.after(dataFimParsed)) {
                JOptionPane.showMessageDialog(this, "Data de início não pode ser posterior à data de fim.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data/hora inválido. Use dd/MM/yyyy HH:mm", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Se o tipo de desconto for percentual, valida se o valor está entre 0 e 1
        TipoDesconto tipoSelecionado = TipoDesconto.valueOf(((String) tipoDescontoComboBox.getSelectedItem()).toUpperCase());
        BigDecimal valorDesconto = new BigDecimal(valorDescontoField.getText().replace(",", "."));
        if (tipoSelecionado == TipoDesconto.PERCENTUAL && (valorDesconto.compareTo(BigDecimal.ZERO) < 0 || valorDesconto.compareTo(BigDecimal.ONE) > 0)) {
            JOptionPane.showMessageDialog(this, "Para tipo PERCENTUAL, o valor deve ser entre 0 e 1 (ex: 0.1 para 10%).", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }


        saved = true;
        setVisible(false);
        dispose();
    }

    private void onCancel() {
        saved = false;
        setVisible(false);
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public void setProdutosDisponiveis(List<ProdutoOutput> produtos) {
        produtosDisponiveisModel.clear();
        for (ProdutoOutput p : produtos) {
            produtosDisponiveisModel.addElement(p);
        }
    }

    public void setSelectedProdutos(List<ProdutoOutput> produtos) {
        produtosSelecionadosModel.clear();
        for (ProdutoOutput p : produtos) {
            produtosSelecionadosModel.addElement(p);
        }
    }

    private void addSelectedProducts() {
        List<ProdutoOutput> selected = produtosDisponiveisList.getSelectedValuesList();
        for (ProdutoOutput p : selected) {
            boolean alreadyAdded = false;
            for (int i = 0; i < produtosSelecionadosModel.size(); i++) {
                if (produtosSelecionadosModel.getElementAt(i).id().equals(p.id())) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (!alreadyAdded) {
                produtosSelecionadosModel.addElement(p);
            }
        }
    }

    private void removeSelectedProducts() {
        List<ProdutoOutput> selected = produtosSelecionadosList.getSelectedValuesList();
        for (ProdutoOutput p : selected) {
            produtosSelecionadosModel.removeElement(p);
        }
    }

    public List<String> getSelectedProdutoIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < produtosSelecionadosModel.size(); i++) {
            ids.add(produtosSelecionadosModel.getElementAt(i).id());
        }
        return ids;
    }

    public CriarPromocaoInput getCriarPromocaoInput() {
        String nome = nomeField.getText();
        TipoDesconto tipoDesconto = TipoDesconto.valueOf(((String) tipoDescontoComboBox.getSelectedItem()).toUpperCase());
        BigDecimal valorDesconto = new BigDecimal(valorDescontoField.getText().replace(",", "."));

        LocalDateTime dataInicio = ((Date) dataInicioField.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime dataFim = ((Date) dataFimField.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        List<String> produtoIds = getSelectedProdutoIds();

        return new CriarPromocaoInput(nome, tipoDesconto, valorDesconto, dataInicio, dataFim, produtoIds);
    }

    public AtualizarPromocaoInput getAtualizarPromocaoInput() {
        Objects.requireNonNull(originalPromocaoId, "ID da promoção original não pode ser nulo para atualização.");

        String nome = nomeField.getText();
        TipoDesconto tipoDesconto = TipoDesconto.valueOf(((String) tipoDescontoComboBox.getSelectedItem()).toUpperCase());
        BigDecimal valorDesconto = new BigDecimal(valorDescontoField.getText().replace(",", "."));

        LocalDateTime dataInicio = ((Date) dataInicioField.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime dataFim = ((Date) dataFimField.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        List<String> produtoIds = getSelectedProdutoIds();

        return new AtualizarPromocaoInput(
                originalPromocaoId,
                Optional.ofNullable(nome),
                Optional.ofNullable(tipoDesconto),
                Optional.ofNullable(valorDesconto),
                Optional.ofNullable(dataInicio),
                Optional.ofNullable(dataFim),
                Optional.of(ativaCheckBox.isSelected()),
                Optional.of(produtoIds)
        );
    }
}