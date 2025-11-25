package dao;

import model.Categoria;
import model.Produto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProdutoDAO {
    private static List<Produto> produtos = new ArrayList<>();
    private static int proximoId = 1;

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

    public Produto atualizar(Produto produtoAtualizado) throws Exception {
        if (produtoAtualizado == null) {
            throw new Exception("Produto não pode ser nulo");
        }
        
        Produto produtoExistente = buscarPorNome(produtoAtualizado.getNome());
        if (produtoExistente == null) {
            throw new Exception("Produto não encontrado");
        }
        
        produtoExistente.setPrecoUnitario(produtoAtualizado.getPrecoUnitario());
        produtoExistente.setUnidade(produtoAtualizado.getUnidade());
        produtoExistente.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
        produtoExistente.setQuantidadeMinima(produtoAtualizado.getQuantidadeMinima());
        produtoExistente.setQuantidadeMaxima(produtoAtualizado.getQuantidadeMaxima());
        produtoExistente.setCategoria(produtoAtualizado.getCategoria());
        
        return produtoExistente;
    }

    public boolean deletar(String nome) throws Exception {
        Produto produto = buscarPorNome(nome);
        if (produto == null) {
            throw new Exception("Produto não encontrado");
        }
        return produtos.remove(produto);
    }

    public List<Produto> buscarPorCategoria(Categoria categoria) {
        return produtos.stream()
                .filter(p -> p.getCategoria() != null && p.getCategoria().getNome().equals(categoria.getNome()))
                .collect(Collectors.toList());
    }

    public void limparTodos() {
        produtos.clear();
        proximoId = 1;
    }
}
