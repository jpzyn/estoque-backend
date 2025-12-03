package dao;

import model.Categoria;
import model.Produto;
import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para acesso aos dados de Produto usando MySQL.
 * Substitui o armazenamento em memória (ArrayList) por banco de dados.
 */
public class ProdutoDAO {
    
    private final CategoriaDAO categoriaDAO;
    
    public ProdutoDAO() {
        this.categoriaDAO = new CategoriaDAO();
    }
    
    /**
     * Cria um novo produto no banco de dados.
     */
    public Produto criar(Produto produto) throws Exception {
        if (produto == null) {
            throw new Exception("Produto não pode ser nulo");
        }
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }
        
        // Verificar se já existe produto com mesmo nome
        if (buscarPorNome(produto.getNome()) != null) {
            throw new Exception("Já existe um produto com este nome");
        }
        
        // Verificar se a categoria existe
        if (produto.getCategoria() == null) {
            throw new Exception("Categoria é obrigatória");
        }
        
        Categoria categoriaExistente = categoriaDAO.buscarPorNome(produto.getCategoria().getNome());
        if (categoriaExistente == null) {
            throw new Exception("Categoria não encontrada: " + produto.getCategoria().getNome());
        }
        
        String sql = "INSERT INTO produtos (nome, preco, unidade, estoque_atual, estoque_minimo, estoque_maximo, categoria_nome) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPrecoUnitario());
            stmt.setString(3, produto.getUnidade());
            stmt.setInt(4, produto.getQuantidadeEstoque());
            stmt.setInt(5, produto.getQuantidadeMinima());
            stmt.setInt(6, produto.getQuantidadeMaxima());
            stmt.setString(7, categoriaExistente.getNome());
            
            stmt.executeUpdate();
            return produto;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao criar produto: " + e.getMessage());
        }
    }
    
    /**
     * Busca um produto pelo nome.
     */
    public Produto buscarPorNome(String nome) {
        String sql = "SELECT p.nome, p.preco, p.unidade, p.estoque_atual, p.estoque_minimo, p.estoque_maximo, " +
                     "c.nome as categoria_nome, c.tamanho, c.embalagem " +
                     "FROM produtos p " +
                     "INNER JOIN categorias c ON p.categoria_nome = c.nome " +
                     "WHERE p.nome = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return criarProdutoDoResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Lista todos os produtos.
     */
    public List<Produto> listarTodos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.nome, p.preco, p.unidade, p.estoque_atual, p.estoque_minimo, p.estoque_maximo, " +
                     "c.nome as categoria_nome, c.tamanho, c.embalagem " +
                     "FROM produtos p " +
                     "INNER JOIN categorias c ON p.categoria_nome = c.nome " +
                     "ORDER BY p.nome";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                produtos.add(criarProdutoDoResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
        }
        
        return produtos;
    }
    
    /**
     * Atualiza um produto existente.
     */
    public Produto atualizar(Produto produtoAtualizado) throws Exception {
        if (produtoAtualizado == null) {
            throw new Exception("Produto não pode ser nulo");
        }
        
        Produto produtoExistente = buscarPorNome(produtoAtualizado.getNome());
        if (produtoExistente == null) {
            throw new Exception("Produto não encontrado");
        }
        
        // Verificar se a categoria existe se foi alterada
        if (produtoAtualizado.getCategoria() != null) {
            Categoria categoriaExistente = categoriaDAO.buscarPorNome(produtoAtualizado.getCategoria().getNome());
            if (categoriaExistente == null) {
                throw new Exception("Categoria não encontrada: " + produtoAtualizado.getCategoria().getNome());
            }
        }
        
        String sql = "UPDATE produtos SET preco = ?, unidade = ?, estoque_atual = ?, " +
                     "estoque_minimo = ?, estoque_maximo = ?, categoria_nome = ? WHERE nome = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, produtoAtualizado.getPrecoUnitario());
            stmt.setString(2, produtoAtualizado.getUnidade());
            stmt.setInt(3, produtoAtualizado.getQuantidadeEstoque());
            stmt.setInt(4, produtoAtualizado.getQuantidadeMinima());
            stmt.setInt(5, produtoAtualizado.getQuantidadeMaxima());
            stmt.setString(6, produtoAtualizado.getCategoria().getNome());
            stmt.setString(7, produtoAtualizado.getNome());
            
            stmt.executeUpdate();
            return produtoAtualizado;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar produto: " + e.getMessage());
        }
    }
    
    /**
     * Deleta um produto pelo nome.
     */
    public boolean deletar(String nome) throws Exception {
        Produto produto = buscarPorNome(nome);
        if (produto == null) {
            throw new Exception("Produto não encontrado");
        }
        
        String sql = "DELETE FROM produtos WHERE nome = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) { // Foreign key constraint
                throw new Exception("Não é possível deletar produto: existem movimentações associadas");
            }
            throw new Exception("Erro ao deletar produto: " + e.getMessage());
        }
    }
    
    /**
     * Busca produtos por categoria.
     */
    public List<Produto> buscarPorCategoria(Categoria categoria) {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.nome, p.preco, p.unidade, p.estoque_atual, p.estoque_minimo, p.estoque_maximo, " +
                     "c.nome as categoria_nome, c.tamanho, c.embalagem " +
                     "FROM produtos p " +
                     "INNER JOIN categorias c ON p.categoria_nome = c.nome " +
                     "WHERE c.nome = ? " +
                     "ORDER BY p.nome";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNome());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(criarProdutoDoResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produtos por categoria: " + e.getMessage());
        }
        
        return produtos;
    }
    
    /**
     * Remove todos os produtos.
     */
    public void limparTodos() {
        String sql = "DELETE FROM produtos";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Erro ao limpar produtos: " + e.getMessage());
        }
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
        
        Categoria.Tamanho tamanho = Categoria.Tamanho.valueOf(tamanhoStr);
        Categoria.Embalagem embalagem = Categoria.Embalagem.valueOf(embalagemStr);
        Categoria categoria = new Categoria(categoriaNome, tamanho, embalagem);
        
        return new Produto(nome, preco, unidade, estoqueAtual, estoqueMinimo, estoqueMaximo, categoria);
    }
}
