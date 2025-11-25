package dao;

import model.Movimentacao;
import model.Produto;

import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {
    private static final List<Movimentacao> movimentacoes = new ArrayList<>();

    public Movimentacao cadastrar(Movimentacao movimentacao) throws Exception {
        if (movimentacao == null) {
            throw new Exception("Movimentação inválida");
        }
        
        movimentacao.validarMovimentacao();
        
        Produto produto = movimentacao.getProduto();
        if (movimentacao.getTipo() == Movimentacao.TipoMovimentacao.ENTRADA) {
            produto.setEstoqueAtual(produto.getEstoqueAtual() + movimentacao.getQuantidade());
        } else {
            produto.setEstoqueAtual(produto.getEstoqueAtual() - movimentacao.getQuantidade());
        }
        
        movimentacoes.add(movimentacao);
        return movimentacao;
    }

    public List<Movimentacao> listarTodas() {
        return new ArrayList<>(movimentacoes);
    }

    public List<Movimentacao> listarPorProduto(Produto produto) {
        List<Movimentacao> resultado = new ArrayList<>();
        for (Movimentacao movimentacao : movimentacoes) {
            if (movimentacao.getProduto() != null && movimentacao.getProduto().getNome().equalsIgnoreCase(produto.getNome())) {
                resultado.add(movimentacao);
            }
        }
        return resultado;
    }

    public List<Movimentacao> listarPorTipo(Movimentacao.TipoMovimentacao tipo) {
        List<Movimentacao> resultado = new ArrayList<>();
        for (Movimentacao movimentacao : movimentacoes) {
            if (movimentacao.getTipo() == tipo) {
                resultado.add(movimentacao);
            }
        }
        return resultado;
    }
}

