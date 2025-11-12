import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Testador de Estoque - Demonstra o funcionamento das classes Produto, Categoria e Movimentacao
 * Este arquivo pode ser executado diretamente para validar o fluxo básico do sistema.
 * 
 * INSTRUÇÕES DE EXECUÇÃO:
 * 1. Compile: javac -d bin src/model/*.java src/TestadorEstoque.java
 * 2. Execute: java -cp bin TestadorEstoque
 */
public class TestadorEstoque {
    
    private static List<Produto> estoque = new ArrayList<>();
    private static List<Movimentacao> movimentacoes = new ArrayList<>();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("TESTADOR DO SISTEMA DE ESTOQUE");
        System.out.println("=".repeat(70));
        
        // Teste 1: Criar categorias
        teste1_CriarCategorias();
        
        // Teste 2: Criar produtos
        teste2_CriarProdutos();
        
        // Teste 3: Aplicar movimentações (entradas)
        teste3_MovimentacoesEntrada();
        
        // Teste 4: Aplicar movimentações (saídas normais)
        teste4_MovimentacoesSaida();
        
        // Teste 5: Testar validações (erro: quantidade mínima)
        teste5_ValidarMinimo();
        
        // Teste 6: Testar validações (erro: quantidade máxima)
        teste6_ValidarMaximo();
        
        // Teste 7: Reajustar preços
        teste7_ReajustarPrecos();
        
