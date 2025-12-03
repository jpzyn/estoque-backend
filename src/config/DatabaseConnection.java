package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Gerencia a conexão com o banco de dados MySQL.
 * Utiliza padrão Singleton para garantir uma única instância de conexão.
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;
    private String database;
    
    private DatabaseConnection() {
        carregarConfiguracao();
        criarBancoSeNaoExistir();
    }
    
    /**
     * Retorna a instância única da classe (Singleton).
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Carrega as configurações do arquivo database.properties.
     */
    private void carregarConfiguracao() {
        Properties props = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                // Se não encontrar no classpath, tenta no diretório do projeto
                try (FileInputStream fileInput = new FileInputStream("database.properties")) {
                    props.load(fileInput);
                }
            } else {
                props.load(input);
            }
            
            String host = props.getProperty("db.host", "localhost");
            String port = props.getProperty("db.port", "3306");
            database = props.getProperty("db.database", "estoque");
            username = props.getProperty("db.username", "root");
            password = props.getProperty("db.password", "");
            
            url = "jdbc:mysql://" + host + ":" + port + "/" + database + 
                  "?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true";
            
        } catch (IOException e) {
            System.err.println("Erro ao carregar configurações do banco de dados: " + e.getMessage());
            // Usar valores padrão
            url = "jdbc:mysql://localhost:3306/estoque?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true";
            username = "root";
            password = "";
            database = "estoque";
        }
    }
    
    /**
     * Cria o banco de dados se ele não existir.
     */
    private void criarBancoSeNaoExistir() {
        String urlSemDatabase = url.substring(0, url.lastIndexOf("/")) + "/";
        
        try (Connection conn = DriverManager.getConnection(urlSemDatabase, username, password)) {
            String createDbSQL = "CREATE DATABASE IF NOT EXISTS " + database + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            conn.createStatement().executeUpdate(createDbSQL);
            System.out.println("Banco de dados '" + database + "' verificado/criado com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao criar banco de dados: " + e.getMessage());
        }
    }
    
    /**
     * Obtém uma conexão com o banco de dados.
     * Se não houver conexão ativa, cria uma nova.
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Conectado ao banco de dados MySQL: " + database);
        }
        return connection;
    }
    
    /**
     * Fecha a conexão com o banco de dados.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexão com o banco de dados fechada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
    
    /**
     * Testa a conexão com o banco de dados.
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Erro ao testar conexão: " + e.getMessage());
            return false;
        }
    }
}

