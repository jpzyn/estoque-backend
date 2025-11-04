public class Categoria {
    private String nome;
    private Tamanho tamanho;
    private Embalagem embalagem;

    public enum Tamanho {
        PEQUENO,
        MEDIO,
        GRANDE
    }

    public enum Embalagem {
        LATA,
        VIDRO,
        PLAASTICO,
    }

    // Construtor, getters e setters
}