package service;

import dao.CategoriaDAO;
import dao.MovimentacaoDAO;
import dao.ProdutoDAO;
import model.Categoria;
import model.Movimentacao;
import model.Produto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RelatorioService {
    private ProdutoDAO produtoDAO;
    private CategoriaDAO categoriaDAO;
    private MovimentacaoDAO movimentacaoDAO;

    public RelatorioService(ProdutoDAO produtoDAO, CategoriaDAO categoriaDAO, MovimentacaoDAO movimentacaoDAO) {
        this.produtoDAO = produtoDAO;
        this.categoriaDAO = categoriaDAO;
        this.movimentacaoDAO = movimentacaoDAO;
    }

    /**
     * Relatório 1: Lista de Preços
     * Retorna uma string formatada com todos os produtos e seus preços
     */
    public String gerarListaPrecos() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LISTA DE PREÇOS ===\n");
        sb.append(String.format("%-25s | %-10s | %-10s | %-15s%n", "PRODUTO", "PREÇO", "UNIDADE", "CATEGORIA"));
        sb.append("-".repeat(66)).append("\n");
        
        List<Produto> produtos = produtoDAO.listarTodos();
        for (Produto p : produtos) {
            String categoriaNome = p.getCategoria() != null ? p.getCategoria().getNome() : "N/A";
            sb.append(String.format("%-25s | R$ %7.2f | %-10s | %-15s%n", 
                p.getNome(), 
                p.getPrecoUnitario(), 
                p.getUnidade(),
                categoriaNome
            ));
        }
        
        return sb.toString();
    }

    /**
     * Relatório 2: Balanço Físico/Financeiro
     * Retorna uma string formatada com a quantidade física e valor financeiro do estoque
     */
    public String gerarBalancoFisicoFinanceiro() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== BALANÇO FÍSICO/FINANCEIRO ===\n");
        sb.append(String.format("%-25s | %-10s | %-15s | %-15s%n", "PRODUTO", "QTDE", "VALOR UNITÁRIO", "VALOR TOTAL"));
        sb.append("-".repeat(80)).append("\n");
        
        List<Produto> produtos = produtoDAO.listarTodos();
        double totalEstoque = 0;
        
        for (Produto p : produtos) {
            double valorTotal = p.getPrecoUnitario() * p.getQuantidadeEstoque();
            totalEstoque += valorTotal;
            sb.append(String.format("%-25s | %8d | R$ %11.2f | R$ %11.2f%n", 
                p.getNome(), 
                p.getQuantidadeEstoque(),
                p.getPrecoUnitario(),
                valorTotal
            ));
        }
        
        sb.append("-".repeat(80)).append("\n");
        sb.append(String.format("%-25s | %8s | %15s | R$ %11.2f%n", "TOTAL GERAL", "", "", totalEstoque));
        
        return sb.toString();
    }

    /**
     * Relatório 3: Produtos Abaixo do Mínimo
     * Retorna uma string formatada com produtos que estão abaixo da quantidade mínima
     */
    public String gerarProdutosAbaixoMinimo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PRODUTOS ABAIXO DO MÍNIMO ===\n");
        sb.append(String.format("%-25s | %-12s | %-12s%n", "PRODUTO", "ESTOQUE", "MÍNIMO"));
        sb.append("-".repeat(60)).append("\n");
        
        List<Produto> produtos = produtoDAO.listarTodos();
        boolean temAbaixoMinimo = false;
        
        for (Produto p : produtos) {
            if (p.getQuantidadeEstoque() < p.getQuantidadeMinima()) {
                sb.append(String.format("%-25s | %10d | %10d%n", 
                    p.getNome(), 
                    p.getQuantidadeEstoque(),
                    p.getQuantidadeMinima()
                ));
                temAbaixoMinimo = true;
            }
        }
        
        if (!temAbaixoMinimo) {
            sb.append("✓ Nenhum produto abaixo do mínimo\n");
        }
        
        return sb.toString();
    }

    /**
     * Relatório 4: Quantidade por Categoria
     * Retorna uma string formatada com a quantidade de produtos e unidades por categoria
     */
    public String gerarQuantidadePorCategoria() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== QUANTIDADE POR CATEGORIA ===\n");
        sb.append(String.format("%-25s | %-15s | %-15s%n", "CATEGORIA", "QTD PRODUTOS", "TOTAL UNIDADES"));
        sb.append("-".repeat(70)).append("\n");
        
        List<Categoria> categorias = categoriaDAO.listarTodas();
        
        for (Categoria categoria : categorias) {
            List<Produto> produtosCategoria = produtoDAO.buscarPorCategoria(categoria);
            int quantidadeProdutos = produtosCategoria.size();
            int totalUnidades = produtosCategoria.stream()
                    .mapToInt(Produto::getQuantidadeEstoque)
                    .sum();
            
            sb.append(String.format("%-25s | %12d | %15d%n", 
                categoria.getNome(), 
                quantidadeProdutos,
                totalUnidades
            ));
        }
        
        return sb.toString();
    }

    /**
     * Relatório 5: Produto com Mais Entrada/Saída
     * Retorna uma string formatada com os produtos que tiveram mais movimentações
     */
    public String gerarProdutoMaisMovimentacoes() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PRODUTO COM MAIS MOVIMENTAÇÕES ===\n");
        
        List<Produto> produtos = produtoDAO.listarTodos();
        
        if (produtos.isEmpty()) {
            sb.append("Nenhum produto cadastrado.\n");
            return sb.toString();
        }
        
        // Calcular totais de entrada e saída por produto
        Map<String, Integer> totalEntradas = new HashMap<>();
        Map<String, Integer> totalSaidas = new HashMap<>();
        
        for (Produto produto : produtos) {
            List<Movimentacao> entradas = movimentacaoDAO.buscarEntradasPorProduto(produto);
            List<Movimentacao> saidas = movimentacaoDAO.buscarSaidasPorProduto(produto);
            
            int somaEntradas = entradas.stream().mapToInt(Movimentacao::getQuantidade).sum();
            int somaSaidas = saidas.stream().mapToInt(Movimentacao::getQuantidade).sum();
            
            totalEntradas.put(produto.getNome(), somaEntradas);
            totalSaidas.put(produto.getNome(), somaSaidas);
        }
        
        // Encontrar produto com mais entradas
        String produtoMaisEntrada = totalEntradas.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        
        // Encontrar produto com mais saídas
        String produtoMaisSaida = totalSaidas.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        
        sb.append(String.format("%-25s | %-15s%n", "TIPO", "PRODUTO"));
        sb.append("-".repeat(50)).append("\n");
        sb.append(String.format("%-25s | %-15s%n", "Mais Entradas", produtoMaisEntrada));
        sb.append(String.format("%-25s | %-15s%n", "Mais Saídas", produtoMaisSaida));
        
        if (!produtoMaisEntrada.equals("N/A")) {
            sb.append(String.format("\nTotal de Entradas (%s): %d%n", 
                produtoMaisEntrada, totalEntradas.get(produtoMaisEntrada)));
        }
        if (!produtoMaisSaida.equals("N/A")) {
            sb.append(String.format("Total de Saídas (%s): %d%n", 
                produtoMaisSaida, totalSaidas.get(produtoMaisSaida)));
        }
        
        return sb.toString();
    }
}

