package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConnection;
import model.Movimentacao;
import model.Produto;

/**
 * DAO para acesso aos dados de Movimentacao usando MySQL.
 * Substitui o armazenamento em memória (ArrayList) por banco de dados.
 */
public class MovimentacaoDAO {
    
    /**
     * Cria uma nova movimentação no banco de dados e atualiza o estoque do produto.
     */
    public Movimentacao criar(Movimentacao movimentacao) throws Exception {
        if (movimentacao == null) {
            throw new Exception("Movimentação não pode ser nula");
        }
        
        // Validar movimentação
        movimentacao.validarMovimentacao();
        
        Connection conn = null;
        boolean originalAutoCommit = true;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            // Verificar se a conexão está válida
            if (conn.isClosed()) {
                // Forçar nova conexão
                conn = DatabaseConnection.getInstance().getConnection();
            }
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false); // Iniciar transação
            
            // Aplicar movimentação no estoque do produto
            Produto produto = movimentacao.getProduto();
            // Buscar produto usando a mesma conexão da transação
            Produto produtoDoBanco = buscarProdutoPorNome(conn, produto.getNome());
            
            if (produtoDoBanco == null) {
                throw new Exception("Produto não encontrado: " + produto.getNome());
            }
            
            int novoEstoque;
            if (movimentacao.getTipo() == Movimentacao.TipoMovimentacao.ENTRADA) {
                novoEstoque = produtoDoBanco.getQuantidadeEstoque() + movimentacao.getQuantidade();
            } else {
                novoEstoque = produtoDoBanco.getQuantidadeEstoque() - movimentacao.getQuantidade();
                if (novoEstoque < 0) {
                    throw new Exception("Estoque insuficiente. Estoque atual: " + produtoDoBanco.getQuantidadeEstoque());
                }
            }
            
