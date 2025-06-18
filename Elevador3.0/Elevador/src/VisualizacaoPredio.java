import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class VisualizacaoPredio extends JPanel {
    private Predio predio;
    private static final int LARGURA_ANDAR = 200;
    private static final int ALTURA_ANDAR = 60;
    private static final int LARGURA_ELEVADOR = 40;
    private static final int ALTURA_ELEVADOR = 50;
    private static final int MARGEM = 20;
    
    public VisualizacaoPredio(Predio predio) {
        this.predio = predio;
        setPreferredSize(new Dimension(LARGURA_ANDAR + 100, 400));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Visualização do Prédio"));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        desenharPredio(g2d);
        desenharElevadores(g2d);
        desenharPessoas(g2d);
    }
    
    private void desenharPredio(Graphics2D g2d) {
        Lista<Andar> andares = predio.getAndares();
        Ponteiro<Andar> pAndar = andares.getInicio();
        int y = getHeight() - MARGEM;
        int andarNum = 0;
        
        while (pAndar != null) {
            Andar andar = pAndar.getElemento();
            
            // Desenha o andar
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(MARGEM, y - ALTURA_ANDAR, LARGURA_ANDAR, ALTURA_ANDAR);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(MARGEM, y - ALTURA_ANDAR, LARGURA_ANDAR, ALTURA_ANDAR);
            
            // Número do andar
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("Andar " + andar.getNumero(), MARGEM + 5, y - ALTURA_ANDAR + 15);
            
            // Status do painel
            PainelExterno painel = andar.getPainelExterno();
            String statusPainel = painel.getStatus();
            Color corStatus = statusPainel.contains("Ativo") ? Color.RED : Color.GREEN;
            g2d.setColor(corStatus);
            g2d.fillOval(MARGEM + LARGURA_ANDAR - 20, y - ALTURA_ANDAR + 5, 15, 15);
            
            // Pessoas aguardando
            FilaPrior pessoasAguardando = andar.getPessoasAguardando();
            if (pessoasAguardando.tamanho() > 0) {
                g2d.setColor(Color.BLUE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.drawString(pessoasAguardando.tamanho() + " pessoas", MARGEM + 5, y - ALTURA_ANDAR + 30);
                
                // Desenha ícones das pessoas
                int xPessoa = MARGEM + 5;
                int yPessoa = y - ALTURA_ANDAR + 40;
                
                // Cadeirantes (prioridade 2)
                if (pessoasAguardando.temElementosNaPrioridade(2)) {
                    g2d.setColor(Color.RED);
                    g2d.fillOval(xPessoa, yPessoa, 8, 8);
                    xPessoa += 12;
                }
                
                // Idosos (prioridade 1)
                if (pessoasAguardando.temElementosNaPrioridade(1)) {
                    g2d.setColor(Color.ORANGE);
                    g2d.fillOval(xPessoa, yPessoa, 8, 8);
                    xPessoa += 12;
                }
                
                // Pessoas normais (prioridade 0)
                if (pessoasAguardando.temElementosNaPrioridade(0)) {
                    g2d.setColor(Color.BLUE);
                    g2d.fillOval(xPessoa, yPessoa, 8, 8);
                }
            }
            
            y -= (ALTURA_ANDAR + 5);
            pAndar = pAndar.getProximo();
            andarNum++;
        }
    }
    
    private void desenharElevadores(Graphics2D g2d) {
        Lista<Elevador> elevadores = predio.getCentral().getElevadores();
        Ponteiro<Elevador> pElev = elevadores.getInicio();
        int xElevador = MARGEM + LARGURA_ANDAR + 10;
        
        while (pElev != null) {
            Elevador elevador = pElev.getElemento();
            
            // Calcula a posição Y do elevador baseado no andar atual
            int yElevador = getHeight() - MARGEM - (elevador.getAndarAtual() * (ALTURA_ANDAR + 5)) - ALTURA_ELEVADOR;
            
            // Desenha o elevador
            g2d.setColor(Color.CYAN);
            g2d.fillRect(xElevador, yElevador, LARGURA_ELEVADOR, ALTURA_ELEVADOR);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(xElevador, yElevador, LARGURA_ELEVADOR, ALTURA_ELEVADOR);
            
            // ID do elevador
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("E" + elevador.getId(), xElevador + 5, yElevador + 15);
            
            // Pessoas dentro
            Fila<Pessoa> pessoasDentro = elevador.getPessoasDentro();
            if (pessoasDentro.tamanho() > 0) {
                g2d.setFont(new Font("Arial", Font.PLAIN, 8));
                g2d.drawString(pessoasDentro.tamanho() + "/5", xElevador + 5, yElevador + 25);
                
                // Desenha pessoas dentro do elevador
                int xPessoa = xElevador + 5;
                int yPessoa = yElevador + 30;
                
                for (int i = 0; i < Math.min(pessoasDentro.tamanho(), 3); i++) {
                    g2d.setColor(Color.BLUE);
                    g2d.fillOval(xPessoa, yPessoa, 6, 6);
                    xPessoa += 8;
                }
            }
            
            // Status de movimento
            if (!elevador.estaParado()) {
                g2d.setColor(Color.GREEN);
                g2d.fillOval(xElevador + LARGURA_ELEVADOR - 10, yElevador + 5, 8, 8);
            }
            
            xElevador += (LARGURA_ELEVADOR + 10);
            pElev = pElev.getProximo();
        }
    }
    
    private void desenharPessoas(Graphics2D g2d) {
        // Desenha legenda
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Legenda:", MARGEM, 20);
        
        // Cadeirantes
        g2d.setColor(Color.RED);
        g2d.fillOval(MARGEM, 25, 8, 8);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Cadeirantes", MARGEM + 12, 32);
        
        // Idosos
        g2d.setColor(Color.ORANGE);
        g2d.fillOval(MARGEM + 80, 25, 8, 8);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Idosos", MARGEM + 92, 32);
        
        // Pessoas normais
        g2d.setColor(Color.BLUE);
        g2d.fillOval(MARGEM + 140, 25, 8, 8);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Normais", MARGEM + 152, 32);
    }
    
    public void atualizar() {
        repaint();
    }
} 