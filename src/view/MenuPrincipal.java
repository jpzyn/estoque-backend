package view;

import controller.EstoqueController;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

public class MenuPrincipal extends JFrame {

    private final EstoqueController controller;

    public MenuPrincipal() {
        super("Sistema de Controle de Estoque - Cliente");
        this.controller = new EstoqueController();
        configurarJanela();
        adicionarComponentes();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);
    }

    private void adicionarComponentes() {
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Produtos", criarPainelProdutos());
        abas.addTab("Categorias", criarPainelCategorias());
        abas.addTab("Movimentações", criarPainelMovimentacoes());
        abas.addTab("Relatórios", criarPainelRelatorios());
        
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.add(abas, BorderLayout.CENTER);
        
        add(painelPrincipal);
    }

    private JPanel criarPainelProdutos() {
        JTextField campoNome = new JTextField();
        JComboBox<String> comboCategoria = new JComboBox<>();
        JTextField campoEstoqueInicial = new JTextField();
        JTextField campoEstoqueMinimo = new JTextField();
        JTextField campoEstoqueMaximo = new JTextField();
        JTextField campoPreco = new JTextField();
        JComboBox<String> comboUnidade = new JComboBox<>(new String[]{"Kilos", "Gramas", "Litros", "Mililitros"});
        JTextArea areaResultado = criarAreaResultado();

        atualizarCategorias(comboCategoria);

        JButton botaoCadastrar = new JButton("Cadastrar Produto");
        botaoCadastrar.addActionListener(e -> {
            try {
                String categoriaSelecionada = (String) comboCategoria.getSelectedItem();
                if (categoriaSelecionada == null || categoriaSelecionada.isEmpty() || 
                    categoriaSelecionada.equals("(Nenhuma categoria cadastrada)") ||
                    categoriaSelecionada.equals("(Erro ao carregar categorias)")) {
                    JOptionPane.showMessageDialog(this, "Selecione uma categoria válida. Cadastre uma categoria primeiro na aba 'Categorias'.", "Dados inválidos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String unidadeSelecionada = (String) comboUnidade.getSelectedItem();
                String resposta = controller.cadastrarProduto(
                        campoNome.getText(),
                        categoriaSelecionada,
                        Integer.parseInt(campoEstoqueInicial.getText()),
                        Integer.parseInt(campoEstoqueMinimo.getText()),
                        Integer.parseInt(campoEstoqueMaximo.getText()),
                        Double.parseDouble(campoPreco.getText()),
                        unidadeSelecionada != null ? unidadeSelecionada : "Unidade"
                );
                areaResultado.setText(resposta);

                atualizarCategorias(comboCategoria);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Estoque atual, estoque mínimo, estoque máximo e preço devem ser numéricos.", "Dados inválidos", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton botaoListar = new JButton("Listar Produtos");
        botaoListar.addActionListener(e -> atualizarAreaComLista(areaResultado, controller.listarProdutos()));


        JButton botaoAtualizarCategorias = new JButton("🔄");
        botaoAtualizarCategorias.setPreferredSize(new Dimension(30, 25));
        botaoAtualizarCategorias.setMaximumSize(new Dimension(30, 25));
        botaoAtualizarCategorias.setMinimumSize(new Dimension(30, 25));
        botaoAtualizarCategorias.setToolTipText("Atualizar categorias");
        botaoAtualizarCategorias.addActionListener(e -> atualizarCategorias(comboCategoria));


        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Nome"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(campoNome, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Categoria"), gbc);


        JPanel painelCategoria = new JPanel(new BorderLayout(5, 0));
        painelCategoria.add(comboCategoria, BorderLayout.CENTER);
        painelCategoria.add(botaoAtualizarCategorias, BorderLayout.EAST);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(painelCategoria, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Estoque atual"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(campoEstoqueInicial, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Estoque mínimo"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(campoEstoqueMinimo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Estoque máximo"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(campoEstoqueMaximo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Preço"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(campoPreco, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Unidade de medida"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(comboUnidade, gbc);
        formulario.setBorder(BorderFactory.createTitledBorder("Dados"));

        JPanel botoes = criarPainelBotoes(botaoCadastrar, botaoListar);

        return montarPainelCompleto(formulario, botoes, areaResultado);
    }

    private JPanel criarPainelCategorias() {
        JPanel painel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Categorias", JLabel.CENTER);
        label.setFont(label.getFont().deriveFont(18f));
        painel.add(label, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelMovimentacoes() {
        JPanel painel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Movimentações", JLabel.CENTER);
        label.setFont(label.getFont().deriveFont(18f));
        painel.add(label, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelRelatorios() {
        JPanel painel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Relatórios", JLabel.CENTER);
        label.setFont(label.getFont().deriveFont(18f));
        painel.add(label, BorderLayout.CENTER);
        return painel;
    }


    private JPanel criarPainelBotoes(JButton... botoes) {
        JPanel painel = new JPanel(new GridLayout(1, botoes.length, 4, 4));
        for (JButton botao : botoes) {
            botao.setPreferredSize(new Dimension(120, 25));
            botao.setMaximumSize(new Dimension(120, 25));
            botao.setMinimumSize(new Dimension(120, 25));
            painel.add(botao);
        }
        painel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return painel;
    }

    private JPanel montarPainelCompleto(JPanel formulario, JPanel botoes, JTextArea areaResultado) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(formulario, BorderLayout.NORTH);
        painel.add(botoes, BorderLayout.CENTER);
        JScrollPane scrollLog = new JScrollPane(areaResultado);
        scrollLog.setPreferredSize(new Dimension(0, 200)); // Altura mínima de 200px
        scrollLog.setMinimumSize(new Dimension(0, 150));
        painel.add(scrollLog, BorderLayout.SOUTH);
        return painel;
    }

    private JTextArea criarAreaResultado() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createTitledBorder("Server Log"));
        return area;
    }


    private void atualizarAreaComLista(JTextArea area, List<String> linhas) {
        if (linhas == null || linhas.isEmpty()) {
            area.setText("Nenhum dado retornado.");
        } else {
            area.setText(String.join(System.lineSeparator(), linhas));
        }
    }

    private void atualizarCategorias(JComboBox<String> combo) {
        combo.removeAllItems();
        try {
            List<String> categorias = controller.listarCategorias();
            
            if (categorias != null && !categorias.isEmpty()) {
                for (String linha : categorias) {
                    if (linha != null && !linha.trim().isEmpty() && !linha.trim().startsWith("ERROR")) {


                        String nomeCategoria = linha.split(" - ")[0].trim();
                        if (!nomeCategoria.isEmpty() && !nomeCategoria.startsWith("ERROR")) {
                            combo.addItem(nomeCategoria);
                        }
                    }
                }
            }
            

            if (combo.getItemCount() == 0) {
                combo.addItem("(Nenhuma categoria cadastrada)");
            }
        } catch (Exception e) {
            combo.addItem("(Erro ao carregar categorias)");
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}

