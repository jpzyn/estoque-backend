package dao;

import model.Categoria;
import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para acesso aos dados de Categoria usando MySQL.
 * Substitui o armazenamento em memória (ArrayList) por banco de dados.
 */
public class CategoriaDAO {
    
    /**
     * Cria uma nova categoria no banco de dados.
     */
    public Categoria criar(Categoria categoria) throws Exception {
        if (categoria == null) {
            throw new Exception("Categoria não pode ser nula");
        }
        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new Exception("Nome da categoria é obrigatório");
        }
        
        // Verificar se já existe categoria com mesmo nome
        if (buscarPorNome(categoria.getNome()) != null) {
            throw new Exception("Já existe uma categoria com este nome");
        }
        
        String sql = "INSERT INTO categorias (nome, tamanho, embalagem) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getTamanho().toString());
            stmt.setString(3, categoria.getEmbalagem().toString());
            
            stmt.executeUpdate();
            return categoria;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao criar categoria: " + e.getMessage());
        }
    }
    
    /**
     * Busca uma categoria pelo nome.
     */
    public Categoria buscarPorNome(String nome) {
        String sql = "SELECT nome, tamanho, embalagem FROM categorias WHERE nome = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return criarCategoriaDoResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar categoria: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Lista todas as categorias.
     */
    public List<Categoria> listarTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT nome, tamanho, embalagem FROM categorias ORDER BY nome";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                categorias.add(criarCategoriaDoResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
        }
        
        return categorias;
    }
    
    /**
     * Atualiza uma categoria existente.
     */
    public Categoria atualizar(Categoria categoriaAtualizada) throws Exception {
        if (categoriaAtualizada == null) {
            throw new Exception("Categoria não pode ser nula");
        }
        
        Categoria categoriaExistente = buscarPorNome(categoriaAtualizada.getNome());
        if (categoriaExistente == null) {
            throw new Exception("Categoria não encontrada");
        }
        
        String sql = "UPDATE categorias SET tamanho = ?, embalagem = ? WHERE nome = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoriaAtualizada.getTamanho().toString());
            stmt.setString(2, categoriaAtualizada.getEmbalagem().toString());
            stmt.setString(3, categoriaAtualizada.getNome());
            
            stmt.executeUpdate();
            return categoriaAtualizada;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar categoria: " + e.getMessage());
        }
    }
    
    /**
     * Deleta uma categoria pelo nome.
     */
    public boolean deletar(String nome) throws Exception {
        Categoria categoria = buscarPorNome(nome);
        if (categoria == null) {
            throw new Exception("Categoria não encontrada");
        }
        
        String sql = "DELETE FROM categorias WHERE nome = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) { // Foreign key constraint
                throw new Exception("Não é possível deletar categoria: existem produtos associados");
            }
            throw new Exception("Erro ao deletar categoria: " + e.getMessage());
        }
    }
    
    /**
     * Remove todas as categorias (cuidado: também remove produtos e movimentações relacionados).
     */
    public void limparTodos() {
        String sql = "DELETE FROM categorias";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Erro ao limpar categorias: " + e.getMessage());
        }
    }
    
    /**
     * Cria um objeto Categoria a partir de um ResultSet.
     */
    private Categoria criarCategoriaDoResultSet(ResultSet rs) throws SQLException {
        String nome = rs.getString("nome");
        String tamanhoStr = rs.getString("tamanho");
        String embalagemStr = rs.getString("embalagem");
        
        Categoria.Tamanho tamanho = Categoria.Tamanho.valueOf(tamanhoStr);
        Categoria.Embalagem embalagem = Categoria.Embalagem.valueOf(embalagemStr);
        
        return new Categoria(nome, tamanho, embalagem);
    }
}
