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
    private JLabel lblTempo;
    private JLabel lblStatus;
    private JButton btnIniciar, btnPausar, btnContinuar, btnParar;
    private JPanel painelElevadores;
    private JPanel painelAndares;
    private JTextArea txtLog;
    private JScrollPane scrollLog;
    private JPanel painelEstatisticas;
    private VisualizacaoPredio visualizacaoPredio;
    private JCheckBox chkHorarioPico;
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
        chkHorarioPico = new JCheckBox("Horário de Pico");
        chkHorarioPico.setFont(new Font("Arial", Font.BOLD, 14));
        chkHorarioPico.addActionListener(e -> {
            boolean pico = chkHorarioPico.isSelected();
            Lista<Elevador> elevadores = predio.getCentral().getElevadores();
            Ponteiro<Elevador> pElev = elevadores.getInicio();
            while (pElev != null) {
                pElev.getElemento().setHorarioPico(pico);
                pElev = pElev.getProximo();
            }
        });
        painelControle.add(lblTempo);
        painelControle.add(lblStatus);
        painelControle.add(btnIniciar);
        painelControle.add(btnPausar);
        painelControle.add(btnContinuar);
        painelControle.add(btnParar);
        painelControle.add(chkHorarioPico);
        visualizacaoPredio = new VisualizacaoPredio(predio);
        int numAndares = predio.getAndares().tamanho();
        int alturaPainel = Math.max(500, numAndares * 70);
        visualizacaoPredio.setPreferredSize(new Dimension(350, alturaPainel));
        painelElevadores = new JPanel();
        painelElevadores.setBorder(BorderFactory.createTitledBorder("Status dos Elevadores"));
        painelElevadores.setLayout(new BoxLayout(painelElevadores, BoxLayout.Y_AXIS));
        painelAndares = new JPanel();
        painelAndares.setBorder(BorderFactory.createTitledBorder("Estado dos Andares"));
        painelAndares.setLayout(new BoxLayout(painelAndares, BoxLayout.Y_AXIS));
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 10));
        scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Log de Eventos"));
        scrollLog.setPreferredSize(new Dimension(400, 80));
        scrollLog.setMinimumSize(new Dimension(400, 60));
        painelEstatisticas = new JPanel();
        painelEstatisticas.setBorder(BorderFactory.createTitledBorder("Estatísticas"));
        painelEstatisticas.setLayout(new BoxLayout(painelEstatisticas, BoxLayout.Y_AXIS));
        painelEstatisticas.setPreferredSize(new Dimension(400, 150));
        painelEstatisticas.setMinimumSize(new Dimension(400, 150));
        painelEstatisticas.setBackground(new Color(240, 248, 255));
        painelEstatisticas.setOpaque(true);
        atualizarPainelElevadores();
        atualizarPainelAndares();
        atualizarEstatisticas();
    }
    private void organizarLayout() {
        setLayout(new BorderLayout());
        add(createTopPanel(), BorderLayout.NORTH);
        JSplitPane splitPanePrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JPanel painelEsquerdo = new JPanel(new BorderLayout());
        JScrollPane scrollPredio = new JScrollPane(visualizacaoPredio);
        scrollPredio.setBorder(BorderFactory.createTitledBorder("Visualização do Prédio"));
        painelEsquerdo.add(scrollPredio, BorderLayout.CENTER);
        JPanel painelDireito = new JPanel(new BorderLayout());
        JPanel painelEstatisticasContainer = new JPanel(new BorderLayout());
        painelEstatisticasContainer.setBorder(BorderFactory.createTitledBorder("📊 Estatísticas em Tempo Real"));
        painelEstatisticasContainer.setBackground(new Color(240, 248, 255));
        painelEstatisticasContainer.add(painelEstatisticas, BorderLayout.CENTER);
        painelEstatisticasContainer.setPreferredSize(new Dimension(400, 150));
        painelEstatisticasContainer.setMinimumSize(new Dimension(400, 150));
        JSplitPane splitPaneCentral = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JScrollPane scrollElevadores = new JScrollPane(painelElevadores);
        scrollElevadores.setBorder(BorderFactory.createTitledBorder("Status dos Elevadores"));
        scrollElevadores.setPreferredSize(new Dimension(400, 250));
        JScrollPane scrollAndares = new JScrollPane(painelAndares);
        scrollAndares.setBorder(BorderFactory.createTitledBorder("Estado dos Andares"));
        scrollAndares.setPreferredSize(new Dimension(400, 250));
        splitPaneCentral.setTopComponent(scrollElevadores);
        splitPaneCentral.setBottomComponent(scrollAndares);
        splitPaneCentral.setDividerLocation(250);
        JPanel painelInferiorDireito = new JPanel(new BorderLayout());
        painelInferiorDireito.setPreferredSize(new Dimension(400, 100));
        painelInferiorDireito.setMinimumSize(new Dimension(400, 80));
        painelInferiorDireito.add(scrollLog, BorderLayout.CENTER);
        painelDireito.add(painelEstatisticasContainer, BorderLayout.NORTH);
        painelDireito.add(splitPaneCentral, BorderLayout.CENTER);
        painelDireito.add(painelInferiorDireito, BorderLayout.SOUTH);
        splitPanePrincipal.setLeftComponent(painelEsquerdo);
        splitPanePrincipal.setRightComponent(painelDireito);
        splitPanePrincipal.setDividerLocation(500);
        add(splitPanePrincipal, BorderLayout.CENTER);
    }
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
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
        chkHorarioPico = new JCheckBox("Horário de Pico");
        chkHorarioPico.setFont(new Font("Arial", Font.BOLD, 14));
        chkHorarioPico.addActionListener(e -> {
            boolean pico = chkHorarioPico.isSelected();
            Lista<Elevador> elevadores = predio.getCentral().getElevadores();
            Ponteiro<Elevador> pElev = elevadores.getInicio();
            while (pElev != null) {
                pElev.getElemento().setHorarioPico(pico);
                pElev = pElev.getProximo();
            }
        });
        painelControle.add(lblTempo);
        painelControle.add(lblStatus);
        painelControle.add(btnIniciar);
        painelControle.add(btnPausar);
        painelControle.add(btnContinuar);
        painelControle.add(btnParar);
        painelControle.add(chkHorarioPico);
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
        int hora = (minutoAtual / 60) % 24;
        int minutos = minutoAtual % 60;
        lblTempo.setText(String.format("Tempo: %02d:%02d", hora, minutos));
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
            JLabel lblAndar = new JLabel("Andar: " + elevador.getAndarAtual());
            lblAndar.setFont(new Font("Arial", Font.BOLD, 14));
            painelElevador.add(lblAndar);
            Fila<Pessoa> pessoasDentro = elevador.getPessoasDentro();
            JLabel lblPessoas = new JLabel("Pessoas: " + pessoasDentro.tamanho() + "/5");
            painelElevador.add(lblPessoas);
            Lista<Integer> destinos = elevador.getDestinos();
            if (!destinos.estaVazia()) {
                JLabel lblDestinos = new JLabel("Destinos: " + destinos.toString());
                painelElevador.add(lblDestinos);
            }
            String status = elevador.estaParado() ? "Parado" : (elevador.getAndarAtual() > 0 ? "Movendo" : "Parado");
            JLabel lblStatus = new JLabel("Status: " + status);
            lblStatus.setForeground(elevador.estaParado() ? Color.GREEN : Color.BLUE);
            painelElevador.add(lblStatus);
            JLabel lblTempoViagem = new JLabel("Tempo restante: " + elevador.getTempoRestanteViagem() + " min");
            lblTempoViagem.setFont(new Font("Arial", Font.ITALIC, 12));
            painelElevador.add(lblTempoViagem);
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
            PainelExterno painel = andar.getPainelExterno();
            JLabel lblPainel = new JLabel("Painel: " + painel.getTipoPainel() + " - " + painel.getStatus());
            painelAndar.add(lblPainel);
            FilaPrior pessoasAguardando = andar.getPessoasAguardando();
            JLabel lblAguardando = new JLabel("Aguardando: " + pessoasAguardando.tamanho() + " pessoas");
            painelAndar.add(lblAguardando);
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
        JPanel painelGeral = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelGeral.setBackground(new Color(240, 248, 255));
        painelGeral.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel lblTotal = new JLabel("📊 Total Transportado: " + heuristica.getTotalPessoasTransportadas());
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(new Color(0, 100, 0));
        int hora = (minutoAtual / 60) % 24;
        int minutos = minutoAtual % 60;
        JLabel lblTempoAtual = new JLabel("🕐 Tempo: " + String.format("%02d:%02d", hora, minutos));
        lblTempoAtual.setFont(new Font("Arial", Font.BOLD, 14));
        lblTempoAtual.setForeground(new Color(100, 0, 100));
        painelGeral.add(lblTotal);
        painelGeral.add(Box.createHorizontalStrut(20));
        painelGeral.add(lblTempoAtual);
        painelEstatisticas.add(painelGeral);
        JPanel painelElevadores = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelElevadores.setBackground(new Color(240, 248, 255));
        painelElevadores.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        Lista<Elevador> elevadores = predio.getCentral().getElevadores();
        Ponteiro<Elevador> pElev = elevadores.getInicio();
        while (pElev != null) {
            Elevador elevador = pElev.getElemento();
            JPanel painelElev = new JPanel();
            painelElev.setLayout(new BoxLayout(painelElev, BoxLayout.Y_AXIS));
            painelElev.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 150), 2));
            painelElev.setBackground(new Color(245, 245, 255));
            JLabel lblElev = new JLabel("🛗 Elevador " + elevador.getId() + ": " + elevador.getTotalPessoasTransportadas() + " pessoas");
            lblElev.setFont(new Font("Arial", Font.BOLD, 12));
            lblElev.setForeground(new Color(0, 0, 150));
            lblElev.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelElev.add(lblElev);
            painelElevadores.add(painelElev);
            painelElevadores.add(Box.createHorizontalStrut(10));
            pElev = pElev.getProximo();
        }
        painelEstatisticas.add(painelElevadores);
        painelEstatisticas.revalidate();
        painelEstatisticas.repaint();
        SwingUtilities.invokeLater(() -> {
            painelEstatisticas.setVisible(true);
            painelEstatisticas.getParent().setVisible(true);
        });
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
            SimuladorGUI gui = new SimuladorGUI(5, 2, 1000);
            gui.setVisible(true);
        });
    }
} 