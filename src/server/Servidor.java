package server;

import dao.CategoriaDAO;
import dao.MovimentacaoDAO;
import dao.ProdutoDAO;
import model.Categoria;
import model.Movimentacao;
import model.Produto;
import service.RelatorioService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servidor {
    private static final int PORTA = 12345;
    private ProdutoDAO produtoDAO;
    private CategoriaDAO categoriaDAO;
    private MovimentacaoDAO movimentacaoDAO;
    private RelatorioService relatorioService;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Servidor() {
        this.produtoDAO = new ProdutoDAO();
        this.categoriaDAO = new CategoriaDAO();
        this.movimentacaoDAO = new MovimentacaoDAO();
        this.relatorioService = new RelatorioService(produtoDAO, categoriaDAO, movimentacaoDAO);
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor iniciado na porta " + PORTA);
            System.out.println("Aguardando conexões...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                
                // Criar thread para cada cliente
                new Thread(new ClienteHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }

    private class ClienteHandler implements Runnable {
        private final Socket socket;

        public ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String comando;
                while ((comando = in.readLine()) != null) {
                    System.out.println("Comando recebido: " + comando);
                    String resposta = processarComando(comando);
                    out.println(resposta);
                    out.println(); // Linha vazia para indicar fim da resposta
                    out.flush();
                }
            } catch (IOException e) {
                System.err.println("Erro na comunicação com cliente: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar socket: " + e.getMessage());
                }
            }
        }

        private String processarComando(String comando) {
            try {
                // Verificar se é formato do frontend (chave=valor separado por ; ou apenas acao=valor)
                if (comando.contains("=")) {
                    return processarComandoFrontend(comando);
                } else {
                    // Formato antigo (pipe separado)
                    return processarComandoAntigo(comando);
                }
            } catch (Exception e) {
                return "ERROR|" + e.getMessage();
            }
        }

        private String processarComandoFrontend(String comando) throws Exception {
            Map<String, String> dados = parseKeyValueFormat(comando);
            String acao = dados.get("acao");
            
            if (acao == null) {
                throw new Exception("Ação não especificada");
            }

            switch (acao.toLowerCase()) {
                // ========== PRODUTOS ==========
                case "cadastrarproduto":
                    return cadastrarProdutoFrontend(dados);
                case "listarprodutos":
                    return listarProdutosFrontend();
                
                // ========== CATEGORIAS ==========
                case "cadastrarcategoria":
                    return cadastrarCategoriaFrontend(dados);
                case "listarcategorias":
                    return listarCategoriasFrontend();
                
                // ========== MOVIMENTAÇÕES ==========
                case "registrarmovimentacao":
                    return registrarMovimentacaoFrontend(dados);
                case "listarmovimentacoes":
                    return listarMovimentacoesFrontend();
                
                // ========== RELATÓRIOS ==========
                case "gerarrelatorio":
                    return gerarRelatorioFrontend(dados);
                
                // ========== LIMPEZA ==========
                case "limpartudo":
                    return limparTudo();
                
                default:
                    throw new Exception("Ação não reconhecida: " + acao);
            }
        }

        private String processarComandoAntigo(String comando) throws Exception {
            String[] partes = comando.split("\\|");
            String operacao = partes[0].toUpperCase();

            switch (operacao) {
                // ========== PRODUTOS ==========
                case "PRODUTO_CRIAR":
                    return criarProduto(partes);
                case "PRODUTO_LISTAR":
                    return listarProdutos();
                case "PRODUTO_BUSCAR":
                    return buscarProduto(partes);
                case "PRODUTO_ATUALIZAR":
                    return atualizarProduto(partes);
                case "PRODUTO_DELETAR":
                    return deletarProduto(partes);

                // ========== CATEGORIAS ==========
                case "CATEGORIA_CRIAR":
                    return criarCategoria(partes);
                case "CATEGORIA_LISTAR":
                    return listarCategorias();
                case "CATEGORIA_BUSCAR":
                    return buscarCategoria(partes);
                case "CATEGORIA_ATUALIZAR":
                    return atualizarCategoria(partes);
                case "CATEGORIA_DELETAR":
                    return deletarCategoria(partes);

                // ========== MOVIMENTAÇÕES ==========
                case "MOVIMENTACAO_CRIAR":
                    return criarMovimentacao(partes);
                case "MOVIMENTACAO_LISTAR":
                    return listarMovimentacoes();

                // ========== RELATÓRIOS ==========
                case "RELATORIO_LISTA_PRECOS":
                    return relatorioService.gerarListaPrecos();
                case "RELATORIO_BALANCO":
                    return relatorioService.gerarBalancoFisicoFinanceiro();
                case "RELATORIO_ABAIXO_MINIMO":
                    return relatorioService.gerarProdutosAbaixoMinimo();
                case "RELATORIO_QUANTIDADE_CATEGORIA":
                    return relatorioService.gerarQuantidadePorCategoria();
                case "RELATORIO_MAIS_MOVIMENTACOES":
                    return relatorioService.gerarProdutoMaisMovimentacoes();

                default:
                    throw new Exception("Operação não reconhecida: " + operacao);
            }
        }

        private Map<String, String> parseKeyValueFormat(String comando) {
            Map<String, String> dados = new HashMap<>();
            // Dividir por ; se houver, senão tratar como um único par chave=valor
            String[] pares = comando.contains(";") ? comando.split(";") : new String[]{comando};
            for (String par : pares) {
                String[] chaveValor = par.split("=", 2);
                if (chaveValor.length == 2) {
                    dados.put(chaveValor[0].toLowerCase().trim(), chaveValor[1].trim());
                }
            }
            return dados;
        }

        // ========== MÉTODOS FRONTEND ==========
        
        private String cadastrarProdutoFrontend(Map<String, String> dados) throws Exception {
            String nome = dados.get("nome");
            String categoriaNome = dados.get("categoria");
            String estoqueInicialStr = dados.get("estoqueinicial");
            String estoqueMinimoStr = dados.get("estoqueminimo");
            String precoStr = dados.get("preco");

            if (nome == null || categoriaNome == null || estoqueInicialStr == null || estoqueMinimoStr == null || precoStr == null) {
                throw new Exception("Parâmetros incompletos para cadastrar produto");
            }

            Categoria categoria = categoriaDAO.buscarPorNome(categoriaNome);
            if (categoria == null) {
                throw new Exception("Categoria não encontrada: " + categoriaNome);
            }

            double preco = Double.parseDouble(precoStr);
            int estoqueInicial = Integer.parseInt(estoqueInicialStr);
            int estoqueMinimo = Integer.parseInt(estoqueMinimoStr);
            
            // Validar que estoque inicial não seja menor que mínimo
            if (estoqueInicial < estoqueMinimo) {
                throw new Exception("Estoque inicial não pode ser menor que o estoque mínimo");
            }
            
            int estoqueMaximo = estoqueMinimo * 10; // Valor padrão se não especificado
            String unidade = "Unidade"; // Valor padrão se não especificado

            Produto produto = new Produto(nome, preco, unidade, estoqueInicial, estoqueMinimo, estoqueMaximo, categoria);
            produtoDAO.criar(produto);
            return "Produto cadastrado com sucesso: " + nome;
        }

        private String listarProdutosFrontend() {
            StringBuilder sb = new StringBuilder();
            List<Produto> produtos = produtoDAO.listarTodos();
            for (Produto p : produtos) {
                String categoriaNome = p.getCategoria() != null ? p.getCategoria().getNome() : "N/A";
                sb.append(String.format("%s - R$ %.2f (%s) | Estoque: %d | Mín: %d | Máx: %d | Categoria: %s%n",
                    p.getNome(), p.getPrecoUnitario(), p.getUnidade(),
                    p.getQuantidadeEstoque(), p.getQuantidadeMinima(),
                    p.getQuantidadeMaxima(), categoriaNome));
            }
            return sb.toString();
        }

        private String cadastrarCategoriaFrontend(Map<String, String> dados) throws Exception {
            String nome = dados.get("nome");
            String tamanhoStr = dados.get("tamanho");
            String embalagemStr = dados.get("embalagem");

            if (nome == null || nome.trim().isEmpty()) {
                throw new Exception("Nome da categoria é obrigatório");
            }

            // Usar valores fornecidos ou padrões
            Categoria.Tamanho tamanho = Categoria.Tamanho.MEDIO;
            Categoria.Embalagem embalagem = Categoria.Embalagem.PLASTICO;

            if (tamanhoStr != null && !tamanhoStr.trim().isEmpty()) {
                try {
                    tamanho = Categoria.Tamanho.valueOf(tamanhoStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new Exception("Tamanho inválido. Use: PEQUENO, MEDIO ou GRANDE");
                }
            }

            if (embalagemStr != null && !embalagemStr.trim().isEmpty()) {
                try {
                    embalagem = Categoria.Embalagem.valueOf(embalagemStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new Exception("Embalagem inválida. Use: LATA, VIDRO ou PLASTICO");
                }
            }

            Categoria categoria = new Categoria(nome, tamanho, embalagem);
            categoriaDAO.criar(categoria);
            return "Categoria cadastrada com sucesso: " + nome;
        }

        private String listarCategoriasFrontend() {
            StringBuilder sb = new StringBuilder();
            List<Categoria> categorias = categoriaDAO.listarTodas();
            for (Categoria c : categorias) {
                sb.append(String.format("%s - %s | %s%n", c.getNome(), c.getTamanho(), c.getEmbalagem()));
            }
            return sb.toString();
        }

        private String registrarMovimentacaoFrontend(Map<String, String> dados) throws Exception {
            String produtoNome = dados.get("produtoid"); // Frontend envia como produtoId
            String tipoStr = dados.get("tipo");
            String quantidadeStr = dados.get("quantidade");

            if (produtoNome == null || tipoStr == null || quantidadeStr == null) {
                throw new Exception("Parâmetros incompletos para registrar movimentação");
            }

            // Tentar buscar por nome primeiro
            Produto produto = produtoDAO.buscarPorNome(produtoNome);
            if (produto == null) {
                // Se não encontrar, talvez seja um ID numérico (buscar na lista)
                List<Produto> produtos = produtoDAO.listarTodos();
                try {
                    int indice = Integer.parseInt(produtoNome) - 1;
                    if (indice >= 0 && indice < produtos.size()) {
                        produto = produtos.get(indice);
                    }
                } catch (NumberFormatException e) {
                    // Não é um número válido
                }
            }

            if (produto == null) {
                throw new Exception("Produto não encontrado: " + produtoNome);
            }

            Movimentacao.TipoMovimentacao tipo = Movimentacao.TipoMovimentacao.valueOf(tipoStr.toUpperCase());
            int quantidade = Integer.parseInt(quantidadeStr);

            Movimentacao movimentacao = new Movimentacao(produto, LocalDateTime.now(), quantidade, tipo);
            movimentacaoDAO.criar(movimentacao);
            return "Movimentação registrada com sucesso. Novo estoque: " + produto.getQuantidadeEstoque();
        }

        private String listarMovimentacoesFrontend() {
            StringBuilder sb = new StringBuilder();
            List<Movimentacao> movimentacoes = movimentacaoDAO.listarTodas();
            for (Movimentacao m : movimentacoes) {
                String produtoNome = m.getProduto() != null ? m.getProduto().getNome() : "N/A";
                String dataStr = m.getData().format(formatter);
                sb.append(String.format("%s | %s | %s | Quantidade: %d%n",
                    produtoNome, dataStr, m.getTipo(), m.getQuantidade()));
            }
            return sb.toString();
        }

        private String gerarRelatorioFrontend(Map<String, String> dados) throws Exception {
            String tipoRelatorio = dados.get("relatorio");
            
            if (tipoRelatorio == null) {
                throw new Exception("Tipo de relatório não especificado");
            }

            switch (tipoRelatorio.toLowerCase()) {
                case "lista_precos":
                    return relatorioService.gerarListaPrecos();
                case "balanco_fisico_financeiro":
                    return relatorioService.gerarBalancoFisicoFinanceiro();
                case "produtos_abaixo_minimo":
                    return relatorioService.gerarProdutosAbaixoMinimo();
                case "quantidade_por_categoria":
                    return relatorioService.gerarQuantidadePorCategoria();
                case "produto_maior_movimentacao":
                    return relatorioService.gerarProdutoMaisMovimentacoes();
                default:
                    throw new Exception("Tipo de relatório não reconhecido: " + tipoRelatorio);
            }
        }

        private String limparTudo() {
            try {
                produtoDAO.limparTodos();
                categoriaDAO.limparTodos();
                movimentacaoDAO.limparTodos();
                return "Todos os dados foram limpos com sucesso.";
            } catch (Exception e) {
                return "Erro ao limpar dados: " + e.getMessage();
            }
        }

        // ========== MÉTODOS PRODUTO ==========
        private String criarProduto(String[] partes) throws Exception {
            if (partes.length < 8) {
                throw new Exception("Parâmetros insuficientes para criar produto");
            }

            String nome = partes[1];
            double preco = Double.parseDouble(partes[2]);
            String unidade = partes[3];
            int qtdEstoque = Integer.parseInt(partes[4]);
            int qtdMinima = Integer.parseInt(partes[5]);
            int qtdMaxima = Integer.parseInt(partes[6]);
            String categoriaNome = partes[7];

            Categoria categoria = categoriaDAO.buscarPorNome(categoriaNome);
            if (categoria == null) {
                throw new Exception("Categoria não encontrada: " + categoriaNome);
            }

            Produto produto = new Produto(nome, preco, unidade, qtdEstoque, qtdMinima, qtdMaxima, categoria);
            produtoDAO.criar(produto);
            return "SUCCESS|Produto criado com sucesso: " + nome;
        }

        private String listarProdutos() {
            StringBuilder sb = new StringBuilder("SUCCESS|");
            List<Produto> produtos = produtoDAO.listarTodos();
            for (Produto p : produtos) {
                String categoriaNome = p.getCategoria() != null ? p.getCategoria().getNome() : "N/A";
                sb.append(String.format("%s|%.2f|%s|%d|%d|%d|%s;",
                    p.getNome(), p.getPrecoUnitario(), p.getUnidade(),
                    p.getQuantidadeEstoque(), p.getQuantidadeMinima(),
                    p.getQuantidadeMaxima(), categoriaNome));
            }
            return sb.toString();
        }

        private String buscarProduto(String[] partes) throws Exception {
            if (partes.length < 2) {
                throw new Exception("Nome do produto é obrigatório");
            }
            Produto produto = produtoDAO.buscarPorNome(partes[1]);
            if (produto == null) {
                return "ERROR|Produto não encontrado";
            }
            String categoriaNome = produto.getCategoria() != null ? produto.getCategoria().getNome() : "N/A";
            return String.format("SUCCESS|%s|%.2f|%s|%d|%d|%d|%s",
                produto.getNome(), produto.getPrecoUnitario(), produto.getUnidade(),
                produto.getQuantidadeEstoque(), produto.getQuantidadeMinima(),
                produto.getQuantidadeMaxima(), categoriaNome);
        }

        private String atualizarProduto(String[] partes) throws Exception {
            if (partes.length < 8) {
                throw new Exception("Parâmetros insuficientes para atualizar produto");
            }

            String nome = partes[1];
            double preco = Double.parseDouble(partes[2]);
            String unidade = partes[3];
            int qtdEstoque = Integer.parseInt(partes[4]);
            int qtdMinima = Integer.parseInt(partes[5]);
            int qtdMaxima = Integer.parseInt(partes[6]);
            String categoriaNome = partes[7];

            Categoria categoria = categoriaDAO.buscarPorNome(categoriaNome);
            if (categoria == null) {
                throw new Exception("Categoria não encontrada: " + categoriaNome);
            }

            Produto produtoAtualizado = new Produto(nome, preco, unidade, qtdEstoque, qtdMinima, qtdMaxima, categoria);
            produtoDAO.atualizar(produtoAtualizado);
            return "SUCCESS|Produto atualizado com sucesso: " + nome;
        }

        private String deletarProduto(String[] partes) throws Exception {
            if (partes.length < 2) {
                throw new Exception("Nome do produto é obrigatório");
            }
            produtoDAO.deletar(partes[1]);
            return "SUCCESS|Produto deletado com sucesso: " + partes[1];
        }

        // ========== MÉTODOS CATEGORIA ==========
        private String criarCategoria(String[] partes) throws Exception {
            if (partes.length < 4) {
                throw new Exception("Parâmetros insuficientes para criar categoria");
            }

            String nome = partes[1];
            Categoria.Tamanho tamanho = Categoria.Tamanho.valueOf(partes[2].toUpperCase());
            Categoria.Embalagem embalagem = Categoria.Embalagem.valueOf(partes[3].toUpperCase());

            Categoria categoria = new Categoria(nome, tamanho, embalagem);
            categoriaDAO.criar(categoria);
            return "SUCCESS|Categoria criada com sucesso: " + nome;
        }

        private String listarCategorias() {
            StringBuilder sb = new StringBuilder("SUCCESS|");
            List<Categoria> categorias = categoriaDAO.listarTodas();
            for (Categoria c : categorias) {
                sb.append(String.format("%s|%s|%s;",
                    c.getNome(), c.getTamanho(), c.getEmbalagem()));
            }
            return sb.toString();
        }

        private String buscarCategoria(String[] partes) throws Exception {
            if (partes.length < 2) {
                throw new Exception("Nome da categoria é obrigatório");
            }
            Categoria categoria = categoriaDAO.buscarPorNome(partes[1]);
            if (categoria == null) {
                return "ERROR|Categoria não encontrada";
            }
            return String.format("SUCCESS|%s|%s|%s",
                categoria.getNome(), categoria.getTamanho(), categoria.getEmbalagem());
        }

        private String atualizarCategoria(String[] partes) throws Exception {
            if (partes.length < 4) {
                throw new Exception("Parâmetros insuficientes para atualizar categoria");
            }

            String nome = partes[1];
            Categoria.Tamanho tamanho = Categoria.Tamanho.valueOf(partes[2].toUpperCase());
            Categoria.Embalagem embalagem = Categoria.Embalagem.valueOf(partes[3].toUpperCase());

            Categoria categoriaAtualizada = new Categoria(nome, tamanho, embalagem);
            categoriaDAO.atualizar(categoriaAtualizada);
            return "SUCCESS|Categoria atualizada com sucesso: " + nome;
        }

        private String deletarCategoria(String[] partes) throws Exception {
            if (partes.length < 2) {
                throw new Exception("Nome da categoria é obrigatório");
            }
            categoriaDAO.deletar(partes[1]);
            return "SUCCESS|Categoria deletada com sucesso: " + partes[1];
        }

        // ========== MÉTODOS MOVIMENTAÇÃO ==========
        private String criarMovimentacao(String[] partes) throws Exception {
            if (partes.length < 4) {
                throw new Exception("Parâmetros insuficientes para criar movimentação");
            }

            String produtoNome = partes[1];
            Movimentacao.TipoMovimentacao tipo = Movimentacao.TipoMovimentacao.valueOf(partes[2].toUpperCase());
            int quantidade = Integer.parseInt(partes[3]);

            Produto produto = produtoDAO.buscarPorNome(produtoNome);
            if (produto == null) {
                throw new Exception("Produto não encontrado: " + produtoNome);
            }

            Movimentacao movimentacao = new Movimentacao(produto, LocalDateTime.now(), quantidade, tipo);
            movimentacaoDAO.criar(movimentacao);
            return "SUCCESS|Movimentação criada com sucesso. Novo estoque: " + produto.getQuantidadeEstoque();
        }

        private String listarMovimentacoes() {
            StringBuilder sb = new StringBuilder("SUCCESS|");
            List<Movimentacao> movimentacoes = movimentacaoDAO.listarTodas();
            for (Movimentacao m : movimentacoes) {
                String produtoNome = m.getProduto() != null ? m.getProduto().getNome() : "N/A";
                String dataStr = m.getData().format(formatter);
                sb.append(String.format("%s|%s|%s|%d;",
                    produtoNome, dataStr, m.getTipo(), m.getQuantidade()));
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.iniciar();
    }
}

