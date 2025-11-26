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

    // Métodos alternativos para compatibilidade
    public double getPrecoUnitario() {
        return preco;
    }

    public void setPrecoUnitario(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return estoqueAtual;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.estoqueAtual = quantidadeEstoque;
    }

    public int getQuantidadeMinima() {
        return estoqueMinimo;
    }

    public void setQuantidadeMinima(int quantidadeMinima) {
        this.estoqueMinimo = quantidadeMinima;
    }

    public int getQuantidadeMaxima() {
        return estoqueMaximo;
    }

    public void setQuantidadeMaxima(int quantidadeMaxima) {
        this.estoqueMaximo = quantidadeMaxima;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }
}