            // Atualizar estoque do produto
            String updateProdutoSQL = "UPDATE produtos SET estoque_atual = ? WHERE nome = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateProdutoSQL)) {
                updateStmt.setInt(1, novoEstoque);
                updateStmt.setString(2, produtoDoBanco.getNome());
                updateStmt.executeUpdate();
            }
            
            // Inserir movimentação
            LocalDateTime dataMovimentacao = movimentacao.getData();
            if (dataMovimentacao == null) {
                dataMovimentacao = LocalDateTime.now();
            }
            
            String insertMovSQL = "INSERT INTO movimentacoes (produto_nome, tipo, quantidade, data_movimentacao) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertMovSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, produtoDoBanco.getNome());
                insertStmt.setString(2, movimentacao.getTipo().toString());
                insertStmt.setInt(3, movimentacao.getQuantidade());
                insertStmt.setTimestamp(4, Timestamp.valueOf(dataMovimentacao));
                
                insertStmt.executeUpdate();
                
                // Obter ID gerado
                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Atualizar ID da movimentação se houver setter
                    }
                }
            }
            
            conn.commit(); // Confirmar transação
            return movimentacao;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Reverter transação em caso de erro
                } catch (SQLException rollbackEx) {
                    System.err.println("Erro ao fazer rollback: " + rollbackEx.getMessage());
                }
            }
            throw new Exception("Erro ao criar movimentação: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    // Restaurar auto-commit apenas se não houver erro
                    if (!conn.isClosed()) {
                        conn.setAutoCommit(originalAutoCommit);
                    }
                } catch (SQLException e) {
                    System.err.println("Erro ao restaurar auto-commit: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Busca um produto pelo nome usando uma conexão específica (para uso em transações).
     */
    private Produto buscarProdutoPorNome(Connection conn, String nome) throws SQLException {
        String sql = "SELECT p.nome, p.preco, p.unidade, p.estoque_atual, p.estoque_minimo, p.estoque_maximo, " +
                     "c.nome as categoria_nome, c.tamanho, c.embalagem " +
                     "FROM produtos p " +
                     "INNER JOIN categorias c ON p.categoria_nome = c.nome " +
                     "WHERE p.nome = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return criarProdutoDoResultSet(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Cria um objeto Produto a partir de um ResultSet.
     */
    private Produto criarProdutoDoResultSet(ResultSet rs) throws SQLException {
        String nome = rs.getString("nome");
        double preco = rs.getDouble("preco");
        String unidade = rs.getString("unidade");
        int estoqueAtual = rs.getInt("estoque_atual");
        int estoqueMinimo = rs.getInt("estoque_minimo");
        int estoqueMaximo = rs.getInt("estoque_maximo");
        
        // Criar categoria
        String categoriaNome = rs.getString("categoria_nome");
        String tamanhoStr = rs.getString("tamanho");
        String embalagemStr = rs.getString("embalagem");
        
        model.Categoria.Tamanho tamanho = model.Categoria.Tamanho.valueOf(tamanhoStr);
        model.Categoria.Embalagem embalagem = model.Categoria.Embalagem.valueOf(embalagemStr);
        model.Categoria categoria = new model.Categoria(categoriaNome, tamanho, embalagem);
        
        return new Produto(nome, preco, unidade, estoqueAtual, estoqueMinimo, estoqueMaximo, categoria);
    }
    
    /**
     * Lista todas as movimentações.
     */
    public List<Movimentacao> listarTodas() {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        String sql = "SELECT m.id, m.produto_nome, m.tipo, m.quantidade, m.data_movimentacao, " +
                     "p.preco, p.unidade, p.estoque_atual, p.estoque_minimo, p.estoque_maximo, " +
                     "c.nome as categoria_nome, c.tamanho, c.embalagem " +
                     "FROM movimentacoes m " +
                     "INNER JOIN produtos p ON m.produto_nome = p.nome " +
                     "INNER JOIN categorias c ON p.categoria_nome = c.nome " +
                     "ORDER BY m.data_movimentacao DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                movimentacoes.add(criarMovimentacaoDoResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar movimentações: " + e.getMessage());
        }
        
        return movimentacoes;
    }
    
    /**
     * Busca movimentações por produto.
     */
    public List<Movimentacao> buscarPorProduto(Produto produto) {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        String sql = "SELECT m.id, m.produto_nome, m.tipo, m.quantidade, m.data_movimentacao, " +
                     "p.preco, p.unidade, p.estoque_atual, p.estoque_minimo, p.estoque_maximo, " +
                     "c.nome as categoria_nome, c.tamanho, c.embalagem " +
                     "FROM movimentacoes m " +
                     "INNER JOIN produtos p ON m.produto_nome = p.nome " +
                     "INNER JOIN categorias c ON p.categoria_nome = c.nome " +
                     "WHERE m.produto_nome = ? " +
                     "ORDER BY m.data_movimentacao DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, produto.getNome());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimentacoes.add(criarMovimentacaoDoResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar movimentações por produto: " + e.getMessage());
        }
        
        return movimentacoes;
    }
    
    /**
     * Busca movimentações por tipo (ENTRADA ou SAIDA).
     */
    public List<Movimentacao> buscarPorTipo(Movimentacao.TipoMovimentacao tipo) {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        String sql = "SELECT m.id, m.produto_nome, m.tipo, m.quantidade, m.data_movimentacao, " +
                     "p.preco, p.unidade, p.estoque_atual, p.estoque_minimo, p.estoque_maximo, " +
                     "c.nome as categoria_nome, c.tamanho, c.embalagem " +
                     "FROM movimentacoes m " +
                     "INNER JOIN produtos p ON m.produto_nome = p.nome " +
                     "INNER JOIN categorias c ON p.categoria_nome = c.nome " +
                     "WHERE m.tipo = ? " +
                     "ORDER BY m.data_movimentacao DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimentacoes.add(criarMovimentacaoDoResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar movimentações por tipo: " + e.getMessage());
        }
        
        return movimentacoes;
    }
    
    /**
     * Busca apenas entradas de um produto.
     */
    public List<Movimentacao> buscarEntradasPorProduto(Produto produto) {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        String sql = "SELECT m.id, m.produto_nome, m.tipo, m.quantidade, m.data_movimentacao, " +
                     "p.preco, p.unidade, p.estoque_atual, p.estoque_minimo, p.estoque_maximo, " +
                     "c.nome as categoria_nome, c.tamanho, c.embalagem " +
                     "FROM movimentacoes m " +
                     "INNER JOIN produtos p ON m.produto_nome = p.nome " +
                     "INNER JOIN categorias c ON p.categoria_nome = c.nome " +
                     "WHERE m.produto_nome = ? AND m.tipo = 'ENTRADA' " +
                     "ORDER BY m.data_movimentacao DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, produto.getNome());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimentacoes.add(criarMovimentacaoDoResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar entradas por produto: " + e.getMessage());
        }
        
        return movimentacoes;
    }
    
    /**
     * Busca apenas saídas de um produto.
     */
    public List<Movimentacao> buscarSaidasPorProduto(Produto produto) {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        String sql = "SELECT m.id, m.produto_nome, m.tipo, m.quantidade, m.data_movimentacao, " +
                     "p.preco, p.unidade, p.estoque_atual, p.estoque_minimo, p.estoque_maximo, " +
                     "c.nome as categoria_nome, c.tamanho, c.embalagem " +
                     "FROM movimentacoes m " +
                     "INNER JOIN produtos p ON m.produto_nome = p.nome " +
                     "INNER JOIN categorias c ON p.categoria_nome = c.nome " +
                     "WHERE m.produto_nome = ? AND m.tipo = 'SAIDA' " +
                     "ORDER BY m.data_movimentacao DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, produto.getNome());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimentacoes.add(criarMovimentacaoDoResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar saídas por produto: " + e.getMessage());
        }
        
        return movimentacoes;
    }
    
    /**
     * Remove todas as movimentações.
     */
    public void limparTodos() {
        String sql = "DELETE FROM movimentacoes";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Erro ao limpar movimentações: " + e.getMessage());
        }
    }
    
    /**
     * Cria um objeto Movimentacao a partir de um ResultSet.
     */
    private Movimentacao criarMovimentacaoDoResultSet(ResultSet rs) throws SQLException {
        // Criar produto
        String produtoNome = rs.getString("produto_nome");
        double preco = rs.getDouble("preco");
        String unidade = rs.getString("unidade");
        int estoqueAtual = rs.getInt("estoque_atual");
        int estoqueMinimo = rs.getInt("estoque_minimo");
        int estoqueMaximo = rs.getInt("estoque_maximo");
        
        String categoriaNome = rs.getString("categoria_nome");
        String tamanhoStr = rs.getString("tamanho");
        String embalagemStr = rs.getString("embalagem");
        
        model.Categoria.Tamanho tamanho = model.Categoria.Tamanho.valueOf(tamanhoStr);
        model.Categoria.Embalagem embalagem = model.Categoria.Embalagem.valueOf(embalagemStr);
        model.Categoria categoria = new model.Categoria(categoriaNome, tamanho, embalagem);
        
        Produto produto = new Produto(produtoNome, preco, unidade, estoqueAtual, estoqueMinimo, estoqueMaximo, categoria);
        
        // Criar movimentação
        String tipoStr = rs.getString("tipo");
        Movimentacao.TipoMovimentacao tipo = Movimentacao.TipoMovimentacao.valueOf(tipoStr);
        int quantidade = rs.getInt("quantidade");
        
        // Obter data do banco ou usar data atual
        Timestamp timestamp = rs.getTimestamp("data_movimentacao");
        LocalDateTime dataMovimentacao = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();
        
        return new Movimentacao(produto, dataMovimentacao, quantidade, tipo);
    }
}
