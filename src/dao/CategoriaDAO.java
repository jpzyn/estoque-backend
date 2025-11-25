package dao;

import model.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    private static List<Categoria> categorias = new ArrayList<>();

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
        
        categorias.add(categoria);
        return categoria;
    }

    public Categoria buscarPorNome(String nome) {
        return categorias.stream()
                .filter(c -> c.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }

    public List<Categoria> listarTodas() {
        return new ArrayList<>(categorias);
    }

    public Categoria atualizar(Categoria categoriaAtualizada) throws Exception {
        if (categoriaAtualizada == null) {
            throw new Exception("Categoria não pode ser nula");
        }
        
        Categoria categoriaExistente = buscarPorNome(categoriaAtualizada.getNome());
        if (categoriaExistente == null) {
            throw new Exception("Categoria não encontrada");
        }
        
        categoriaExistente.setTamanho(categoriaAtualizada.getTamanho());
        categoriaExistente.setEmbalagem(categoriaAtualizada.getEmbalagem());
        
        return categoriaExistente;
    }

    public boolean deletar(String nome) throws Exception {
        Categoria categoria = buscarPorNome(nome);
        if (categoria == null) {
            throw new Exception("Categoria não encontrada");
        }
        return categorias.remove(categoria);
    }

    public void limparTodos() {
        categorias.clear();
    }
}
