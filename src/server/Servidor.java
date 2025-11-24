package server;

import dao.CategoriaDAO;
import dao.ProdutoDAO;
import model.Categoria;
import model.Produto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servidor {
    private static final int PORTA = 12345;

    private final ProdutoDAO produtoDAO;
    private final CategoriaDAO categoriaDAO;

    public Servidor() {
        this.produtoDAO = new ProdutoDAO();
        this.categoriaDAO = new CategoriaDAO();
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor (produtos e categorias) iniciado na porta " + PORTA);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClienteHandler(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }

    private class ClienteHandler implements Runnable {
        private final Socket socket;

        ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String comando;
                while ((comando = in.readLine()) != null) {
                    String resposta = processarComando(comando);
                    out.println(resposta);
                    out.println();
                    out.flush();
                }
            } catch (IOException e) {
                System.err.println("Erro na comunicação com cliente: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private String processarComando(String comando) {
        try {
            if (comando.contains("=")) {
                return processarComandoFrontend(comando);
            }
            return processarComandoAntigo(comando);
        } catch (Exception e) {
            return "ERROR|" + e.getMessage();
        }
    }

    private String processarComandoFrontend(String comando) throws Exception {
        Map<String, String> dados = parseKeyValueFormat(comando);
        String acao = dados.get("acao");
        if (acao == null) {
            throw new Exception("Ação não informada");
        }
        switch (acao.toLowerCase()) {
            case "cadastrarproduto":
                return cadastrarProdutoFrontend(dados);
            case "listarprodutos":
                return listarProdutosFrontend();
            case "cadastrarcategoria":
                return cadastrarCategoriaFrontend(dados);
            case "listarcategorias":
                return listarCategoriasFrontend();
            default:
                return "FUNCIONALIDADE_EM_DESENVOLVIMENTO|" + acao;
        }
    }

    private String processarComandoAntigo(String comando) throws Exception {
        String[] partes = comando.split("\\|");
        String operacao = partes[0].toUpperCase();
        switch (operacao) {
            case "PRODUTO_CRIAR":
                return criarProdutoPipe(partes);
            case "PRODUTO_LISTAR":
                return listarProdutosPipe();
            case "CATEGORIA_CRIAR":
                return criarCategoriaPipe(partes);
            case "CATEGORIA_LISTAR":
                return listarCategoriasPipe();
            default:
                return "FUNCIONALIDADE_EM_DESENVOLVIMENTO|" + operacao;
        }
    }

    private Map<String, String> parseKeyValueFormat(String comando) {
        Map<String, String> dados = new HashMap<>();
        String[] pares = comando.contains(";") ? comando.split(";") : new String[]{comando};
        for (String par : pares) {
            String[] chaveValor = par.split("=", 2);
            if (chaveValor.length == 2) {
                dados.put(chaveValor[0].toLowerCase().trim(), chaveValor[1].trim());
            }
        }
        return dados;
    }

    private String cadastrarProdutoFrontend(Map<String, String> dados) throws Exception {
        String nome = dados.get("nome");
        String categoriaNome = dados.get("categoria");
        String estoqueInicial = dados.get("estoqueinicial");
        String estoqueMinimo = dados.get("estoqueminimo");
        String estoqueMaximo = dados.get("estoquemaximo");
        String preco = dados.get("preco");
        String unidade = dados.getOrDefault("unidade", "Unidade");

        if (nome == null || categoriaNome == null || estoqueInicial == null || estoqueMinimo == null || estoqueMaximo == null || preco == null) {
            throw new Exception("Parâmetros incompletos para cadastrar produto");
        }

        Categoria categoria = categoriaDAO.buscarPorNome(categoriaNome);
        if (categoria == null) {
            throw new Exception("Categoria não encontrada: " + categoriaNome);
        }

        Produto produto = new Produto(
                nome,
                Double.parseDouble(preco),
                unidade,
                Integer.parseInt(estoqueInicial),
                Integer.parseInt(estoqueMinimo),
                Integer.parseInt(estoqueMaximo),
                categoria
        );
        produtoDAO.cadastrar(produto);
        return "SUCCESS|Produto cadastrado com sucesso";
    }

    private String listarProdutosFrontend() {
        StringBuilder sb = new StringBuilder();
        List<Produto> produtos = produtoDAO.listarTodos();
        for (Produto p : produtos) {
            String categoriaNome = p.getCategoria() != null ? p.getCategoria().getNome() : "N/A";
            sb.append(String.format("%s - R$ %.2f (%s) | Estoque: %d/%d | Categoria: %s%n",
                    p.getNome(),
                    p.getPreco(),
                    p.getUnidade(),
                    p.getEstoqueAtual(),
                    p.getEstoqueMinimo(),
                    categoriaNome));
        }
        return sb.isEmpty() ? "Nenhum produto cadastrado" : sb.toString();
    }

    private String cadastrarCategoriaFrontend(Map<String, String> dados) throws Exception {
        String nome = dados.get("nome");
        String tamanho = dados.get("tamanho");
        String embalagem = dados.get("embalagem");

        if (nome == null || tamanho == null || embalagem == null) {
            throw new Exception("Parâmetros incompletos para cadastrar categoria");
        }

        Categoria categoria = new Categoria(
                nome,
                Categoria.Tamanho.valueOf(tamanho.toUpperCase()),
                Categoria.Embalagem.valueOf(embalagem.toUpperCase())
        );
        categoriaDAO.cadastrar(categoria);
        return "SUCCESS|Categoria cadastrada com sucesso";
    }

    private String listarCategoriasFrontend() {
        StringBuilder sb = new StringBuilder();
        for (Categoria categoria : categoriaDAO.listarTodas()) {
            sb.append(String.format("%s - %s | %s%n",
                    categoria.getNome(),
                    categoria.getTamanho(),
                    categoria.getEmbalagem()));
        }
        return sb.isEmpty() ? "Nenhuma categoria cadastrada" : sb.toString();
    }

    private String criarProdutoPipe(String[] partes) throws Exception {
        if (partes.length < 7) {
            throw new Exception("Parâmetros insuficientes para criar produto");
        }
        String nome = partes[1];
        String categoriaNome = partes[2];
        int estoqueAtual = Integer.parseInt(partes[3]);
        int estoqueMinimo = Integer.parseInt(partes[4]);
        int estoqueMaximo = Integer.parseInt(partes[5]);
        double preco = Double.parseDouble(partes[6]);

        Categoria categoria = categoriaDAO.buscarPorNome(categoriaNome);
        if (categoria == null) {
            throw new Exception("Categoria não encontrada: " + categoriaNome);
        }

        Produto produto = new Produto(nome, preco, "Unidade", estoqueAtual, estoqueMinimo, estoqueMaximo, categoria);
        produtoDAO.cadastrar(produto);
        return "SUCCESS|Produto criado";
    }

    private String listarProdutosPipe() {
        StringBuilder sb = new StringBuilder("SUCCESS|");
        for (Produto p : produtoDAO.listarTodos()) {
            String categoriaNome = p.getCategoria() != null ? p.getCategoria().getNome() : "N/A";
            sb.append(String.format("%s|%.2f|%s|%d|%d|%s;", p.getNome(), p.getPreco(), p.getUnidade(),
                    p.getEstoqueAtual(), p.getEstoqueMinimo(), categoriaNome));
        }
        return sb.toString();
    }

    private String criarCategoriaPipe(String[] partes) throws Exception {
        if (partes.length < 4) {
            throw new Exception("Parâmetros insuficientes para criar categoria");
        }
        Categoria categoria = new Categoria(
                partes[1],
                Categoria.Tamanho.valueOf(partes[2].toUpperCase()),
                Categoria.Embalagem.valueOf(partes[3].toUpperCase())
        );
        categoriaDAO.cadastrar(categoria);
        return "SUCCESS|Categoria criada";
    }

    private String listarCategoriasPipe() {
        StringBuilder sb = new StringBuilder("SUCCESS|");
        for (Categoria categoria : categoriaDAO.listarTodas()) {
            sb.append(String.format("%s|%s|%s;", categoria.getNome(), categoria.getTamanho(), categoria.getEmbalagem()));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}

