import java.io.Serializable;

public class Pessoa implements Serializable {
    private int id;
    private int andarOrigem;
    private int andarDestino;
    private int tipoPrioridade; // 0: normal, 1: idoso, 2: cadeirante
    private int minutoEntrada;
    private int minutoSaida;
    private int tempoViagem;

    public Pessoa(int id, int andarOrigem, int andarDestino, int tipoPrioridade) {
        this.id = id;
        this.andarOrigem = andarOrigem;
        this.andarDestino = andarDestino;
        this.tipoPrioridade = tipoPrioridade;
        this.minutoEntrada = -1;
        this.minutoSaida = -1;
        this.tempoViagem = 0;
    }

    public int getId() {
        return id;
    }

    public int getAndarOrigem() {
        return andarOrigem;
    }

    public int getAndarDestino() {
        return andarDestino;
    }

    public int getTipoPrioridade() {
        return tipoPrioridade;
    }

    public void entrarElevador(int minutoAtual) {
        this.minutoEntrada = minutoAtual;
        System.out.printf("DEBUG: Pessoa %d entrou no elevador no minuto %d\n", id, minutoAtual);
    }

    public void sairElevador(int minutoAtual) {
        this.minutoSaida = minutoAtual;
        if (this.minutoEntrada >= 0) {
            this.tempoViagem = this.minutoSaida - this.minutoEntrada;
            System.out.printf("DEBUG: Pessoa %d - Entrada: %d, Saída: %d, Tempo: %d minutos\n", 
                id, minutoEntrada, minutoSaida, tempoViagem);
        }
    }

    public int getTempoViagem() {
        return tempoViagem;
    }

    @Override
    public String toString() {
        String tipo = tipoPrioridade == 2 ? "Cadeirante" : 
                     tipoPrioridade == 1 ? "Idoso" : "Normal";
        return String.format("Pessoa %d (%s)", id, tipo);
    }
}

