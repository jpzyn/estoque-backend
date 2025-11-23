package view;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.List;

public class GraficoPanel extends JPanel {
    private List<DadoGrafico> dados;
    private String titulo;
    private String tipoGrafico; // "barras" ou "pizza"
    private Color[] cores = {
        new Color(66, 133, 244),   // Azul
        new Color(52, 168, 83),   // Verde
        new Color(251, 188, 4),   // Amarelo
        new Color(234, 67, 53),   // Vermelho
        new Color(156, 39, 176),  // Roxo
        new Color(255, 152, 0),   // Laranja
        new Color(0, 188, 212),   // Ciano
        new Color(121, 85, 72)    // Marrom
    };

    public static class DadoGrafico {
        String label;
        double valor;
        
        public DadoGrafico(String label, double valor) {
            this.label = label;
            this.valor = valor;
        }
    }

    public GraficoPanel(String titulo, String tipoGrafico) {
        this.titulo = titulo;
        this.tipoGrafico = tipoGrafico;
        this.dados = new ArrayList<>();
        setBackground(Color.WHITE);
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
        repaint();
    }

    public void setTipoGrafico(String tipoGrafico) {
        this.tipoGrafico = tipoGrafico;
        repaint();
    }

    public void setDados(List<DadoGrafico> dados) {
        this.dados = dados != null ? dados : new ArrayList<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (dados == null || dados.isEmpty()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.GRAY);
            g2d.drawString("Nenhum dado para exibir", getWidth() / 2 - 80, getHeight() / 2);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int largura = getWidth();
        int altura = getHeight();
        int margem = 40;
        int areaLargura = largura - 2 * margem;
        int areaAltura = altura - 2 * margem - 30;


        if (titulo != null && !titulo.isEmpty()) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(g2d.getFont().deriveFont(16f));
            int tituloX = (largura - g2d.getFontMetrics().stringWidth(titulo)) / 2;
            g2d.drawString(titulo, tituloX, 25);
        }

        if (tipoGrafico.equals("barras")) {
            desenharGraficoBarras(g2d, margem, 50, areaLargura, areaAltura);
        } else if (tipoGrafico.equals("pizza")) {
            desenharGraficoPizza(g2d, margem, 50, areaLargura, areaAltura);
        }
    }

    private void desenharGraficoBarras(Graphics2D g2d, int x, int y, int largura, int altura) {
        if (dados.isEmpty()) return;

        double maxValor = dados.stream().mapToDouble(d -> d.valor).max().orElse(1);
        if (maxValor == 0) maxValor = 1;

        int numBarras = dados.size();
        int larguraBarra = Math.max(20, (largura - (numBarras - 1) * 10) / numBarras);
        int espacamento = 10;


        g2d.setColor(Color.BLACK);
        g2d.drawLine(x, y + altura, x + largura, y + altura); // Eixo X
        g2d.drawLine(x, y, x, y + altura); // Eixo Y


        for (int i = 0; i < dados.size(); i++) {
            DadoGrafico dado = dados.get(i);
            int barraX = x + i * (larguraBarra + espacamento);
            int barraAltura = (int) ((dado.valor / maxValor) * altura);
            int barraY = y + altura - barraAltura;


            Color cor = cores[i % cores.length];
            g2d.setColor(cor);
            g2d.fillRect(barraX, barraY, larguraBarra, barraAltura);


            g2d.setColor(Color.BLACK);
            g2d.drawRect(barraX, barraY, larguraBarra, barraAltura);


            String label = dado.label.length() > 15 ? dado.label.substring(0, 12) + "..." : dado.label;
            int labelX = barraX + (larguraBarra - g2d.getFontMetrics().stringWidth(label)) / 2;
            g2d.setColor(Color.BLACK);
            g2d.setFont(g2d.getFont().deriveFont(10f));
            g2d.drawString(label, labelX, y + altura + 15);


            if (barraAltura > 15) {
                String valorStr = formatarValor(dado.valor);
                int valorX = barraX + (larguraBarra - g2d.getFontMetrics().stringWidth(valorStr)) / 2;
                g2d.setColor(Color.BLACK);
                g2d.setFont(g2d.getFont().deriveFont(9f));
                g2d.drawString(valorStr, valorX, barraY - 3);
            }
        }


        g2d.setColor(Color.GRAY);
        g2d.setFont(g2d.getFont().deriveFont(9f));
        for (int i = 0; i <= 5; i++) {
            int valorY = y + altura - (i * altura / 5);
            double valor = maxValor * (1 - (double) i / 5);
            String valorStr = formatarValor(valor);
            g2d.drawString(valorStr, x - g2d.getFontMetrics().stringWidth(valorStr) - 5, valorY + 4);
            g2d.drawLine(x - 5, valorY, x, valorY);
        }
    }

    private void desenharGraficoPizza(Graphics2D g2d, int x, int y, int largura, int altura) {
        if (dados.isEmpty()) return;

        double total = dados.stream().mapToDouble(d -> d.valor).sum();
        if (total == 0) return;

        int centroX = x + largura / 2;
        int centroY = y + altura / 2;

        int raio = Math.min(largura, altura) / 2 - 10;

        double anguloInicial = -90; // Começar do topo (0 graus no sistema de coordenadas)
        
        for (int i = 0; i < dados.size(); i++) {
            DadoGrafico dado = dados.get(i);
            double porcentagem = dado.valor / total;
            double angulo = porcentagem * 360;

            Color cor = cores[i % cores.length];
            g2d.setColor(cor);


            Arc2D.Double arco = new Arc2D.Double(
                centroX - raio, centroY - raio, 
                raio * 2, raio * 2, 
                anguloInicial, angulo, 
                Arc2D.PIE
            );
            g2d.fill(arco);
            g2d.setColor(Color.BLACK);
            g2d.draw(arco);


            if (angulo > 5) {
                double anguloMedio = Math.toRadians(anguloInicial + angulo / 2);
                int labelX = centroX + (int) ((raio * 0.7) * Math.cos(anguloMedio));
                int labelY = centroY - (int) ((raio * 0.7) * Math.sin(anguloMedio));
                
                String label = dado.label.length() > 12 ? dado.label.substring(0, 9) + "..." : dado.label;
                String porcentagemStr = String.format("%.1f%%", porcentagem * 100);
                g2d.setColor(Color.BLACK);
                g2d.setFont(g2d.getFont().deriveFont(9f));
                int labelWidth = g2d.getFontMetrics().stringWidth(label);
                g2d.drawString(label, labelX - labelWidth / 2, labelY);
                int porcentagemWidth = g2d.getFontMetrics().stringWidth(porcentagemStr);
                g2d.drawString(porcentagemStr, labelX - porcentagemWidth / 2, labelY + 11);
            }

            anguloInicial += angulo;
        }
    }

    private String formatarValor(double valor) {
        if (valor >= 1000000) {
            return String.format("%.1fM", valor / 1000000);
        } else if (valor >= 1000) {
            return String.format("%.1fK", valor / 1000);
        } else if (valor == (int) valor) {
            return String.valueOf((int) valor);
        } else {
            return String.format("%.2f", valor);
        }
    }
}

