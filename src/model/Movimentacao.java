package model;

import java.time.LocalDateTime;

public class Movimentacao {
    private Produto produto;
    private LocalDateTime data;
    private int quantidade;
    private TipoMovimentacao tipo;

    public enum TipoMovimentacao {
        ENTRADA,
        SAIDA
    }

    public Movimentacao() {
        this.data = LocalDateTime.now();
    }

    public Movimentacao(Produto produto, LocalDateTime data, int quantidade, TipoMovimentacao tipo) {
        this.produto = produto;
        this.data = data == null ? LocalDateTime.now() : data;
        this.quantidade = quantidade;
        this.tipo = tipo;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public void validarMovimentacao() throws Exception {
        if (produto == null) {
            throw new Exception("Produto inválido na movimentação.");
        }

        if (tipo == TipoMovimentacao.SAIDA) {
            int novaQuantidade = produto.getEstoqueAtual() - quantidade;
            if (novaQuantidade < 0) {
                throw new Exception("Estoque insuficiente. Estoque atual: " + produto.getEstoqueAtual());
            }
        } else {
            int novaQuantidade = produto.getEstoqueAtual() + quantidade;
            if (novaQuantidade > produto.getEstoqueMaximo()) {
                throw new Exception("Atenção: Excede a capacidade máxima de estoque! Máximo permitido: " + produto.getEstoqueMaximo());
            }
        }
    }

    @Override
    public String toString() {
        return "Movimentacao{" +
                "produto=" + (produto != null ? produto.getNome() : "null") +
                ", data=" + data +
                ", quantidade=" + quantidade +
                ", tipo=" + tipo +
                '}';
    }
}