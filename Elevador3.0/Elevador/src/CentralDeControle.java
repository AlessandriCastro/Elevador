public class CentralDeControle extends EntidadeSimulavel {
    private Lista<Elevador> elevadores;
    private Heuristica heuristica;
    private Predio predio;


    public CentralDeControle(int quantidadeElevadores, int andarMaximo, Predio predio) {
        this.predio = predio;
        elevadores = new Lista<>();
        for (int i = 0; i < quantidadeElevadores; i++) {
            elevadores.add(new Elevador(i, 5, andarMaximo, predio), 0); // Corrigido: adiciona novo elevador
        }
        this.heuristica = new Heuristica(predio, elevadores);
    }



    @Override
    public void atualizar(int minutoSimulado) {
        heuristica.distribuirChamadas();
//       Atualiza os elevadores
        Ponteiro<Elevador> p = elevadores.getInicio();
        while (p != null) {
            Elevador e = p.getElemento();
            e.atualizar(minutoSimulado);
            p = p.getProximo();
        }
    }

    public Lista<Elevador> getElevadores() {
        return elevadores;
    }

    public Heuristica getHeuristica() {
        return heuristica;
    }
}
