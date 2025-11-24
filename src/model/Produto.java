package model;

public class Produto {
    private String nome;
    private double preco;
    private String unidade;
    private int estoqueAtual;
    private int estoqueMinimo;
    private int estoqueMaximo;
    private Categoria categoria;

    public Produto(String nome,
                   double preco,
                   String unidade,
                   int estoqueAtual,
                   int estoqueMinimo,
                   int estoqueMaximo,
                   Categoria categoria) {
        this.nome = nome;
        this.preco = preco;
        this.unidade = unidade;
        this.estoqueAtual = estoqueAtual;
        this.estoqueMinimo = estoqueMinimo;
        this.estoqueMaximo = estoqueMaximo;
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public String getUnidade() {
        return unidade;
    }

    public int getEstoqueAtual() {
        return estoqueAtual;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public int getEstoqueMaximo() {
        return estoqueMaximo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setEstoqueAtual(int estoqueAtual) {
        this.estoqueAtual = estoqueAtual;
    }
}

