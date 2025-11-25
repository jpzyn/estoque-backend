import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Cliente de Teste para o Sistema de Estoque
 * Este cliente conecta ao servidor e testa todas as operações CRUD e relatórios.
 * 
 * INSTRUÇÕES DE EXECUÇÃO:
 * 1. Primeiro, inicie o servidor: Execute Servidor.java
 * 2. Depois, execute este arquivo: ClienteTeste.java
 */
public class ClienteTeste {
    private static final String HOST = "localhost";
    private static final int PORTA = 12345;

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("CLIENTE DE TESTE - SISTEMA DE ESTOQUE");
        System.out.println("=".repeat(70));
        System.out.println("Conectando ao servidor em " + HOST + ":" + PORTA + "...\n");

        try (Socket socket = new Socket(HOST, PORTA);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("✓ Conectado ao servidor!\n");

            // ========== TESTE 1: CRUD DE CATEGORIAS ==========
            System.out.println("[TESTE 1] Testando CRUD de Categorias");
            System.out.println("-".repeat(70));

            // Criar categorias
            System.out.println("\n1.1 Criando categorias...");
            String resposta1 = enviarComando(out, in, "CATEGORIA_CRIAR|Limpeza|GRANDE|PLASTICO");
            System.out.println("   Resposta: " + resposta1);

            String resposta2 = enviarComando(out, in, "CATEGORIA_CRIAR|Alimentos|MEDIO|LATA");
            System.out.println("   Resposta: " + resposta2);

            String resposta3 = enviarComando(out, in, "CATEGORIA_CRIAR|Óleos|PEQUENO|VIDRO");
            System.out.println("   Resposta: " + resposta3);

            // Listar categorias
            System.out.println("\n1.2 Listando categorias...");
            String resposta4 = enviarComando(out, in, "CATEGORIA_LISTAR");
            System.out.println("   Resposta: " + resposta4);

            // Buscar categoria
            System.out.println("\n1.3 Buscando categoria 'Limpeza'...");
            String resposta5 = enviarComando(out, in, "CATEGORIA_BUSCAR|Limpeza");
            System.out.println("   Resposta: " + resposta5);

            // ========== TESTE 2: CRUD DE PRODUTOS ==========
            System.out.println("\n\n[TESTE 2] Testando CRUD de Produtos");
            System.out.println("-".repeat(70));

            // Criar produtos
            System.out.println("\n2.1 Criando produtos...");
            String resposta6 = enviarComando(out, in, "PRODUTO_CRIAR|Detergente Neutro|5.50|Litro|100|20|200|Limpeza");
            System.out.println("   Resposta: " + resposta6);

            String resposta7 = enviarComando(out, in, "PRODUTO_CRIAR|Leite Integral|3.20|Litro|50|15|100|Alimentos");
            System.out.println("   Resposta: " + resposta7);

            String resposta8 = enviarComando(out, in, "PRODUTO_CRIAR|Óleo de Soja|4.80|Litro|80|25|150|Óleos");
            System.out.println("   Resposta: " + resposta8);

            // Listar produtos
            System.out.println("\n2.2 Listando produtos...");
            String resposta9 = enviarComando(out, in, "PRODUTO_LISTAR");
            System.out.println("   Resposta: " + resposta9);

            // Buscar produto
            System.out.println("\n2.3 Buscando produto 'Detergente Neutro'...");
            String resposta10 = enviarComando(out, in, "PRODUTO_BUSCAR|Detergente Neutro");
            System.out.println("   Resposta: " + resposta10);

            // Atualizar produto
            System.out.println("\n2.4 Atualizando preço do 'Detergente Neutro' para R$ 6.00...");
            String resposta11 = enviarComando(out, in, "PRODUTO_ATUALIZAR|Detergente Neutro|6.00|Litro|100|20|200|Limpeza");
            System.out.println("   Resposta: " + resposta11);

            // Verificar atualização
            String resposta12 = enviarComando(out, in, "PRODUTO_BUSCAR|Detergente Neutro");
            System.out.println("   Produto atualizado: " + resposta12);

            // ========== TESTE 3: MOVIMENTAÇÕES ==========
            System.out.println("\n\n[TESTE 3] Testando Movimentações");
            System.out.println("-".repeat(70));

            // Entrada de estoque
            System.out.println("\n3.1 Criando entrada de 50 unidades de Detergente...");
            String resposta13 = enviarComando(out, in, "MOVIMENTACAO_CRIAR|Detergente Neutro|ENTRADA|50");
            System.out.println("   Resposta: " + resposta13);

            System.out.println("\n3.2 Criando entrada de 30 unidades de Leite...");
            String resposta14 = enviarComando(out, in, "MOVIMENTACAO_CRIAR|Leite Integral|ENTRADA|30");
            System.out.println("   Resposta: " + resposta14);

            // Saída de estoque
            System.out.println("\n3.3 Criando saída de 20 unidades de Detergente...");
            String resposta15 = enviarComando(out, in, "MOVIMENTACAO_CRIAR|Detergente Neutro|SAIDA|20");
            System.out.println("   Resposta: " + resposta15);

            // Listar movimentações
            System.out.println("\n3.4 Listando todas as movimentações...");
            String resposta16 = enviarComando(out, in, "MOVIMENTACAO_LISTAR");
            System.out.println("   Resposta: " + resposta16);

            // Testar validação - tentar sair mais do que permitido
            System.out.println("\n3.5 Testando validação (tentativa de saída que deixaria abaixo do mínimo)...");
            String resposta17 = enviarComando(out, in, "MOVIMENTACAO_CRIAR|Leite Integral|SAIDA|100");
            System.out.println("   Resposta: " + resposta17);

            // ========== TESTE 4: RELATÓRIOS ==========
            System.out.println("\n\n[TESTE 4] Testando Relatórios");
            System.out.println("-".repeat(70));

            System.out.println("\n4.1 Relatório: Lista de Preços");
            String resposta18 = enviarComando(out, in, "RELATORIO_LISTA_PRECOS");
            System.out.println(resposta18);

            System.out.println("\n4.2 Relatório: Balanço Físico/Financeiro");
            String resposta19 = enviarComando(out, in, "RELATORIO_BALANCO");
            System.out.println(resposta19);

            System.out.println("\n4.3 Relatório: Produtos Abaixo do Mínimo");
            String resposta20 = enviarComando(out, in, "RELATORIO_ABAIXO_MINIMO");
            System.out.println(resposta20);

            System.out.println("\n4.4 Relatório: Quantidade por Categoria");
            String resposta21 = enviarComando(out, in, "RELATORIO_QUANTIDADE_CATEGORIA");
            System.out.println(resposta21);

            System.out.println("\n4.5 Relatório: Produto com Mais Movimentações");
            String resposta22 = enviarComando(out, in, "RELATORIO_MAIS_MOVIMENTACOES");
            System.out.println(resposta22);

            // ========== TESTE 5: DELETE ==========
            System.out.println("\n\n[TESTE 5] Testando Delete");
            System.out.println("-".repeat(70));

            System.out.println("\n5.1 Deletando produto 'Óleo de Soja'...");
            String resposta23 = enviarComando(out, in, "PRODUTO_DELETAR|Óleo de Soja");
            System.out.println("   Resposta: " + resposta23);

            System.out.println("\n5.2 Verificando lista após delete...");
            String resposta24 = enviarComando(out, in, "PRODUTO_LISTAR");
            System.out.println("   Resposta: " + resposta24);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("TESTES CONCLUÍDOS COM SUCESSO!");
            System.out.println("=".repeat(70));

        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
            System.err.println("\nCertifique-se de que o servidor está rodando!");
            System.err.println("Execute Servidor.java primeiro antes de executar este teste.");
        }
    }

    /**
     * Envia um comando ao servidor e retorna a resposta
     */
    private static String enviarComando(PrintWriter out, BufferedReader in, String comando) {
        try {
            out.println(comando);
            String resposta = in.readLine();
            if (resposta == null) {
                return "Erro: Nenhuma resposta recebida do servidor";
            }
            return resposta;
        } catch (IOException e) {
            return "Erro ao enviar comando: " + e.getMessage();
        }
    }
}

