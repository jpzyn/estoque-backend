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
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.util.ArrayList;
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

    private String converterNomeRelatorioParaBackend(String nomeRelatorio) {
        switch (nomeRelatorio) {
            case "Lista de Preços":
                return "lista_precos";
            case "Balanço Físico/Financeiro":
                return "balanco_fisico_financeiro";
            case "Produtos Abaixo do Mínimo":
                return "produtos_abaixo_minimo";
            case "Quantidade por Categoria":
                return "quantidade_por_categoria";
            case "Produto Mais Movimentado":
                return "produto_maior_movimentacao";
            case "Todos os Itens em Estoque":
                return "todos_itens_estoque";
            default:
                return nomeRelatorio.toLowerCase().replace(" ", "_");
        }
    }

    private void atualizarTabelaRelatorio(DefaultTableModel modelo, String tipoRelatorio, String resultado) {
        modelo.setRowCount(0);
        modelo.setColumnCount(0);
        
        if (resultado == null || resultado.trim().isEmpty() || resultado.startsWith("ERROR") || resultado.startsWith("Falha")) {
            modelo.setColumnIdentifiers(new Object[]{"Mensagem"});
            modelo.addRow(new Object[]{resultado != null ? resultado : "Nenhum dado disponível"});
            return;
        }
        
        String[] linhas = resultado.split("\\r?\\n");
        
        switch (tipoRelatorio) {
            case "lista_precos":
                parsearListaPrecos(modelo, linhas);
                break;
            case "balanco_fisico_financeiro":
                parsearBalancoFisicoFinanceiro(modelo, linhas);
                break;
            case "produtos_abaixo_minimo":
                parsearProdutosAbaixoMinimo(modelo, linhas);
                break;
            case "quantidade_por_categoria":
                parsearQuantidadePorCategoria(modelo, linhas);
                break;
            case "produto_maior_movimentacao":
                parsearProdutoMaisMovimentacoes(modelo, linhas);
                break;
            case "todos_itens_estoque":
                parsearTodosItensEstoque(modelo, linhas);
                break;
            default:
                modelo.setColumnIdentifiers(new Object[]{"Dados"});
                for (String linha : linhas) {
                    if (!linha.trim().isEmpty() && !linha.trim().startsWith("===") && !linha.trim().startsWith("-")) {
                        modelo.addRow(new Object[]{linha.trim()});
                    }
                }
        }
    }

    private void parsearListaPrecos(DefaultTableModel modelo, String[] linhas) {
        modelo.setColumnIdentifiers(new Object[]{"Produto", "Preço", "Unidade", "Categoria"});
        
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO")) {
                continue;
            }
            

            String[] partes = linha.split("\\|");
            if (partes.length >= 4) {
                String produto = partes[0].trim();
                String preco = partes[1].trim();
                String unidade = partes[2].trim();
                String categoria = partes[3].trim();
                modelo.addRow(new Object[]{produto, preco, unidade, categoria});
            }
        }
    }

    private void parsearBalancoFisicoFinanceiro(DefaultTableModel modelo, String[] linhas) {
        modelo.setColumnIdentifiers(new Object[]{"Produto", "Quantidade", "Valor Unitário", "Valor Total"});
        
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO") || linha.trim().startsWith("TOTAL")) {
                continue;
            }
            

            String[] partes = linha.split("\\|");
            if (partes.length >= 4) {
                String produto = partes[0].trim();
                String quantidade = partes[1].trim();
                String valorUnitario = partes[2].trim();
                String valorTotal = partes[3].trim();
                modelo.addRow(new Object[]{produto, quantidade, valorUnitario, valorTotal});
            }
        }
        

        for (String linha : linhas) {
            if (linha.trim().startsWith("TOTAL")) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 4) {
                    modelo.addRow(new Object[]{"TOTAL GERAL", "", "", partes[3].trim()});
                }
                break;
            }
        }
    }

    private void parsearProdutosAbaixoMinimo(DefaultTableModel modelo, String[] linhas) {
        modelo.setColumnIdentifiers(new Object[]{"Produto", "Estoque", "Mínimo"});
        
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO") || linha.trim().startsWith("✓")) {
                continue;
            }
            

            String[] partes = linha.split("\\|");
            if (partes.length >= 3) {
                String produto = partes[0].trim();
                String estoque = partes[1].trim();
                String minimo = partes[2].trim();
                modelo.addRow(new Object[]{produto, estoque, minimo});
            }
        }
    }

    private void parsearQuantidadePorCategoria(DefaultTableModel modelo, String[] linhas) {
        modelo.setColumnIdentifiers(new Object[]{"Categoria", "Qtd Produtos Distintos"});
        
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("CATEGORIA")) {
                continue;
            }
            

            String[] partes = linha.split("\\|");
            if (partes.length >= 2) {
                String categoria = partes[0].trim();
                String qtdProdutos = partes[1].trim();
                modelo.addRow(new Object[]{categoria, qtdProdutos});
            }
        }
    }

    private void atualizarGraficoRelatorio(GraficoPanel grafico, String tipoRelatorio, String resultado) {
        if (resultado == null || resultado.trim().isEmpty() || resultado.startsWith("ERROR") || resultado.startsWith("Falha")) {
            grafico.setDados(new ArrayList<>());
            return;
        }

        String[] linhas = resultado.split("\\r?\\n");
        List<GraficoPanel.DadoGrafico> dadosGrafico = new ArrayList<>();

        switch (tipoRelatorio) {
            case "lista_precos":
                dadosGrafico = extrairDadosListaPrecos(linhas);
                grafico.setTitulo("Lista de Preços");
                grafico.setTipoGrafico("barras");
                break;
            case "balanco_fisico_financeiro":
                dadosGrafico = extrairDadosBalanco(linhas);
                grafico.setTitulo("Balanço Físico/Financeiro");
                grafico.setTipoGrafico("barras");
                break;
            case "produtos_abaixo_minimo":
                dadosGrafico = extrairDadosAbaixoMinimo(linhas);
                grafico.setTitulo("Produtos Abaixo do Mínimo");
                grafico.setTipoGrafico("barras");
                break;
            case "quantidade_por_categoria":
                dadosGrafico = extrairDadosQuantidadeCategoria(linhas);
                grafico.setTitulo("Quantidade por Categoria");
                grafico.setTipoGrafico("pizza");

                grafico.setPreferredSize(new Dimension(grafico.getWidth() > 0 ? grafico.getWidth() : 800, 400));
                grafico.setMinimumSize(new Dimension(0, 400));
                break;
            case "produto_maior_movimentacao":

                grafico.setDados(new ArrayList<>());
                return;
            case "todos_itens_estoque":
                dadosGrafico = extrairDadosTodosItensEstoque(linhas);
                grafico.setTitulo("Todos os Itens em Estoque");
                grafico.setTipoGrafico("barras");
                break;
        }

        grafico.setDados(dadosGrafico);
    }

    private List<GraficoPanel.DadoGrafico> extrairDadosListaPrecos(String[] linhas) {
        List<GraficoPanel.DadoGrafico> dados = new ArrayList<>();
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO")) {
                continue;
            }
            String[] partes = linha.split("\\|");
            if (partes.length >= 2) {
                String produto = partes[0].trim();
                String precoStr = partes[1].replace("R$", "").trim();
                try {
                    double preco = Double.parseDouble(precoStr);
                    dados.add(new GraficoPanel.DadoGrafico(produto, preco));
                } catch (NumberFormatException e) {

                }
            }
        }
        return dados;
    }

    private List<GraficoPanel.DadoGrafico> extrairDadosBalanco(String[] linhas) {
        List<GraficoPanel.DadoGrafico> dados = new ArrayList<>();
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO") || linha.trim().startsWith("TOTAL")) {
                continue;
            }
            String[] partes = linha.split("\\|");
            if (partes.length >= 4) {
                String produto = partes[0].trim();
                String valorTotalStr = partes[3].replace("R$", "").trim();
                try {
                    double valorTotal = Double.parseDouble(valorTotalStr);
                    dados.add(new GraficoPanel.DadoGrafico(produto, valorTotal));
                } catch (NumberFormatException e) {

                }
            }
        }
        return dados;
    }

    private List<GraficoPanel.DadoGrafico> extrairDadosAbaixoMinimo(String[] linhas) {
        List<GraficoPanel.DadoGrafico> dados = new ArrayList<>();
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO") || linha.trim().startsWith("✓")) {
                continue;
            }
            String[] partes = linha.split("\\|");
            if (partes.length >= 3) {
                String produto = partes[0].trim();
                try {
                    int estoque = Integer.parseInt(partes[1].trim());
                    int minimo = Integer.parseInt(partes[2].trim());

                    dados.add(new GraficoPanel.DadoGrafico(produto, minimo - estoque));
                } catch (NumberFormatException e) {

                }
            }
        }
        return dados;
    }

    private List<GraficoPanel.DadoGrafico> extrairDadosQuantidadeCategoria(String[] linhas) {
        List<GraficoPanel.DadoGrafico> dados = new ArrayList<>();
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("CATEGORIA")) {
                continue;
            }
            String[] partes = linha.split("\\|");
            if (partes.length >= 2) {
                String categoria = partes[0].trim();
                try {
                    int qtdProdutos = Integer.parseInt(partes[1].trim());
                    dados.add(new GraficoPanel.DadoGrafico(categoria, qtdProdutos));
                } catch (NumberFormatException e) {

                }
            }
        }
        return dados;
    }

    private List<GraficoPanel.DadoGrafico> extrairDadosMaisMovimentacoes(String[] linhas) {
        List<GraficoPanel.DadoGrafico> dados = new ArrayList<>();
        String produtoMaisEntrada = null;
        String produtoMaisSaida = null;

        for (String linha : linhas) {
            if (linha.contains("Mais Entradas")) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 2) {
                    produtoMaisEntrada = partes[1].trim();

                    if (!produtoMaisEntrada.equals("N/A")) {
                        dados.add(new GraficoPanel.DadoGrafico(produtoMaisEntrada + " (Entradas)", 1));
                    }
                }
            } else if (linha.contains("Mais Saídas")) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 2) {
                    produtoMaisSaida = partes[1].trim();

                    if (!produtoMaisSaida.equals("N/A")) {
                        dados.add(new GraficoPanel.DadoGrafico(produtoMaisSaida + " (Saídas)", 1));
                    }
                }
            }
        }

        return dados;
    }

    private List<GraficoPanel.DadoGrafico> extrairDadosTodosItensEstoque(String[] linhas) {
        List<GraficoPanel.DadoGrafico> dados = new ArrayList<>();
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO")) {
                continue;
            }
            String[] partes = linha.split("\\|");
            if (partes.length >= 4) {
                String produto = partes[0].trim();
                String estoqueStr = partes[3].trim();
                try {
                    int estoque = Integer.parseInt(estoqueStr);
                    dados.add(new GraficoPanel.DadoGrafico(produto, estoque));
                } catch (NumberFormatException e) {

                }
            }
        }
        return dados;
    }

    private void parsearTodosItensEstoque(DefaultTableModel modelo, String[] linhas) {
        modelo.setColumnIdentifiers(new Object[]{"Produto", "Categoria", "Preço", "Estoque", "Mínimo", "Máximo", "Unidade"});
        
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO")) {
                continue;
            }
            

            String[] partes = linha.split("\\|");
            if (partes.length >= 7) {
                String produto = partes[0].trim();
                String categoria = partes[1].trim();
                String preco = partes[2].trim();
                String estoque = partes[3].trim();
                String minimo = partes[4].trim();
                String maximo = partes[5].trim();
                String unidade = partes[6].trim();
                modelo.addRow(new Object[]{produto, categoria, preco, estoque, minimo, maximo, unidade});
            }
        }
    }

    private void parsearProdutoMaisMovimentacoes(DefaultTableModel modelo, String[] linhas) {
        modelo.setColumnIdentifiers(new Object[]{"Tipo", "Produto"});
        
        String produtoMaisEntrada = "N/A";
        String produtoMaisSaida = "N/A";
        
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("TIPO")) {
                continue;
            }
            
            if (linha.contains("Mais Entradas")) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 2) {
                    produtoMaisEntrada = partes[1].trim();
                }
            } else if (linha.contains("Mais Saídas")) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 2) {
                    produtoMaisSaida = partes[1].trim();
                }
            }
        }
        
        if (!produtoMaisEntrada.equals("N/A")) {
            modelo.addRow(new Object[]{"Mais Entradas", produtoMaisEntrada});
        }
        if (!produtoMaisSaida.equals("N/A")) {
            modelo.addRow(new Object[]{"Mais Saídas", produtoMaisSaida});
        }
    }

    private JPanel criarFormularioPadrao(String[] labels, JTextField[] campos) {
        JPanel formulario = new JPanel(new GridLayout(labels.length, 2, 8, 8));
        for (int i = 0; i < labels.length; i++) {
            formulario.add(new JLabel(labels[i]));
            formulario.add(campos[i]);
        }
        formulario.setBorder(BorderFactory.createTitledBorder("Dados"));
        return formulario;
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

    private JFormattedTextField criarCampoData() {
        JFormattedTextField campoData = null;
        try {
            MaskFormatter mask = new MaskFormatter("##/##/####");
            mask.setPlaceholderCharacter('_');
            mask.setValidCharacters("0123456789");
            campoData = new JFormattedTextField(mask);
            campoData.setColumns(10);
        } catch (ParseException e) {

            campoData = new JFormattedTextField();
        }
        return campoData;
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

    private void atualizarProdutos(JComboBox<String> combo) {
        combo.removeAllItems();
        try {
            List<String> produtos = controller.listarProdutos();
            
            if (produtos != null && !produtos.isEmpty()) {
                for (String linha : produtos) {
                    if (linha != null) {
                        String linhaLimpa = linha.trim();

                        if (linhaLimpa.isEmpty() || 
                            linhaLimpa.startsWith("ERROR") || 
                            linhaLimpa.startsWith("Falha na comunicação") ||
                            linhaLimpa.startsWith("SUCCESS|") && linhaLimpa.length() == 8) {
                            continue;
                        }

                        if (linhaLimpa.startsWith("SUCCESS|")) {
                            linhaLimpa = linhaLimpa.substring(8).trim();
                        }

                        if (!linhaLimpa.isEmpty()) {
                            combo.addItem(linhaLimpa);
                        }
                    }
                }
            }
            

            if (combo.getItemCount() == 0) {
                combo.addItem("(Nenhum produto cadastrado)");
            }
        } catch (Exception e) {
            combo.addItem("(Erro ao carregar produtos)");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}

