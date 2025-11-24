package dao;

import model.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    private static final List<Categoria> categorias = new ArrayList<>();

    public Categoria cadastrar(Categoria categoria) throws Exception {
        if (categoria == null || categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new Exception("Categoria inválida");
        }
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
}

