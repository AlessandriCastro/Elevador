
public class Predio extends EntidadeSimulavel {
    private CentralDeControle central;
    private Lista<Andar> andares;

    public Predio(int quantidadeAndares, int quantidadeElevadores) {

        andares = new Lista<>();

        for (int i = 0; i < quantidadeAndares; i++) {
            andares.inserirFim(new Andar(i));
        }
        int andarMaximo = quantidadeAndares - 1;
        central = new CentralDeControle(quantidadeElevadores, andarMaximo, this);
    }

    @Override
    public void atualizar(int minutoSimulado) {
        central.atualizar(minutoSimulado);
    }

    public CentralDeControle getCentral() {
        return central;
    }

    public Lista<Andar> getAndares() {
        return andares;
    }
    public Andar getAndar(int numero) {
        Ponteiro<Andar> atual = andares.getInicio();
        while (atual != null) {
            Andar andar = atual.getElemento();
            if (andar.getNumero() == numero) {
                return andar;
            }
            atual = atual.getProximo();
        }
        return null; // Se não encontrar
    }
    
}
