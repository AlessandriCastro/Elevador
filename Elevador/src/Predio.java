

public class Predio extends EntidadeSimulavel {
    private CentralDeControle central;
    private Lista andares;

    public Predio(int quantidadeAndares, int quantidadeElevadores) {
        central = new CentralDeControle(quantidadeElevadores);
        andares = new Lista();
        for (int i = 0; i < quantidadeAndares; i++) {
            andares.inserirFim(new Andar(i));
        }
    }

    @Override
    public void atualizar(int minutoSimulado) {
        central.atualizar(minutoSimulado);
    }

    public CentralDeControle getCentral() {
        return central;
    }
}