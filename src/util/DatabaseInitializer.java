package util;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Inicializa o banco de dados criando as tabelas necessárias.
 */
public class DatabaseInitializer {
    
    /**
     * Cria todas as tabelas do banco de dados executando o script schema.sql.
     */
    public static void initializeDatabase() {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        
        try (Connection conn = dbConnection.getConnection();
             InputStream input = DatabaseInitializer.class.getClassLoader().getResourceAsStream("schema.sql")) {
            
            if (input == null) {
                System.err.println("Arquivo schema.sql não encontrado no classpath. Criando tabelas manualmente...");
                criarTabelasManualmente(conn);
                return;
            }
            
            // Ler o script SQL
            StringBuilder sqlScript = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Pular comentários e linhas vazias
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("USE")) {
                        continue;
                    }
                    sqlScript.append(line).append(" ");
                }
            }
            
            // Executar comandos SQL separados por ponto e vírgula
            String[] comandos = sqlScript.toString().split(";");
            try (Statement stmt = conn.createStatement()) {
                for (String comando : comandos) {
                    comando = comando.trim();
                    if (!comando.isEmpty()) {
                        stmt.executeUpdate(comando);
                    }
                }
            }
            
            System.out.println("Tabelas criadas/verificadas com sucesso!");
            
        } catch (Exception e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
            
            // Tentar criar tabelas manualmente
            try (Connection conn = dbConnection.getConnection()) {
                criarTabelasManualmente(conn);
            } catch (SQLException ex) {
                System.err.println("Erro ao criar tabelas manualmente: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Cria as tabelas manualmente caso o arquivo schema.sql não seja encontrado.
     */
    private static void criarTabelasManualmente(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // Criar tabela categorias
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS categorias (" +
                "nome VARCHAR(100) PRIMARY KEY, " +
                "tamanho ENUM('PEQUENO', 'MEDIO', 'GRANDE') NOT NULL, " +
                "embalagem ENUM('LATA', 'VIDRO', 'PLASTICO') NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );
            
            // Criar tabela produtos
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS produtos (" +
                "nome VARCHAR(100) PRIMARY KEY, " +
                "preco DECIMAL(10, 2) NOT NULL, " +
                "unidade VARCHAR(20) NOT NULL, " +
                "estoque_atual INT NOT NULL DEFAULT 0, " +
                "estoque_minimo INT NOT NULL DEFAULT 0, " +
                "estoque_maximo INT NOT NULL DEFAULT 0, " +
                "categoria_nome VARCHAR(100) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (categoria_nome) REFERENCES categorias(nome) ON DELETE RESTRICT ON UPDATE CASCADE, " +
                "INDEX idx_categoria (categoria_nome)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );
            
            // Criar tabela movimentacoes
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS movimentacoes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "produto_nome VARCHAR(100) NOT NULL, " +
                "tipo ENUM('ENTRADA', 'SAIDA') NOT NULL, " +
                "quantidade INT NOT NULL, " +
                "observacao TEXT, " +
                "data_movimentacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (produto_nome) REFERENCES produtos(nome) ON DELETE RESTRICT ON UPDATE CASCADE, " +
                "INDEX idx_produto (produto_nome), " +
                "INDEX idx_tipo (tipo), " +
                "INDEX idx_data (data_movimentacao)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );
            
            System.out.println("Tabelas criadas manualmente com sucesso!");
        }
    }
}