        // Teste 8: Gerar relatórios simples
        teste8_Relatorios();
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("TESTES FINALIZADOS COM SUCESSO!");
        System.out.println("=".repeat(70));
    }
    
    // ==================== TESTES ====================
    
    private static void teste1_CriarCategorias() {
        System.out.println("\n[TESTE 1] Criando categorias...");
        
        Categoria limpeza = new Categoria("Limpeza", Categoria.Tamanho.GRANDE, Categoria.Embalagem.PLASTICO);
        Categoria alimentos = new Categoria("Alimentos", Categoria.Tamanho.MEDIO, Categoria.Embalagem.LATA);
        Categoria oleos = new Categoria("Óleos", Categoria.Tamanho.PEQUENO, Categoria.Embalagem.VIDRO);
        
        System.out.println("  ✓ Categoria 1: " + limpeza.getNome() + " (" + limpeza.getTamanho() + ", " + limpeza.getEmbalagem() + ")");
        System.out.println("  ✓ Categoria 2: " + alimentos.getNome() + " (" + alimentos.getTamanho() + ", " + alimentos.getEmbalagem() + ")");
        System.out.println("  ✓ Categoria 3: " + oleos.getNome() + " (" + oleos.getTamanho() + ", " + oleos.getEmbalagem() + ")");
    }
    
    private static void teste2_CriarProdutos() {
        System.out.println("\n[TESTE 2] Criando produtos...");
        
        Categoria limpeza = new Categoria("Limpeza", Categoria.Tamanho.GRANDE, Categoria.Embalagem.PLASTICO);
        Categoria alimentos = new Categoria("Alimentos", Categoria.Tamanho.MEDIO, Categoria.Embalagem.LATA);
        Categoria oleos = new Categoria("Óleos", Categoria.Tamanho.PEQUENO, Categoria.Embalagem.VIDRO);
        
        // Produto 1: Detergente
        Produto detergente = new Produto(
            "Detergente Neutro",
            5.50,
            "Litro",
            100,      // quantidade estoque
            20,       // quantidade mínima
            200,      // quantidade máxima
            limpeza
        );
        estoque.add(detergente);
        
        // Produto 2: Leite
        Produto leite = new Produto(
            "Leite Integral",
            3.20,
            "Litro",
            50,
            15,
            100,
            alimentos
        );
        estoque.add(leite);
        
        // Produto 3: Óleo de Soja
        Produto oleo = new Produto(
            "Óleo de Soja",
            4.80,
            "Litro",
            80,
            25,
            150,
            oleos
        );
        estoque.add(oleo);
        
        System.out.println("  ✓ Produto 1 criado: " + detergente.getNome() + " - R$ " + detergente.getPrecoUnitario() + " (Estoque: " + detergente.getQuantidadeEstoque() + ")");
        System.out.println("  ✓ Produto 2 criado: " + leite.getNome() + " - R$ " + leite.getPrecoUnitario() + " (Estoque: " + leite.getQuantidadeEstoque() + ")");
        System.out.println("  ✓ Produto 3 criado: " + oleo.getNome() + " - R$ " + oleo.getPrecoUnitario() + " (Estoque: " + oleo.getQuantidadeEstoque() + ")");
    }
    
    private static void teste3_MovimentacoesEntrada() {
        System.out.println("\n[TESTE 3] Aplicando movimentações de ENTRADA...");
        
        Produto detergente = estoque.get(0);
        Produto leite = estoque.get(1);
        
        try {
            // Entrada de 50 unidades de detergente
            Movimentacao mov1 = new Movimentacao(detergente, LocalDateTime.now().minusHours(2), 50, Movimentacao.TipoMovimentacao.ENTRADA);
            mov1.validarMovimentacao();
            detergente.setQuantidadeEstoque(detergente.getQuantidadeEstoque() + 50);
            movimentacoes.add(mov1);
            System.out.println("  ✓ Entrada de 50 unidades de " + detergente.getNome() + " - Novo estoque: " + detergente.getQuantidadeEstoque());
            
            // Entrada de 30 unidades de leite
            Movimentacao mov2 = new Movimentacao(leite, LocalDateTime.now().minusHours(1), 30, Movimentacao.TipoMovimentacao.ENTRADA);
            mov2.validarMovimentacao();
            leite.setQuantidadeEstoque(leite.getQuantidadeEstoque() + 30);
            movimentacoes.add(mov2);
            System.out.println("  ✓ Entrada de 30 unidades de " + leite.getNome() + " - Novo estoque: " + leite.getQuantidadeEstoque());
            
        } catch (Exception e) {
            System.out.println("  ✗ Erro: " + e.getMessage());
        }
    }
    
    private static void teste4_MovimentacoesSaida() {
        System.out.println("\n[TESTE 4] Aplicando movimentações de SAÍDA (normais)...");
        
        Produto detergente = estoque.get(0);
        Produto oleo = estoque.get(2);
        
        try {
            // Saída de 30 unidades de detergente
            Movimentacao mov3 = new Movimentacao(detergente, LocalDateTime.now(), 30, Movimentacao.TipoMovimentacao.SAIDA);
            mov3.validarMovimentacao();
            detergente.setQuantidadeEstoque(detergente.getQuantidadeEstoque() - 30);
            movimentacoes.add(mov3);
            System.out.println("  ✓ Saída de 30 unidades de " + detergente.getNome() + " - Novo estoque: " + detergente.getQuantidadeEstoque());
            
            // Saída de 40 unidades de óleo
            Movimentacao mov4 = new Movimentacao(oleo, LocalDateTime.now(), 40, Movimentacao.TipoMovimentacao.SAIDA);
            mov4.validarMovimentacao();
            oleo.setQuantidadeEstoque(oleo.getQuantidadeEstoque() - 40);
            movimentacoes.add(mov4);
            System.out.println("  ✓ Saída de 40 unidades de " + oleo.getNome() + " - Novo estoque: " + oleo.getQuantidadeEstoque());
            
        } catch (Exception e) {
            System.out.println("  ✗ Erro: " + e.getMessage());
        }
    }
    
    private static void teste5_ValidarMinimo() {
        System.out.println("\n[TESTE 5] Validando limite MÍNIMO (deve falhar)...");
        
        Produto leite = estoque.get(1);
        
        try {
            // Tentar sair 70 unidades quando tem 80 em estoque e mínimo é 15
            // Resultado: 80 - 70 = 10, que é MENOR que 15 - deve lançar exceção
            Movimentacao movFalha = new Movimentacao(leite, LocalDateTime.now(), 70, Movimentacao.TipoMovimentacao.SAIDA);
            movFalha.validarMovimentacao();
            System.out.println("  ✗ ERRO: Validação não funcionou! (Não deveria permitir)");
        } catch (Exception e) {
            System.out.println("  ✓ Validação funcionou corretamente: " + e.getMessage());
            System.out.println("     (Estoque atual: " + leite.getQuantidadeEstoque() + " | Mínimo: " + leite.getQuantidadeMinima() + ")");
        }
    }
    
    private static void teste6_ValidarMaximo() {
        System.out.println("\n[TESTE 6] Validando limite MÁXIMO (deve falhar)...");
        
        Produto detergente = estoque.get(0);
        
        try {
            // Tentar entrar 150 unidades quando tem 170 em estoque e máximo é 200
            // Resultado: 170 + 150 = 320, que é MAIOR que 200 - deve lançar exceção
            Movimentacao movFalha = new Movimentacao(detergente, LocalDateTime.now(), 150, Movimentacao.TipoMovimentacao.ENTRADA);
            movFalha.validarMovimentacao();
            System.out.println("  ✗ ERRO: Validação não funcionou! (Não deveria permitir)");
        } catch (Exception e) {
            System.out.println("  ✓ Validação funcionou corretamente: " + e.getMessage());
            System.out.println("     (Estoque atual: " + detergente.getQuantidadeEstoque() + " | Máximo: " + detergente.getQuantidadeMaxima() + ")");
        }
    }
    
    private static void teste7_ReajustarPrecos() {
        System.out.println("\n[TESTE 7] Reajustando preços (10% de aumento)...");
        
        Produto detergente = estoque.get(0);
        Produto leite = estoque.get(1);
        
        double precoAntigo1 = detergente.getPrecoUnitario();
        detergente.reajustarPreco(10);
        System.out.println("  ✓ " + detergente.getNome() + ": R$ " + precoAntigo1 + " → R$ " + String.format("%.2f", detergente.getPrecoUnitario()));
        
        double precoAntigo2 = leite.getPrecoUnitario();
        leite.reajustarPreco(10);
        System.out.println("  ✓ " + leite.getNome() + ": R$ " + precoAntigo2 + " → R$ " + String.format("%.2f", leite.getPrecoUnitario()));
    }
    
    private static void teste8_Relatorios() {
        System.out.println("\n[TESTE 8] Gerando relatórios...");
        
        // Relatório 1: Lista de Preços
        System.out.println("\n  === RELATÓRIO 1: LISTA DE PREÇOS ===");
        System.out.println("  " + "-".repeat(66));
        System.out.printf("  %-25s | %-10s | %-10s | %-15s%n", "PRODUTO", "PREÇO", "UNIDADE", "CATEGORIA");
        System.out.println("  " + "-".repeat(66));
        for (Produto p : estoque) {
            System.out.printf("  %-25s | R$ %7.2f | %-10s | %-15s%n", 
                p.getNome(), 
                p.getPrecoUnitario(), 
                p.getUnidade(),
                p.getCategoria().getNome()
            );
        }
        
        // Relatório 2: Balanço Físico/Financeiro
        System.out.println("\n  === RELATÓRIO 2: BALANÇO FÍSICO/FINANCEIRO ===");
        System.out.println("  " + "-".repeat(80));
        System.out.printf("  %-25s | %-10s | %-15s | %-15s%n", "PRODUTO", "QTDE", "VALOR UNITÁRIO", "VALOR TOTAL");
        System.out.println("  " + "-".repeat(80));
        double totalEstoque = 0;
        for (Produto p : estoque) {
            double valorTotal = p.getPrecoUnitario() * p.getQuantidadeEstoque();
            totalEstoque += valorTotal;
            System.out.printf("  %-25s | %8d | R$ %11.2f | R$ %11.2f%n", 
                p.getNome(), 
                p.getQuantidadeEstoque(),
                p.getPrecoUnitario(),
                valorTotal
            );
        }
        System.out.println("  " + "-".repeat(80));
        System.out.printf("  %-25s | %8s | %15s | R$ %11.2f%n", "TOTAL GERAL", "", "", totalEstoque);
        
        // Relatório 3: Produtos Abaixo do Mínimo
        System.out.println("\n  === RELATÓRIO 3: PRODUTOS ABAIXO DO MÍNIMO ===");
        System.out.println("  " + "-".repeat(60));
        System.out.printf("  %-25s | %-12s | %-12s%n", "PRODUTO", "ESTOQUE", "MÍNIMO");
        System.out.println("  " + "-".repeat(60));
        boolean temAbaixoMinimo = false;
        for (Produto p : estoque) {
            if (p.getQuantidadeEstoque() < p.getQuantidadeMinima()) {
                System.out.printf("  %-25s | %10d | %10d%n", 
                    p.getNome(), 
                    p.getQuantidadeEstoque(),
                    p.getQuantidadeMinima()
                );
                temAbaixoMinimo = true;
            }
        }
        if (!temAbaixoMinimo) {
            System.out.println("  ✓ Nenhum produto abaixo do mínimo");
        }
        
        // Relatório 4: Histórico de Movimentações
        System.out.println("\n  === RELATÓRIO 4: HISTÓRICO DE MOVIMENTAÇÕES ===");
        System.out.println("  " + "-".repeat(80));
        System.out.printf("  %-25s | %-20s | %-8s | %-12s%n", "PRODUTO", "DATA/HORA", "TIPO", "QUANTIDADE");
        System.out.println("  " + "-".repeat(80));
        for (Movimentacao m : movimentacoes) {
            System.out.printf("  %-25s | %-20s | %-8s | %10d%n", 
                m.getProduto().getNome(),
                m.getData().format(formatter),
                m.getTipo().toString(),
                m.getQuantidade()
            );
        }
    }
}
