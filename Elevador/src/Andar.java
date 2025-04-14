import java.io.Serializable;

public class Andar implements Serializable {
    private int numero;
    private Fila pessoasAguardando;
    private PainelElevador painel;

    public Andar(int numero) {
        this.numero = numero;
        this.pessoasAguardando = new Fila();
        this.painel = new PainelElevador();
    }

    public int getNumero() {
        return numero;
    }

    public Fila getPessoasAguardando() {
        return pessoasAguardando;
    }

    public PainelElevador getPainel() {
        return painel;
    }
}