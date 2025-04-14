

public class CentralDeControle extends EntidadeSimulavel {
    private Lista elevadores;

    public CentralDeControle(int quantidadeElevadores) {
        elevadores = new Lista();
        for (int i = 0; i < quantidadeElevadores; i++) {
            elevadores.inserirFim(new Elevador(i + 1));
        }
    }

    @Override
    public void atualizar(int minutoSimulado) {
        Ponteiro p = elevadores.getInicio();
        while (p != null) {
            Elevador e = (Elevador) p.getElemento();
            e.atualizar(minutoSimulado);
            p = p.getProximo();
        }
    }

    public Lista getElevadores() {
        return elevadores;
    }
}