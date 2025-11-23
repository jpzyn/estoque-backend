package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.ClienteSocket;

public class EstoqueController {

    private final ClienteSocket clienteSocket;

    public EstoqueController() {
        this(new ClienteSocket());
    }

    public EstoqueController(ClienteSocket clienteSocket) {
        this.clienteSocket = clienteSocket;
    }

    public String cadastrarProduto(String nome, String categoria, int estoqueInicial, int estoqueMinimo, int estoqueMaximo, double preco, String unidade) {
        Map<String, String> dados = new HashMap<>();
        dados.put("nome", nome);
        dados.put("categoria", categoria);
        dados.put("estoqueInicial", String.valueOf(estoqueInicial));
        dados.put("estoqueMinimo", String.valueOf(estoqueMinimo));
        dados.put("estoqueMaximo", String.valueOf(estoqueMaximo));
        dados.put("preco", String.valueOf(preco));
        dados.put("unidade", unidade);
        return enviar("cadastrarProduto", dados);
    }

    public List<String> listarProdutos() {
        return enviarComoLista("listarProdutos", Collections.emptyMap());
    }

    public List<String> listarCategorias() {
        return enviarComoLista("listarCategorias", Collections.emptyMap());
    }

    private String enviar(String acao, Map<String, String> dados) {
        try {
            return clienteSocket.enviar(acao, dados);
        } catch (IOException e) {
            return "Falha na comunicação com o servidor: " + e.getMessage();
        }
    }

    private List<String> enviarComoLista(String acao, Map<String, String> dados) {
        String resposta = enviar(acao, dados);
        if (resposta == null || resposta.isBlank()) {
            return new ArrayList<>();
        }

        if (resposta.trim().startsWith("ERROR") || resposta.trim().startsWith("Falha na comunicação")) {
            return new ArrayList<>();
        }

        String[] linhas = resposta.split("\\r?\\n");
        List<String> resultado = new ArrayList<>();
        for (String linha : linhas) {
            if (linha != null && !linha.trim().isEmpty()) {
                resultado.add(linha.trim());
            }
        }
        return resultado;
    }
}

