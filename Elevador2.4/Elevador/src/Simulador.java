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
                if (minutoSimulado >= 18) { //pause em 18 min
                    pausar();
                    return;
                    
                }
                predio.atualizar(minutoSimulado++);
            }
        }, 0, velocidadeEmMs);
    }
    public void agendarPessoa(Pessoa pessoa, long atrasoMs) {
        if (timer == null) return;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int origem = pessoa.getAndarOrigem();
                predio.getAndar(origem).adicionarPessoa(pessoa);
               // System.out.println("Pessoa " + pessoa.getId() + " adicionada ao andar " + origem + " após " + atrasoMs + "ms.");
            }
        }, atrasoMs);
    }


    public void gravar(String nomeArquivo) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            out.writeObject(this);
            System.out.println("Simulação gravada em: " + nomeArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Simulador carregar(String nomeArquivo) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(nomeArquivo))) {
            Simulador sim = (Simulador) in.readObject();
            sim.continuar();
            return sim;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}