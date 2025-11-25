package dao;

import model.Movimentacao;
import model.Produto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MovimentacaoDAO {
    private static List<Movimentacao> movimentacoes = new ArrayList<>();

    public Movimentacao criar(Movimentacao movimentacao) throws Exception {
        if (movimentacao == null) {
            throw new Exception("Movimentação não pode ser nula");
        }
        
        // Validar movimentação
        movimentacao.validarMovimentacao();
        
        // Aplicar movimentação no estoque do produto
        Produto produto = movimentacao.getProduto();
        if (movimentacao.getTipo() == Movimentacao.TipoMovimentacao.ENTRADA) {
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + movimentacao.getQuantidade());
        } else {
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - movimentacao.getQuantidade());
        }
        
        movimentacoes.add(movimentacao);
        return movimentacao;
    }

    public List<Movimentacao> listarTodas() {
        return new ArrayList<>(movimentacoes);
    }

    public List<Movimentacao> buscarPorProduto(Produto produto) {
        return movimentacoes.stream()
                .filter(m -> m.getProduto() != null && m.getProduto().getNome().equals(produto.getNome()))
                .collect(Collectors.toList());
    }

    public List<Movimentacao> buscarPorTipo(Movimentacao.TipoMovimentacao tipo) {
        return movimentacoes.stream()
                .filter(m -> m.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    public List<Movimentacao> buscarEntradasPorProduto(Produto produto) {
        return movimentacoes.stream()
                .filter(m -> m.getProduto() != null && 
                            m.getProduto().getNome().equals(produto.getNome()) &&
                            m.getTipo() == Movimentacao.TipoMovimentacao.ENTRADA)
                .collect(Collectors.toList());
    }

    public List<Movimentacao> buscarSaidasPorProduto(Produto produto) {
        return movimentacoes.stream()
                .filter(m -> m.getProduto() != null && 
                            m.getProduto().getNome().equals(produto.getNome()) &&
                            m.getTipo() == Movimentacao.TipoMovimentacao.SAIDA)
                .collect(Collectors.toList());
    }

    public void limparTodos() {
        movimentacoes.clear();
    }
}
