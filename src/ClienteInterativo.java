import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Cliente Interativo para o Sistema de Estoque
 * Permite digitar comandos manualmente para testar o servidor
 * 
 * INSTRUÇÕES:
 * 1. Primeiro, inicie o servidor (Servidor.java)
 * 2. Execute este arquivo
 * 3. Digite os comandos conforme solicitado
 * 4. Digite "sair" para desconectar
 */
public class ClienteInterativo {
    private static final String HOST = "localhost";
    private static final int PORTA = 12345;

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("CLIENTE INTERATIVO - SISTEMA DE ESTOQUE");
        System.out.println("=".repeat(70));
        System.out.println("Digite 'sair' para desconectar");
        System.out.println("Digite 'ajuda' para ver exemplos de comandos\n");

        try (Socket socket = new Socket(HOST, PORTA);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("✓ Conectado ao servidor em " + HOST + ":" + PORTA + "\n");

            String comando;
            while (true) {
                System.out.print("Digite um comando: ");
                String linha = consoleInput.readLine();
                if (linha == null) {
                    System.out.println("\nEntrada encerrada. Desconectando...");
                    break;
                }
                comando = linha.trim();

                if (comando.equalsIgnoreCase("sair")) {
                    System.out.println("Desconectando...");
                    break;
                }

                if (comando.equalsIgnoreCase("ajuda")) {
                    mostrarAjuda();
                    continue;
                }

                if (comando.isEmpty()) {
                    continue;
                }

                // Enviar comando ao servidor
                out.println(comando);
                
                // Receber resposta
                String resposta = in.readLine();
                if (resposta != null) {
                    System.out.println("Resposta: " + resposta);
                } else {
                    System.out.println("Erro: Nenhuma resposta recebida do servidor");
                    break;
                }
                System.out.println();
            }

        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
            System.err.println("\nCertifique-se de que o servidor está rodando!");
        }
    }

    private static void mostrarAjuda() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("EXEMPLOS DE COMANDOS:");
        System.out.println("=".repeat(70));
        
        System.out.println("\n📁 CATEGORIAS:");
        System.out.println("  CATEGORIA_CRIAR|Limpeza|GRANDE|PLASTICO");
        System.out.println("  CATEGORIA_LISTAR");
        System.out.println("  CATEGORIA_BUSCAR|Limpeza");
        System.out.println("  CATEGORIA_DELETAR|Limpeza");
        
        System.out.println("\n📦 PRODUTOS:");
        System.out.println("  PRODUTO_CRIAR|Detergente|5.50|Litro|100|20|200|Limpeza");
        System.out.println("  PRODUTO_LISTAR");
        System.out.println("  PRODUTO_BUSCAR|Detergente");
        System.out.println("  PRODUTO_DELETAR|Detergente");
        
        System.out.println("\n🔄 MOVIMENTAÇÕES:");
        System.out.println("  MOVIMENTACAO_CRIAR|Detergente|ENTRADA|50");
        System.out.println("  MOVIMENTACAO_CRIAR|Detergente|SAIDA|20");
        System.out.println("  MOVIMENTACAO_LISTAR");
        
        System.out.println("\n📊 RELATÓRIOS:");
        System.out.println("  RELATORIO_LISTA_PRECOS");
        System.out.println("  RELATORIO_BALANCO");
        System.out.println("  RELATORIO_ABAIXO_MINIMO");
        System.out.println("  RELATORIO_QUANTIDADE_CATEGORIA");
        System.out.println("  RELATORIO_MAIS_MOVIMENTACOES");
        
        System.out.println("\n" + "=".repeat(70) + "\n");
    }
}

