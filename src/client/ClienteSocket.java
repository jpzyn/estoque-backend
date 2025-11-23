package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

public class ClienteSocket {

    private final String host;
    private final int port;

    public ClienteSocket() {
        this("localhost", 12345);
    }

    public ClienteSocket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String enviar(String acao, Map<String, String> dados) throws IOException {
        String payload = montarPayload(acao, dados);
        Socket socket = new Socket(host, port);
        try {

            socket.setSoTimeout(5000); // 5 segundos
            
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            writer.println(payload);
            writer.flush();



            StringBuilder resposta = new StringBuilder();
            String linha;
            boolean primeiraLinha = true;
            
            try {
                while ((linha = reader.readLine()) != null) {

                    if (linha.isEmpty()) {
                        break;
                    }
                    if (!primeiraLinha) {
                        resposta.append(System.lineSeparator());
                    }
                    resposta.append(linha);
                    primeiraLinha = false;
                }
            } catch (java.net.SocketTimeoutException e) {

            }
            
            return resposta.toString();
        } finally {
            socket.close();
        }
    }

    private String montarPayload(String acao, Map<String, String> dados) {
        StringJoiner joiner = new StringJoiner(";");
        joiner.add("acao=" + acao);
        if (dados != null) {
            dados.forEach((chave, valor) -> joiner.add(chave + "=" + valor));
        }
        return joiner.toString();
    }
}

