package dao;

import model.Categoria;
import model.Produto;

import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
    private static final List<Produto> produtos = new ArrayList<>();

    public Produto cadastrar(Produto produto) throws Exception {
        if (produto == null || produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new Exception("Produto inválido");
        }
        if (buscarPorNome(produto.getNome()) != null) {
            throw new Exception("Já existe um produto com este nome");
        }
        produtos.add(produto);
        return produto;
    }

    public Produto buscarPorNome(String nome) {
        return produtos.stream()
                .filter(p -> p.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos);
    }

    public List<Produto> listarPorCategoria(Categoria categoria) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto produto : produtos) {
            if (produto.getCategoria() != null && produto.getCategoria().getNome().equalsIgnoreCase(categoria.getNome())) {
                resultado.add(produto);
            }
        }
        return resultado;
    }
}

