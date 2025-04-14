

public class Elevador extends EntidadeSimulavel {
    private int id;

    public Elevador(int id) {
        this.id = id;
    }

    @Override
    public void atualizar(int minutoSimulado) {
        System.out.println("Elevador " + id + " processando minuto " + minutoSimulado);
    }
}