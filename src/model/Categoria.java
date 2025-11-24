package model;

public class Categoria {
    public enum Tamanho {
        PEQUENO,
        MEDIO,
        GRANDE
    }

    public enum Embalagem {
        LATA,
        VIDRO,
        PLASTICO
    }

    private String nome;
    private Tamanho tamanho;
    private Embalagem embalagem;

    public Categoria(String nome, Tamanho tamanho, Embalagem embalagem) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.embalagem = embalagem;
    }

    public String getNome() {
        return nome;
    }

    public Tamanho getTamanho() {
        return tamanho;
    }

    public Embalagem getEmbalagem() {
        return embalagem;
    }
}

