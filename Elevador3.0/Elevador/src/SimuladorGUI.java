import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class SimuladorGUI extends JFrame {
    private Simulador simulador;
    private Timer timer;
    private boolean emExecucao = false;
    
    // Componentes da interface
    private JLabel lblTempo;
    private JLabel lblStatus;
    private JButton btnIniciar, btnPausar, btnContinuar, btnParar;
    private JPanel painelElevadores;
    private JPanel painelAndares;
    private JTextArea txtLog;
    private JScrollPane scrollLog;
    private JPanel painelEstatisticas;
    private VisualizacaoPredio visualizacaoPredio;
    
    // Dados para atualização
    private int minutoAtual = 0;
    private Predio predio;
    
    public SimuladorGUI(int andares, int elevadores, int velocidadeMs) {
        this.simulador = new Simulador(andares, elevadores, velocidadeMs);
        this.predio = simulador.getPredio();
        
        configurarJanela();
        criarComponentes();
        organizarLayout();
        configurarEventos();
    }
    
    private void configurarJanela() {
        setTitle("Simulador de Elevador - Visualização em Tempo Real");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    private void criarComponentes() {
        // Painel de controle
        JPanel painelControle = new JPanel();
        painelControle.setBorder(BorderFactory.createTitledBorder("Controles"));
        painelControle.setLayout(new FlowLayout());
        
        lblTempo = new JLabel("Tempo: 00:00");
        lblTempo.setFont(new Font("Arial", Font.BOLD, 16));
        
        lblStatus = new JLabel("Status: Parado");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setForeground(Color.RED);
        
        btnIniciar = new JButton("▶ Iniciar");
        btnPausar = new JButton("⏸ Pausar");
        btnContinuar = new JButton("⏯ Continuar");
        btnParar = new JButton("⏹ Parar");
        
        btnPausar.setEnabled(false);
        btnContinuar.setEnabled(false);
        
        painelControle.add(lblTempo);
        painelControle.add(lblStatus);
        painelControle.add(btnIniciar);
        painelControle.add(btnPausar);
        painelControle.add(btnContinuar);
        painelControle.add(btnParar);
        
        // Visualização do prédio
        visualizacaoPredio = new VisualizacaoPredio(predio);
        int numAndares = predio.getAndares().tamanho();
        int alturaPainel = Math.max(500, numAndares * 70); // 70px por andar, mínimo 500px
        visualizacaoPredio.setPreferredSize(new Dimension(350, alturaPainel));
        
        // Painel de elevadores
        painelElevadores = new JPanel();
        painelElevadores.setBorder(BorderFactory.createTitledBorder("Status dos Elevadores"));
        painelElevadores.setLayout(new BoxLayout(painelElevadores, BoxLayout.Y_AXIS));
        
        // Painel de andares
        painelAndares = new JPanel();
        painelAndares.setBorder(BorderFactory.createTitledBorder("Estado dos Andares"));
        painelAndares.setLayout(new BoxLayout(painelAndares, BoxLayout.Y_AXIS));
        
        // Área de log
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Log de Eventos"));
        scrollLog.setPreferredSize(new Dimension(400, 200));
        
        // Painel de estatísticas
        painelEstatisticas = new JPanel();
        painelEstatisticas.setBorder(BorderFactory.createTitledBorder("Estatísticas"));
        painelEstatisticas.setLayout(new GridLayout(0, 2, 10, 5));
        painelEstatisticas.setPreferredSize(new Dimension(400, 80));
        painelEstatisticas.setBackground(new Color(240, 248, 255)); // Cor de fundo azul claro
        
        atualizarPainelElevadores();
        atualizarPainelAndares();
        atualizarEstatisticas();
    }
    
    private void organizarLayout() {
        setLayout(new BorderLayout());
        
        // Painel superior com controles
        add(createTopPanel(), BorderLayout.NORTH);
        
        // Painel central com visualização
        JSplitPane splitPanePrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Painel esquerdo - Visualização do prédio
        JPanel painelEsquerdo = new JPanel(new BorderLayout());
        JScrollPane scrollPredio = new JScrollPane(visualizacaoPredio);
        scrollPredio.setBorder(BorderFactory.createTitledBorder("Visualização do Prédio"));
        painelEsquerdo.add(scrollPredio, BorderLayout.CENTER);
        
        // Painel direito - Informações detalhadas
        JSplitPane splitPaneDireito = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // Painel superior direito - Elevadores, Andares e Estatísticas
        JPanel painelSuperiorDireito = new JPanel(new BorderLayout());
        painelSuperiorDireito.add(painelElevadores, BorderLayout.NORTH);
        painelSuperiorDireito.add(painelAndares, BorderLayout.CENTER);
        painelSuperiorDireito.add(painelEstatisticas, BorderLayout.SOUTH);
        
        // Painel inferior direito - Apenas Log
        JPanel painelInferiorDireito = new JPanel(new BorderLayout());
        painelInferiorDireito.add(scrollLog, BorderLayout.CENTER);
        
        splitPaneDireito.setTopComponent(painelSuperiorDireito);
        splitPaneDireito.setBottomComponent(painelInferiorDireito);
        splitPaneDireito.setDividerLocation(400);
        
        splitPanePrincipal.setLeftComponent(painelEsquerdo);
        splitPanePrincipal.setRightComponent(splitPaneDireito);
        splitPanePrincipal.setDividerLocation(500);
        
        add(splitPanePrincipal, BorderLayout.CENTER);
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Painel de controle principal
        JPanel painelControle = new JPanel();
        painelControle.setBorder(BorderFactory.createTitledBorder("Controles"));
        painelControle.setLayout(new FlowLayout());
        
        lblTempo = new JLabel("Tempo: 00:00");
        lblTempo.setFont(new Font("Arial", Font.BOLD, 16));
        
        lblStatus = new JLabel("Status: Parado");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setForeground(Color.RED);
        
        btnIniciar = new JButton("▶ Iniciar");
        btnPausar = new JButton("⏸ Pausar");
        btnContinuar = new JButton("⏯ Continuar");
        btnParar = new JButton("⏹ Parar");
        
        btnPausar.setEnabled(false);
        btnContinuar.setEnabled(false);
        
        painelControle.add(lblTempo);
        painelControle.add(lblStatus);
        painelControle.add(btnIniciar);
        painelControle.add(btnPausar);
        painelControle.add(btnContinuar);
        painelControle.add(btnParar);
        
        topPanel.add(painelControle, BorderLayout.CENTER);
        return topPanel;
    }
    
    private void configurarEventos() {
        btnIniciar.addActionListener(e -> iniciarSimulacao());
        btnPausar.addActionListener(e -> pausarSimulacao());
        btnContinuar.addActionListener(e -> continuarSimulacao());
        btnParar.addActionListener(e -> pararSimulacao());
    }
    
    private void iniciarSimulacao() {
        if (!emExecucao) {
            emExecucao = true;
            minutoAtual = 0;
            
            btnIniciar.setEnabled(false);
            btnPausar.setEnabled(true);
            btnContinuar.setEnabled(false);
            btnParar.setEnabled(true);
            lblStatus.setText("Status: Executando");
            lblStatus.setForeground(Color.GREEN);
            
            // Inicia o timer para atualização da interface
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        if (minutoAtual >= 1440) { // 24 horas
                            pararSimulacao();
                            return;
                        }
                        
                        // Atualiza o simulador
                        predio.atualizar(minutoAtual);
                        
                        // Atualiza a interface
                        atualizarInterface();
                        
                        minutoAtual++;
                    });
                }
            }, 0, simulador.getVelocidadeEmMs());
            
            adicionarLog("Simulação iniciada");
        }
    }
    
    private void pausarSimulacao() {
        if (emExecucao) {
            emExecucao = false;
            if (timer != null) {
                timer.cancel();
            }
            
            btnIniciar.setEnabled(false);
            btnPausar.setEnabled(false);
            btnContinuar.setEnabled(true);
            btnParar.setEnabled(true);
            lblStatus.setText("Status: Pausado");
            lblStatus.setForeground(Color.ORANGE);
            
            adicionarLog("Simulação pausada");
        }
    }
    
    private void continuarSimulacao() {
        if (!emExecucao) {
            emExecucao = true;
            
            btnIniciar.setEnabled(false);
            btnPausar.setEnabled(true);
            btnContinuar.setEnabled(false);
            btnParar.setEnabled(true);
            lblStatus.setText("Status: Executando");
            lblStatus.setForeground(Color.GREEN);
            
            // Reinicia o timer
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        if (minutoAtual >= 1440) {
                            pararSimulacao();
                            return;
                        }
                        
                        predio.atualizar(minutoAtual);
                        atualizarInterface();
                        minutoAtual++;
                    });
                }
            }, 0, simulador.getVelocidadeEmMs());
            
            adicionarLog("Simulação retomada");
        }
    }
    
    private void pararSimulacao() {
        emExecucao = false;
        if (timer != null) {
            timer.cancel();
        }
        
        btnIniciar.setEnabled(true);
        btnPausar.setEnabled(false);
        btnContinuar.setEnabled(false);
        btnParar.setEnabled(false);
        lblStatus.setText("Status: Parado");
        lblStatus.setForeground(Color.RED);
        
        adicionarLog("Simulação parada");
    }
    
    private void atualizarInterface() {
        // Atualiza o tempo
        int hora = (minutoAtual / 60) % 24;
        int minutos = minutoAtual % 60;
        lblTempo.setText(String.format("Tempo: %02d:%02d", hora, minutos));
        
        // Atualiza os painéis
        atualizarPainelElevadores();
        atualizarPainelAndares();
        atualizarEstatisticas();
        visualizacaoPredio.atualizar();
    }
    
    private void atualizarPainelElevadores() {
        painelElevadores.removeAll();
        
        Lista<Elevador> elevadores = predio.getCentral().getElevadores();
        Ponteiro<Elevador> pElev = elevadores.getInicio();
        
        while (pElev != null) {
            Elevador elevador = pElev.getElemento();
            
            JPanel painelElevador = new JPanel();
            painelElevador.setBorder(BorderFactory.createTitledBorder("Elevador " + elevador.getId()));
            painelElevador.setLayout(new GridLayout(0, 1, 5, 5));
            
            // Andar atual
            JLabel lblAndar = new JLabel("Andar: " + elevador.getAndarAtual());
            lblAndar.setFont(new Font("Arial", Font.BOLD, 14));
            painelElevador.add(lblAndar);
            
            // Pessoas dentro
            Fila<Pessoa> pessoasDentro = elevador.getPessoasDentro();
            JLabel lblPessoas = new JLabel("Pessoas: " + pessoasDentro.tamanho() + "/5");
            painelElevador.add(lblPessoas);
            
            // Destinos
            Lista<Integer> destinos = elevador.getDestinos();
            if (!destinos.estaVazia()) {
                JLabel lblDestinos = new JLabel("Destinos: " + destinos.toString());
                painelElevador.add(lblDestinos);
            }
            
            // Status de movimento
            String status = elevador.estaParado() ? "Parado" : (elevador.getAndarAtual() > 0 ? "Movendo" : "Parado");
            JLabel lblStatus = new JLabel("Status: " + status);
            lblStatus.setForeground(elevador.estaParado() ? Color.GREEN : Color.BLUE);
            painelElevador.add(lblStatus);
            
            painelElevadores.add(painelElevador);
            pElev = pElev.getProximo();
        }
        
        painelElevadores.revalidate();
        painelElevadores.repaint();
    }
    
    private void atualizarPainelAndares() {
        painelAndares.removeAll();
        
        Lista<Andar> andares = predio.getAndares();
        Ponteiro<Andar> pAndar = andares.getInicio();
        
        while (pAndar != null) {
            Andar andar = pAndar.getElemento();
            
            JPanel painelAndar = new JPanel();
            painelAndar.setBorder(BorderFactory.createTitledBorder("Andar " + andar.getNumero()));
            painelAndar.setLayout(new GridLayout(0, 1, 5, 5));
            
            // Painel externo
            PainelExterno painel = andar.getPainelExterno();
            JLabel lblPainel = new JLabel("Painel: " + painel.getTipoPainel() + " - " + painel.getStatus());
            painelAndar.add(lblPainel);
            
            // Pessoas aguardando
            FilaPrior pessoasAguardando = andar.getPessoasAguardando();
            JLabel lblAguardando = new JLabel("Aguardando: " + pessoasAguardando.tamanho() + " pessoas");
            painelAndar.add(lblAguardando);
            
            // Detalhes das pessoas
            if (pessoasAguardando.tamanho() > 0) {
                StringBuilder detalhes = new StringBuilder("Detalhes: ");
                if (pessoasAguardando.temElementosNaPrioridade(2)) {
                    detalhes.append("♿ ");
                }
                if (pessoasAguardando.temElementosNaPrioridade(1)) {
                    detalhes.append("👴 ");
                }
                if (pessoasAguardando.temElementosNaPrioridade(0)) {
                    detalhes.append("👤 ");
                }
                JLabel lblDetalhes = new JLabel(detalhes.toString());
                painelAndar.add(lblDetalhes);
            }
            
            // Elevadores neste andar
            Lista<Elevador> elevadores = predio.getCentral().getElevadores();
            Ponteiro<Elevador> pElev = elevadores.getInicio();
            while (pElev != null) {
                if (pElev.getElemento().getAndarAtual() == andar.getNumero()) {
                    JLabel lblElevador = new JLabel("🛗 Elevador " + pElev.getElemento().getId() + " aqui");
                    lblElevador.setForeground(Color.BLUE);
                    lblElevador.setFont(new Font("Arial", Font.BOLD, 12));
                    painelAndar.add(lblElevador);
                    break;
                }
                pElev = pElev.getProximo();
            }
            
            painelAndares.add(painelAndar);
            pAndar = pAndar.getProximo();
        }
        
        painelAndares.revalidate();
        painelAndares.repaint();
    }
    
    private void atualizarEstatisticas() {
        painelEstatisticas.removeAll();
        
        Heuristica heuristica = predio.getCentral().getHeuristica();
        
        // Estatísticas gerais
        JLabel lblTotal = new JLabel("📊 Total transportado: " + heuristica.getTotalPessoasTransportadas());
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setForeground(new Color(0, 100, 0)); // Verde escuro
        
        // Estatísticas por elevador
        Lista<Elevador> elevadores = predio.getCentral().getElevadores();
        Ponteiro<Elevador> pElev = elevadores.getInicio();
        
        while (pElev != null) {
            Elevador elevador = pElev.getElemento();
            JLabel lblElev = new JLabel("🛗 Elevador " + elevador.getId() + ": " + elevador.getTotalPessoasTransportadas());
            lblElev.setFont(new Font("Arial", Font.BOLD, 12));
            lblElev.setForeground(new Color(0, 0, 150)); // Azul escuro
            painelEstatisticas.add(lblElev);
            pElev = pElev.getProximo();
        }
        
        painelEstatisticas.add(lblTotal);
        
        painelEstatisticas.revalidate();
        painelEstatisticas.repaint();
    }
    
    private void adicionarLog(String mensagem) {
        int hora = (minutoAtual / 60) % 24;
        int minutos = minutoAtual % 60;
        String timestamp = String.format("[%02d:%02d]", hora, minutos);
        
        txtLog.append(timestamp + " " + mensagem + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimuladorGUI gui = new SimuladorGUI(5, 2, 1000); // 5 andares, 2 elevadores, 1 segundo por ciclo
            gui.setVisible(true);
        });
    }
} 