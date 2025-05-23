import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class Simulador implements Serializable {
    private int minutoSimulado;
    private int velocidadeEmMs;
    private transient Timer timer;
    private boolean emExecucao;
    private Predio predio;

    public Simulador(int andares, int elevadores, int velocidadeEmMs) {
        this.minutoSimulado = 0;
        this.velocidadeEmMs = velocidadeEmMs;
        this.predio = new Predio(andares, elevadores);
        this.timer = new Timer();
    }

    public void iniciar() {
        if (emExecucao) return;
        emExecucao = true;
        iniciarTimer();
        System.out.println("Simulação iniciada.");
    }

    public void pausar() {
        if (timer != null) {
            timer.cancel();
            emExecucao = false;
            System.out.println("Simulação pausada.");
        }
    }

    public void continuar() {
        if (!emExecucao) {
            iniciarTimer();
            emExecucao = true;
            System.out.println("Simulação retomada.");
        }
    }

    public Predio getPredio() {
        return predio;
    }

    public void encerrar() {
        if (timer != null) timer.cancel();
        emExecucao = false;
        System.out.println("Simulação encerrada.");
    }

    private void iniciarTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (minutoSimulado >= 1440) { // 24 horas * 60 minutos
                    pausar();
                    return;
                }
                System.out.println("DEBUG: Minuto atual: " + minutoSimulado + " (Hora: " + (minutoSimulado / 60) + ")");
                predio.atualizar(minutoSimulado);
                minutoSimulado++;
            }
        }, 0, velocidadeEmMs);
    }

    public void agendarPessoa(Pessoa pessoa, long atrasoMs) {
        if (timer == null) return;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int origem = pessoa.getAndarOrigem();
                predio.getAndar(origem).adicionarPessoa(
                    pessoa.getId(),
                    pessoa.getAndarOrigem(),
                    pessoa.getAndarDestino(),
                    pessoa.getTipoPrioridade()
                );
            }
        }, atrasoMs);
    }

    public void gravar(String nomeArquivo) {
        try {
            // Verifica se a simulação já completou um dia
            if (minutoSimulado < 1440) {
                System.out.println("\n⚠️ A simulação ainda não completou um dia completo (24 horas)");
                System.out.println("⏰ Minutos simulados até agora: " + minutoSimulado);
                System.out.println("⏳ Aguarde a simulação completar 1440 minutos (24 horas)");
                return;
            }

            // Pausa a simulação antes de gravar
            boolean estavaEmExecucao = emExecucao;
            if (estavaEmExecucao) {
                pausar();
            }

            // Obtém o caminho completo do arquivo
            File arquivo = new File(nomeArquivo);
            String caminhoCompleto = arquivo.getAbsolutePath();

            // Grava a simulação
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(arquivo));
            out.writeObject(this);
            out.close();
            
            System.out.println("\n✅ Simulação gravada com sucesso!");
            System.out.println("📁 Arquivo: " + caminhoCompleto);
            System.out.println("⏰ Tempo simulado: 24 horas (1440 minutos)");
            System.out.println("👥 Total de pessoas transportadas: " + predio.getCentral().getHeuristica().getTotalPessoasTransportadas());
            
            // Retoma a simulação se estava em execução
            if (estavaEmExecucao) {
                continuar();
            }
            
        } catch (IOException e) {
            System.err.println("\n❌ Erro ao gravar simulação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Simulador carregar(String nomeArquivo) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(nomeArquivo));
            Simulador sim = (Simulador) in.readObject();
            in.close();
            
            System.out.println("\n✅ Simulação carregada com sucesso!");
            System.out.println("📁 Arquivo: " + nomeArquivo);
            System.out.println("⏰ Tempo simulado: 24 horas (1440 minutos)");
            System.out.println("👥 Total de pessoas transportadas: " + sim.getPredio().getCentral().getHeuristica().getTotalPessoasTransportadas());
            
            return sim;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Erro ao carregar simulação: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}