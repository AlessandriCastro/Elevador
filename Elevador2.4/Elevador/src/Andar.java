import java.io.Serializable;

public class Andar implements Serializable {
    private int numero;
    private Fila<Pessoa> pessoasAguardando;
    private PainelElevador painel;

    public Andar(int numero) {
        this.numero = numero;
        this.pessoasAguardando = new Fila<>();
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
    public void adicionarPessoa(Pessoa pessoa) {
        pessoasAguardando.enqueue(pessoa);

        if (pessoa.getAndarDestino() > this.numero) {
            painel.pressionarSubir();
        } else if (pessoa.getAndarDestino() < this.numero) {
            painel.pressionarDescer();
        }

    }



}